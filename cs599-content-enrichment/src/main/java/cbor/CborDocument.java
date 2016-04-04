package cbor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class CborDocument {
	String url;
	Response response;
	String key;
	String imported;
	String fileName;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("url=" + url + "\n");
		sb.append("body=\n");
		sb.append(response.body);
		
		return sb.toString();
	}
	
	public InputStream getInputStream() throws UnsupportedEncodingException {
		InputStream stream = new ByteArrayInputStream(response.body.getBytes("UTF-8"));
		return stream;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fname) {
		this.fileName = fname;
	}
	
	public String getRelativePath() {
		String[] tokens = key.split("_");
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < tokens.length - 1; i++) {
			sb.append(tokens[i]);
			sb.append("/");
		}
		
		sb.append(getFileName());
		
		return sb.toString();
	}
}

class Response {
	List<String[]> headers;
	String body;
}