/**
 * 
 */
package de.uni_koblenz.schemex.cache;

import java.util.TreeSet;

import de.uni_koblenz.schemex.schema.SchemaConstants;
import de.uni_koblenz.schemex.util.Hash;

/**
 * @author Mathias
 * 
 */
public class InstanceTypeHash implements Instance {

	protected String instance_uri;
	protected int tc_hash;

	public InstanceTypeHash(InstanceFull _instance) {
		instance_uri = _instance.getInstanceURI();
		tc_hash = _instance.getTypesHash();
	}

	/**
	 * @see de.uni_koblenz.schemex.cache.Instance#addType(java.lang.String)
	 */
	@Override
	public boolean addType(String _type_uri) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see de.uni_koblenz.schemex.cache.Instance#getTypesHash()
	 */
	@Override
	public int getTypesHash() {
		return tc_hash;
	}

	/**
	 * Creates a MD5 hash value based on the footprint / signature of the types
	 * that are attached to an instance.
	 * 
	 * @return MD5 hash value
	 */
	protected String getTypeClusterHash() {
		return Hash.md5(Integer.toString(getTypesHash()));
	}

	/**
	 * @see de.uni_koblenz.schemex.cache.Instance#getTypeClusterURI()
	 */
	@Override
	public String getTypeClusterURI() {
		String value = SchemaConstants.TC_URI_PREFIX + getTypeClusterHash();
		// no rdf:type links outgoing from this instance
		if (tc_hash == 0) {
			// set return value to defined no-types-URI
			value = SchemaConstants.TC_URI_NO_TYPES;
		}
		return value;
	}

	/**
	 * @see de.uni_koblenz.schemex.cache.Instance#getInstanceURI()
	 */
	@Override
	public String getInstanceURI() {
		return instance_uri;
	}

	@Override
	public String getTypesPostfix() {
		String value;
		if(tc_hash == (new TreeSet<String>()).hashCode()){
			value = "tcnotypes";
		}
		value = getTypeClusterHash();
		return value;
	}

}
