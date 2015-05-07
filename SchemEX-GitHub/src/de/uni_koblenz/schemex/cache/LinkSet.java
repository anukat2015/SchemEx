package de.uni_koblenz.schemex.cache;

import java.util.TreeSet;

import de.uni_koblenz.schemex.schema.SchemaConstants;
import de.uni_koblenz.schemex.util.Hash;

/**
 * A set of Links
 * 
 * @author Mathias
 *
 */
public class LinkSet extends TreeSet<Link> {
	
	/**
	 * Create a MD5 hash
	 * 
	 * @return MD5 hashed hashCode()-value
	 */
	protected String getLinkSetHash() {
		return Hash.md5(Integer.toString(this.hashCode()));
	}
	
	/**
	 * Creates an URI based on the linkset
	 * 
	 * @return linkset URI
	 */
	public String getLinkSetURI() {
		return SchemaConstants.LINKSET_URI_PREFIX + getLinkSetHash();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1863495408543885361L;

}
