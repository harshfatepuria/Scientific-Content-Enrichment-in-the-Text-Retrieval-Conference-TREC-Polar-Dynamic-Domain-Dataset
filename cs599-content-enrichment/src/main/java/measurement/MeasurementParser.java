package measurement;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import edu.stanford.nlp.ie.NumberNormalizer;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import shared.TikaExtractedTextBasedParser;
import sweet.SweetOntology;

/**
 * A parser to extract measurement from document text
 * The parser will employ TagRatioParser to get relevant text. 
 * Then tokenizes the text to get number represented in the text.
 * The parser then extract the number and its two following word tokens
 * and try to match them with Units concept defined in SWEET Ontology and some predefined symbols 
 *
 */
public class MeasurementParser extends TikaExtractedTextBasedParser {

	/**
	 * 
	 */
	private SweetOntology sweet;
	
	public MeasurementParser() throws Exception {
		sweet = SweetOntology.getInstance();
	}
	
	private boolean extractDumpData = false;
	
	
	/**
	 * get whether the parser should include its dump data in the result files
	 * @return
	 */
	public boolean isExtractDumpData() {
		return extractDumpData;
	}

	/**
	 * Tell the parser whether it should include its dump data in the result files
	 * Dump data maybe useful for observation to improve extraction performance
	 * @param extractDumpData
	 */
	public void setExtractDumpData(boolean extractDumpData) {
		this.extractDumpData = extractDumpData;
	}

	private static final long serialVersionUID = 664156334712200733L;

	@Override
	public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context)
			throws IOException, SAXException, TikaException {
//		String sampleText = "This place is twenty two k.m. long and 2,400 metra high. \n Its average temperature is 60 degree F but sometimes can be up to 37.5 degree celsius. It is 0 tolerance.";
//		String text = sampleText;
		String text = getTextFromTagRatioParser(stream, metadata, context);
		
		
		if (text != null && text.length() > 0) {
			List<String> tokens = tokenize(text);
			List<BigDecimal> nums = extractNumbers(tokens);
			
			List<StringAndNumber> fixed = fixAdjacentNumber(tokens, nums);
			tokens = fixed.stream().map(StringAndNumber::getString).collect(Collectors.toList());
			nums = fixed.stream().map(StringAndNumber::getNumber).collect(Collectors.toList());
			
			List<Number3Gram> extractedTuples = extractNumbers3Gram(tokens, nums);
			
			for(Number3Gram n3gram : extractedTuples) {
				String extractedMeasurement = sweet.matchMeasurement(n3gram.post1, n3gram.post2);
				if(extractedMeasurement != null) {
					metadata.add("measurement_value", n3gram.number.toString());
					metadata.add("measurement_unit", extractedMeasurement);
					
					String extractedText = n3gram.number.toString() + " " + extractedMeasurement;
					metadata.add("measurement_extractedText", extractedText);
					if (isExtractDumpData()) {
						metadata.add("measurement_dump", n3gram.toString() + " | " + extractedMeasurement);
					}
				} else {
					if (isExtractDumpData()) {
						metadata.add("measurement_dump", n3gram.toString());
					}
				}
			}
		}
		
		XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
        xhtml.startDocument();
        xhtml.endDocument();
	}
	
	
	private String getTextFromTagRatioParser(InputStream stream, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
		TagRatioParser parser = new TagRatioParser();
		BodyContentHandler handler = new BodyContentHandler();
		parser.parse(stream, handler, metadata, context);
		
		return handler.toString();
	}
	
	/**
	 * Do tokenization using Stanford CoreNLP DocumentPreprocessor 
	 * @param text
	 * @return
	 */
	private List<String> tokenize(String text) {
		List<String> list = new ArrayList<>();
		
		DocumentPreprocessor dp = new DocumentPreprocessor(new StringReader(text));
		for (List<HasWord> sentence : dp) {
	        for(HasWord h : sentence) {
	        	list.add(h.word());
	        }
		}
		
		return list;
	}
	
	/**
	 * Extract number from each token using Stanford CoreNLP NumberNormalizer
	 * @param tokens
	 * @return
	 */
	private List<BigDecimal> extractNumbers(List<String> tokens) {
		List<BigDecimal> result = new ArrayList<>();
		
		if (tokens.size() == 1) {
			BigDecimal num = convertToNumber(tokens.get(0), "");
			result.add(num);
		} else if (tokens.size() >= 1) {
			for(int i = 0; i < tokens.size() - 1; i++) {
				BigDecimal num = convertToNumber(tokens.get(i), tokens.get(i+1));
				result.add(num);
			}
			
			BigDecimal num = convertToNumber(tokens.get(tokens.size()-1), "");
			result.add(num);
		}
		
		return result;
	}
	
	/**
	 * Combined adjacent tokens that are consider numbers
	 * These token maybe parts of only one number so it should be treated as one token
	 * @param tokens
	 * @param nums
	 * @return
	 */
	private List<StringAndNumber> fixAdjacentNumber(List<String> tokens, List<BigDecimal> nums) {
		int i = 0;
		List<StringAndNumber> result = new ArrayList<>();
		while(i < tokens.size()) {
			if (nums.get(i) == null) {
				result.add(new StringAndNumber(tokens.get(i) , null));
				i++;
			} else {
				String numString = tokens.get(i);
				BigDecimal num = nums.get(i);
				boolean extended = false;
				
				i++;
				while(i < nums.size() && nums.get(i) != null) {
					numString += " " + tokens.get(i);
					extended = true;
					i++;
				}
				
				if (extended) {
					num = convertToNumber(numString, i < nums.size() -1 ? tokens.get(i) : "");
				}
				
				result.add(new StringAndNumber(numString, num));
			}
		}
		
		return result;
	}
	
	/**
	 * Extracted number and its two following tokens
	 * @param tokens
	 * @param nums
	 * @return
	 */
	private List<Number3Gram> extractNumbers3Gram(List<String> tokens, List<BigDecimal> nums) {
		List<Number3Gram> result = new ArrayList<>();
		
		if (tokens.size() == 1) {
			BigDecimal num = nums.get(0);
			
			if (num != null) {
				result.add(new Number3Gram(num, tokens.get(0), "", ""));
			}
		} else if (tokens.size() == 2) {
			BigDecimal num = nums.get(0);
			
			if (num != null) {
				result.add(new Number3Gram(num, tokens.get(0), tokens.get(1), ""));
			}
			
			num = nums.get(1);
			if (num != null) {
				result.add(new Number3Gram(num, tokens.get(1), "", ""));
			}
		} else {
			for(int i = 0; i < tokens.size() - 2; i++) {
				BigDecimal num = nums.get(i);
				if (num != null) {
					result.add(new Number3Gram(num, tokens.get(i), tokens.get(i+1), tokens.get(i+2)));
				}
			}
			
			int i = tokens.size() - 2;
			BigDecimal num = nums.get(i);
			if (num != null) {
				result.add(new Number3Gram(num, tokens.get(i), tokens.get(i+1), ""));
			}
			
			i++;
			num = nums.get(i);
			if (num != null) {
				result.add(new Number3Gram(num, tokens.get(i), "", ""));
			}
			
		}
		
		return result;
	}
	
	private BigDecimal convertToNumber(String word) {
		try {
			Number n = NumberNormalizer.wordToNumber(word);
			return new BigDecimal(n.toString());
		} catch (Throwable e) {
			return null;
		}
	}
	
	private BigDecimal convertToNumber(String word, String nextWord) {
		return convertToNumber(word);
	}
	
	
	private class Number3Gram {
		BigDecimal number;
		String numberString;
		String post1;
		String post2;
		
		Number3Gram(BigDecimal number, String actualNumberString, String post1, String post2) {
			this.numberString = actualNumberString;
			this.post1 = post1;
			this.post2 = post2;
			this.number = number;
		}
		
		@Override
		public String toString() {
			return String.format("%s %s %s | %s", numberString, post1, post2, number.toString());
		}
	}

	private class StringAndNumber {
		String string;
		BigDecimal number;
		
		public StringAndNumber(String s, BigDecimal num) {
			this.string = s;
			this.number = num;
		}
		
		@Override
		public String toString() {
			return String.format("%s %s", string, number == null ? "null" : number.toString());
		}

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}

		public BigDecimal getNumber() {
			return number;
		}

		public void setNumber(BigDecimal number) {
			this.number = number;
		}
		
	}
}
