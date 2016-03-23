package shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractParserRunner {
	private String baseFolder;
	private String resultFolder;
	private boolean overwriteResult = false;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();;
	
	private URI baseFolderUri;
	
	public List<String> runParser() throws IOException, Exception {
		List<String> successPath = new ArrayList<>();
		
		Files.walk(Paths.get(baseFolder))
		.filter(Files::isRegularFile)
		.forEach(path -> {
			File resultFile = getResultFile(path);
			
			if (!overwriteResult && resultFile.exists()) {
				return;
			}
			
			try {
				boolean success = parse(path, resultFile);
				if (success) {
					successPath.add(getRelativePath(path));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		});
		
		return successPath;
	}
	
	protected File getResultFile(Path path) {
		return getResultFile(path, "");
	}
	
	protected File getResultFile(Path path, String suffix) {
		return new File(resultFolder, getRelativePath(path) + suffix);
	}
	
	protected abstract boolean parse(Path path, File resultFile) throws Exception;
	
	protected InputStream getInputStream(Path path) throws FileNotFoundException {
		return new FileInputStream(path.toFile());
	}
	
	protected String getRelativePath(Path path) {
		return baseFolderUri.relativize(path.toUri()).toString();
	}
	
	public String getBaseFolder() {
		return baseFolder;
	}
	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
		baseFolderUri = Paths.get(baseFolder).toUri();
	}
	public String getResultFolder() {
		return resultFolder;
	}
	public void setResultFolder(String resultFolder) {
		this.resultFolder = resultFolder;
	}
	public boolean isOverwriteResult() {
		return overwriteResult;
	}
	public void setOverwriteResult(boolean overwriteResult) {
		this.overwriteResult = overwriteResult;
	}
	public Gson getGson() {
		return gson;
	}
	public void setGson(Gson gson) {
		this.gson = gson;
	}
	
	
}
