package tessaract;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;

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
	
	private static final Set<String> SUPPORTED_TYPES = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList(new String[] {
                    MediaType.image("png").toString(), MediaType.image("tiff").toString(), MediaType.image("x-ms-bmp").toString()
            })));
	
	@Override
	protected boolean isAllowedType(String type) {
		if (type == null) {
			return false;
		}
		
		return SUPPORTED_TYPES.contains(type);
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
