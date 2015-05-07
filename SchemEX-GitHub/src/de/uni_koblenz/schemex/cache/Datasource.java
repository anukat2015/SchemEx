package de.uni_koblenz.schemex.cache;

public class Datasource implements Comparable<Datasource> {
	
	/**
	 * Datasource URI
	 */
	String uri;

	/**
	 * Creates a datasource
	 * 
	 * @param hash URI of the datasource
	 */
	public Datasource(String _uri) {
		super();
		this.uri = _uri;
	}
	
	/**
	 * Returns database URI with <> brackets
	 * 
	 * @return "<"+URI+">"
	 */
	public String toN3() {
		return "<"+uri+">";
	}

	@Override
	public int compareTo(Datasource ds) {
		// TODO Auto-generated method stub
		return this.getURI().compareTo(ds.getURI());
	}
	
	@Override
	public int hashCode() {
		return this.getURI().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Datasource))return false;
		Datasource ds = (Datasource) o;
		return this.getURI().equals(ds.getURI());
	};
	
	/*
	 * getters and setters
	 */
	public String getURI() {
		return uri;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}

}
