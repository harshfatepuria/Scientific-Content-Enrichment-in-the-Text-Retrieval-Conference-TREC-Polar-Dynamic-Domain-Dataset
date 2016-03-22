package geoparser;

import org.apache.tika.metadata.Metadata;

public class GeoData {
	
	public GeoData(String path, Metadata metadata) {
		this.path = path;
		this.metadata = metadata;
	}
	
	private String path;
	private Metadata metadata;
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	
	
}
