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

public class MeasurementParser extends TikaExtractedTextBasedParser {

	/**
	 * 
	 */
	private SweetOntology sweet;
	
	public MeasurementParser() throws Exception {
		sweet = SweetOntology.getInstance();
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
					metadata.add("measurement_extracted", n3gram.number.toString() + " " + extractedMeasurement);
					metadata.add("measurement_verifyDump", n3gram.toString() + " | " + extractedMeasurement);
				} else {
					metadata.add("measurement_verifyDump", n3gram.toString());
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
		} catch (Exception e) {
			return null;
		}
	}
	
	private BigDecimal convertToNumber(String word, String nextWord) {
		return convertToNumber(word);
	}
	
	/*
	private Set<String> confuseWords = new HashSet<>(Arrays.asList("for", "our", "height", "file"));
	private BigDecimal convertToNumber(String word, String nextWord) {
		
		if (NumberUtils.isNumber(word)) {
			try {
				return new BigDecimal(word);
			} catch (Exception e) {
				return BigDecimal.valueOf(NumberUtils.toDouble(word));
			}
		}
		
		if ("zero".equalsIgnoreCase(word)) {
			return BigDecimal.ZERO;
		}
	
		if (confuseWords.contains(word.toLowerCase())) {
			return null;
		}
		
		if (!"eight".equalsIgnoreCase(word) && word.toLowerCase().indexOf("ight") == 1) {
			return null;
		}
		
		if (!"four".equalsIgnoreCase(word) && word.toLowerCase().indexOf("our") == 1) {
			return null;
		}
		
		String num = QuantifiableEntityNormalizer.normalizedNumberString(word, nextWord, null);
		if (num == null || "0.0".equals(num)) {
			return null;
		}
		
		try {
			return new BigDecimal(num);
		} catch (Exception e) {
			System.out.println(num);
			e.printStackTrace();
			return null;
		}
	}
	*/
	
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
