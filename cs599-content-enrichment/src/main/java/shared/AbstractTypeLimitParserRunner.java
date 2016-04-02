package shared;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.tika.Tika;

/**
 * Subclass of AbstractParserRunner
 * provide a method to limit parsing to some file type
 *
 */
public abstract class AbstractTypeLimitParserRunner extends AbstractParserRunner {

	private Tika tika = new Tika();
	
	protected abstract boolean isAllowedType(String type);
	
	@Override
	protected boolean isAllowProcessing(Path path) {
		try {
			String type = tika.detect(path);
			return isAllowedType(type);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
