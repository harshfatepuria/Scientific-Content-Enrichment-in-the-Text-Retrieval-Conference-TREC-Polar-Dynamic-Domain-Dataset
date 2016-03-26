package measurement;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import edu.stanford.nlp.ie.QuantifiableEntityNormalizer;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import shared.TikaExtractedTextBasedParser;

public class MeasurementParser extends TikaExtractedTextBasedParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 664156334712200733L;

	@Override
	public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context)
			throws IOException, SAXException, TikaException {
		String text = "This place is twenty-two km long and 2,400 metre high. \n Its average temperature is 60 F but sometimes can be up to 37.5 degree celcius. It is 0 tolerance.";
		
		List<String> tokens = tokenize(text);
		List<BigDecimal> nums = extractNumbers(tokens);
		List<Number3Gram> extractedTuples = extractNumbers3Gram(tokens, nums);
		
		extractedTuples.forEach(n -> System.out.println(n));
		
		XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
        xhtml.startDocument();
        xhtml.endDocument();
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
			BigDecimal num = getNumber(tokens.get(0), "");
			result.add(num);
		} else if (tokens.size() >= 1) {
			for(int i = 0; i < tokens.size() - 1; i++) {
				BigDecimal num = getNumber(tokens.get(i), tokens.get(i+1));
				result.add(num);
			}
			
			BigDecimal num = getNumber(tokens.get(tokens.size()-1), "");
			result.add(num);
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
	
	/*
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
					num = getNumber(numString, i < nums.size() -1 ? tokens.get(i) : "");
				}
				
				result.add(new StringAndNumber(numString, num));
			}
		}
		
		return result;
	}
	*/
	
	/*
	private List<Number3Gram> extractNumbers1(List<String> tokens) {
		List<Number3Gram> result = new ArrayList<>();
		
		if (tokens.size() == 1) {
			BigDecimal num = getNumber(tokens.get(0), "");
			if (num != null) {
				result.add(new Number3Gram(num, tokens.get(0), "", ""));
			}
		} else if (tokens.size() >= 1) {
			BigDecimal num = getNumber(tokens.get(0), tokens.get(1));
			if (num != null) {
				result.add(new Number3Gram(num, tokens.get(0), "", tokens.get(1)));
			}
			
			for(int i = 1; i < tokens.size() - 1; i++) {
				num = getNumber(tokens.get(i), tokens.get(i+1));
				if (num != null) {
					result.add(new Number3Gram(num, tokens.get(i), tokens.get(i-1), tokens.get(i+1)));
				}
			}
			
			num = getNumber(tokens.get(tokens.size()-1), "");
			if (num != null) {
				result.add(new Number3Gram(num, tokens.get(tokens.size()-1), tokens.get(tokens.size()-2), ""));
			}
		}
		
		return result;
	}
	*/
	
	private BigDecimal getNumber(String word, String nextWord) {
		try {
			return new BigDecimal(word);
		} catch(Exception e) {
			
		}
		
		if ("zero".equalsIgnoreCase(word)) {
			return BigDecimal.ZERO;
		}
		
		String num = QuantifiableEntityNormalizer.normalizedNumberString(word, nextWord, null);
		if (num == null || "0.0".equals(num)) {
			return null;
		}
		
		return new BigDecimal(num);
	}
	
	/*
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
			return String.format("%s %s %s : %s", numberString, post1, post2, number.toString());
		}
	}

}
