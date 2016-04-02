package shared;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

/**
 * FileMarker helps marking the files that already finish parsing
 * or files that cannot be parsed.
 * Should speed up the execution when the program is re-run
 * so it doesn't have to start everything over again
 *
 */
public class FileMarker implements Closeable, AutoCloseable {
	
	private File markerFile;
	private HashSet<String> files;
	private PrintWriter printWriter;
	
	public FileMarker(File markerFile) throws FileNotFoundException, IOException {
		this.markerFile = markerFile;
		readMarkerFile();
		prepareWriter();
	}
	
	private void readMarkerFile() throws FileNotFoundException, IOException {
		if (!markerFile.exists()) {
			markerFile.getParentFile().mkdirs();
			markerFile.createNewFile();
		}
		
		try(BufferedReader br = new BufferedReader(new FileReader(markerFile))) {
			files = new HashSet<>();
			br.lines()
				.filter(line -> line.trim().length() > 0)
				.forEach(line -> files.add(line.trim()));
		}
		
		System.out.println("Marker:: marker file read, " + files.size() + " files marked");
	}
	
	private void prepareWriter() throws FileNotFoundException {
		printWriter = new PrintWriter(new FileOutputStream(markerFile, true));
	}
	
	public void closeWriter() {
		if (printWriter != null) {
			printWriter.close();
		}
	}
	
	public boolean exists(String path) {
		return files.contains(path);
	}
	
	public void mark(String path) {
		if (!exists(path)) {
			files.add(path);
			printWriter.println(path);
			
			if (files.size() % 200 == 0) {
				System.out.println("Marker:: " + files.size() + " files marked");
			}
		}
	}
	
	public Integer getMarkSize() {
		return files.size();
	}

	@Override
	public void close() throws IOException {
		closeWriter();
	}
}
