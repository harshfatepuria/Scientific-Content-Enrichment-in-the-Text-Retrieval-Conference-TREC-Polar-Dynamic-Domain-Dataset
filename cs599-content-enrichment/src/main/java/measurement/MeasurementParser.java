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
		List<Number3Gram> nums = extractNumbers(tokens);
		
		nums.forEach(n -> System.out.println(n));
		
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
	
	private List<Number3Gram> extractNumbers(List<String> tokens) {
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
	
	private BigDecimal getNumber(String word, String nextWord) {
		try {
			return new BigDecimal(word);
		} catch(Exception e) {
			
		}
		
		String num = QuantifiableEntityNormalizer.normalizedNumberString(word, nextWord, null);
		if (num == null || "0.0".equals(num)) {
			return null;
		}
		
		return new BigDecimal(num);
	}
	
	private class Number3Gram {
		BigDecimal number;
		String pre;
		String numberString;
		String post;
		
		Number3Gram(BigDecimal number, String actualNumberString, String pre, String post) {
			this.numberString = actualNumberString;
			this.pre = pre;
			this.post = post;
			this.number = number;
		}
		
		@Override
		public String toString() {
			return String.format("%s %s %s", pre, numberString, post);
		}
	}

}
