package shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cbor.CborDocument;
import cbor.CborReader;

/**
 * Utility class to run specific parsers to all the documents in a folder.
 * 
 */
public abstract class AbstractParserRunner {
	private String baseFolder;
	private String resultFolder;
	private String markerFile;
	private boolean overwriteResult = false;
	private Long fileSizeLimit = Long.valueOf(2*1024*1024);
	private boolean cborFormat;
	
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	private URI baseFolderUri;
	
	/**
	 * Run the parser to all documents in the folder according to setting
	 * @return list of path that are parsed successfully
	 * @throws IOException
	 * @throws Exception
	 */
	public List<String> runParser() throws IOException, Exception {
		List<String> successPath = new ArrayList<>();
		final FileMarker fileMarker = markerFile == null ? null :  new FileMarker(new File(markerFile));
		
		String[] lastProcess = new String[]{""};
		int[] count = new int[]{0, 0};
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
	            try {
	                Thread.sleep(200);
	                System.out.println("Shutting down ...");

	                if (fileMarker != null) {
	                	fileMarker.closeWriter();
	                	System.out.println("Marker:: " + fileMarker.getMarkSize() + " files marked");
	                	
	                	System.out.println("Runner:: " + count[0] + " files processed");
	                	System.out.println("Runner:: " + count[1] + " files skipped");
	                	System.out.println("Runner:: LastProcessingPath = " +lastProcess[0]);
	                }

	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
		});
		
		Files.walk(Paths.get(baseFolder)).filter(Files::isRegularFile).forEach(path -> {
			if (!isAllowProcessing(path)) {
				return;
			}
			
			if (getFileSizeLimit() != null && path.toFile().length() > getFileSizeLimit()) {
				return;
			}
			
			String relativePath = getRelativePath(path);
			CborDocument cborDoc = null;
			
			if (isDocumentsInCborFormat()) {
				try {
					cborDoc = CborReader.read(path.toFile());
					relativePath = cborDoc.getRelativePath();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			File resultFile = getResultFile(relativePath);

			lastProcess[0] = relativePath;
			
			if (count[0] % 2000 == 0 && count[0] != 0) {
				System.out.println("Runner:: " + count[0] + " files processed");
			}

			if (fileMarker != null && fileMarker.exists(relativePath) && !overwriteResult) {
				count[0]++;
				count[1]++;
				return;
			}

			try {
				if (overwriteResult || !resultFile.exists()) {
					boolean success = false;
					
					if (cborDoc != null) {
						success = parse(path, relativePath, resultFile, cborDoc);
						
						if (resultFile.exists()) {
							File out = new File(getResultFolder(), cborDoc.getRelativePath());
							out.getParentFile().mkdirs();
							FileOutputStream fo = new FileOutputStream(out);
							IOUtils.copy(cborDoc.getInputStream(), fo);
						}						
					} else {
						success = parse(path, relativePath, resultFile);
					}
					
					if (success) {
						successPath.add(relativePath);
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}

			try {
				if (fileMarker != null && !fileMarker.exists(relativePath)) {
					fileMarker.mark(relativePath);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			count[0]++;
		});
		
		if (fileMarker != null) {
			fileMarker.closeWriter();
		}
		
		return successPath;
	}
	
	/**
	 * get file object that represent the result file of a specific document path
	 * @param path
	 * @return file object that represent the result file
	 */
	protected File getResultFile(String relativePath) {
		return getResultFile(relativePath, "");
	}
	
	/**
	 * get file object that represent the result file of a specific document path by append a suffix
	 * @param path
	 * @return file object that represent the result file
	 */
	protected File getResultFile(String relativePath, String suffix) {
		return new File(resultFolder, relativePath + suffix);
	}	
	
	protected boolean parse(Path path, String relativePath, File resultFile) throws Exception {
		return parse(path, relativePath, resultFile, null);
	}
	
	/**
	 * Abstract method, for parsing a document
	 * @param path path to the documents
	 * @param relativePath relative path to base folder
	 * @param resultFile file object that represent the result file
	 * @param cborDoc object representing CBOR document, null if the document is not in CBOR format
	 * @return true if the file is parsed successfully 
	 * @throws Exception
	 */
	protected abstract boolean parse(Path path, String relativePath, File resultFile, CborDocument cborDoc) throws Exception;
		
	/**
	 * Get the file size limit in bytes (default: 2MB)
	 * @return file size limit in bytes
	 */
	public Long getFileSizeLimit() {
		return fileSizeLimit;
	}

	/**
	 * Set the file size limit, unlimited if set to null
	 * @param fileSizeLimit
	 */
	public void setFileSizeLimit(Long fileSizeLimit) {
		this.fileSizeLimit = fileSizeLimit;
	}
	
	/**
	 * Get the base folder of documents to be parsed
	 * @return base folder
	 */
	public String getBaseFolder() {
		return baseFolder;
	}
	
	/**
	 * Set the base folder of documents to be parsed
	 * @param baseFolder
	 */
	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
		baseFolderUri = Paths.get(baseFolder).toUri();
	}
	
	/**
	 * Get the base folder to keep the result files
	 * @return
	 */
	public String getResultFolder() {
		return resultFolder;
	}
	
	/**
	 * Set the base folder to keep the result files
	 * @param resultFolder
	 */
	public void setResultFolder(String resultFolder) {
		this.resultFolder = resultFolder;
	}
	
	/**
	 * Get the marker file
	 * @return
	 */
	public String getMarkerFile() {
		return markerFile;
	}
	
	/**
	 * Set the marker file, set to null if you don't want to use marker file
	 * Marker file will help the program not to do the parsing on already parsed files
	 * or files that it cannot parse when you re-run the program
	 * @param markerFile
	 */
	public void setMarkerFile(String markerFile) {
		this.markerFile = markerFile;
	}
	
	/**
	 * To see whether the program should overwrite existing result files or not
	 * @return
	 */
	public boolean isOverwriteResult() {
		return overwriteResult;
	}
	
	/**
	 * Set whether you want to overwrite existing result files or not
	 * @param overwriteResult
	 */
	public void setOverwriteResult(boolean overwriteResult) {
		this.overwriteResult = overwriteResult;
	}
	
	/**
	 * Tell whether the documents are represented in CBOR format
	 * @return
	 */
	public boolean isDocumentsInCborFormat() {
		return cborFormat;
	}

	/**
	 * Set whether the documents are represented in CBOR format
	 * @param cborFormat
	 */
	public void setDocumentsInCborFormat(boolean cborFormat) {
		this.cborFormat = cborFormat;
	}

	/**
	 * Filtering rule for documents
	 * @param path path to the documents
	 * @return true if the document is allowed to be parsed
	 */
	protected boolean isAllowProcessing(Path path) {
		return true;
	}
	
//	protected InputStream getInputStream(Path path) throws FileNotFoundException {
//		return new FileInputStream(path.toFile());
//	}

	/**
	 * Get relative path of a document from specified base folder
	 * @param path
	 * @return relative path of a document
	 */
	protected String getRelativePath(Path path) {
		return baseFolderUri.relativize(path.toUri()).toString();
	}

	protected Gson getGson() {
		return gson;
	}
	
	protected void setGson(Gson gson) {
		this.gson = gson;
	}
	
	
}
