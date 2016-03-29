package tessaract;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;

import org.apache.tika.Tika;

import shared.AbstractTypeLimitParserRunner;

public class TesseractOCRParserRunner extends AbstractTypeLimitParserRunner {

	
	public TesseractOCRParserRunner(String baseFolder, String resultFolder) throws Exception {
		this(baseFolder, resultFolder, null);
	}
	
	public TesseractOCRParserRunner(String baseFolder, String resultFolder, String markerFile) throws Exception {
		setBaseFolder(baseFolder);
		setResultFolder(resultFolder);
		setMarkerFile(markerFile);
		initializeParser();
	}
	
	private Tika tika;
	private void initializeParser() {
		tika = new Tika();
	}
	
	@Override
	protected boolean isAllowedType(String type) {
		if (type == null) {
			return false;
		}
		return type.startsWith("image/");
	}

	@Override
	protected boolean parse(Path path, File resultFile) throws Exception {
		String text = tika.parseToString(path);
		text = text.trim();
		
		if (text.length() > 0) {
			resultFile.getParentFile().mkdirs();
			try(PrintWriter pw = new PrintWriter(new FileWriter(resultFile))) {
				pw.print(text);
			}
		}
		
		return true;
	}
	
	@Override
	protected File getResultFile(Path path) {
		return super.getResultFile(path, ".tesseract");
	}

}
