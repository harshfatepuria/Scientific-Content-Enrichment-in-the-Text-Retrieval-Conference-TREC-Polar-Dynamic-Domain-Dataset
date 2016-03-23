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
	private String markerFile;
	private boolean overwriteResult = false;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();;
	
	private URI baseFolderUri;
	
	public List<String> runParser() throws IOException, Exception {
		List<String> successPath = new ArrayList<>();
		final FileMarker fileMarker = markerFile == null ? null :  new FileMarker(new File(markerFile));
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
	            try {
	                Thread.sleep(200);
	                System.out.println("Shouting down ...");

	                if (fileMarker != null) {
	                	fileMarker.closeWriter();
	                	System.out.println("Marker:: " + fileMarker.getMarkSize() + " files marked");
	                }

	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
		});
		
		try {
			Files.walk(Paths.get(baseFolder))
			.filter(Files::isRegularFile)
			.forEach(path -> {
				File resultFile = getResultFile(path);
				String relativePath = getRelativePath(path);
				
				if (fileMarker != null && fileMarker.exists(relativePath) && !overwriteResult) {
//					System.out.println("skip " + relativePath);
					return;
				}
				
				try {
					if (overwriteResult || !resultFile.exists()) {
						boolean success = parse(path, resultFile);
						if (success) {
							successPath.add(relativePath);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				
				try {
					if(fileMarker != null && !fileMarker.exists(relativePath)) {
						fileMarker.mark(relativePath);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				
//				System.out.println("finish " + relativePath);
				
			});
		} finally {
			fileMarker.closeWriter();
		}
		return successPath;
	}
	
	protected File getResultFile(Path path) {
		return getResultFile(path, "");
	}
	
	protected File getResultFile(Path path, String suffix) {
		return new File(resultFolder, getRelativePath(path) + suffix);
	}
	/*
	protected File getMarkerFile(Path path) {
		if (markerFile != null) {
			return new File(markerFile, getRelativePath(path));
		}
		return null;
	}
	*/
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
	public String getMarkerFile() {
		return markerFile;
	}
	public void setMarkerFile(String markerFile) {
		this.markerFile = markerFile;
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
