package de.uni_koblenz.schemex.cache;

public interface Instance {
	
	/**
	 * Adds an RDF type to an instance
	 * @param _type_uri URI of the RDF type
	 * @return <code>true</code> if this set did not already contain the specified element
	 */
	public boolean addType(String _type_uri);
	
	/**
	 * Creates an hash value based on the set of types that an
	 * instance belongs to
	 * 
	 * @return hash value
	 */
	public int getTypesHash();
	
	/**
	 * Creates an RDF URI for a type cluster based on a URI prefix and a MD5 hash value.
	 * If there are no types attached to this instance, a no-types-URI is set.
	 * 
	 * @return generated URI for a specific type cluster
	 */
	public String getTypeClusterURI();
	
	/**
	 * Returns the URI of the instance
	 * @return instance URI
	 */
	public String getInstanceURI();
	
	/**
	 * Creates a postfix address for a type cluster based on MD5 hash value.
	 * If there are no types attached to this instance, a no-types-postfix is set.
	 * 
	 * @return generated URI for a specific type cluster
	 */
	public String getTypesPostfix();
}
