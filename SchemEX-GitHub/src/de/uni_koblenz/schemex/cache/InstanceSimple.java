package de.uni_koblenz.schemex.cache;

import java.util.*;

import de.uni_koblenz.schemex.schema.SchemaConstants;
import de.uni_koblenz.schemex.util.Hash;


/**
 * RDF instance item/object containing the subject URI and its RDF-types
 * 
 * @author Mathias
 *
 */
public class InstanceSimple implements Instance {
	/**
	 * instance URI
	 */
	protected String instance_uri;
	
	/**
	 * set of rdf-types
	 */
	protected Set<String> types;
	
	/**
	 * Creates a new simple RDF instance with RDF types (without links and datasources)
	 * 
	 * @param _instance_uri subject URI
	 */
	public InstanceSimple(String _instance_uri) {
		instance_uri = _instance_uri;
		types = new TreeSet<String>();
	}
	
	/**
	 * Adds a new RDF-type to the instance
	 * 
	 * @param _type_uri RDF type URI
	 * @return <code>true</code> if this set did not already contain the specified element 
	 */

	public boolean addType(String _type_uri) {
		return types.add(_type_uri);
	}
	
	/**
	 * Creates a MD5 hash value based on the footprint / signature of the types that are
	 * attached to an instance. 
	 * 
	 * @return MD5 hash value
	 */
	protected String getTypeClusterHash() {
		
		return Hash.md5(Integer.toString(getTypesHash()));
	}
	
	
	/**
	 * @see de.uni_koblenz.schemex.cache.Instance#getTypeClusterURI()
	 */
	public String getTypeClusterURI() {
		String value = SchemaConstants.TC_URI_PREFIX + getTypeClusterHash();
		// no rdf:type links outgoing from this instance
		if (types.size() == 0) {
			// set return value to defined no-types-URI
			value = SchemaConstants.TC_URI_NO_TYPES;
		}
		return value;
	}
	
	/**
	 * @see de.uni_koblenz.schemex.cache.Instance#getTypesHash()
	 */
	public int getTypesHash() {
		return types.hashCode();
	}
	
	// getter and setter
	
	/**
	 * @see de.uni_koblenz.schemex.cache.Instance#getInstanceURI()
	 */
	public String getInstanceURI() {
		return instance_uri;
	}

	public void setInstanceURI(String instance_uri) {
		this.instance_uri = instance_uri;
	}

	public Set<String> getTypes() {
		return types;
	}

	public void setTypes(Set<String> types) {
		this.types = types;
	}

	@Override
	public String getTypesPostfix() {
		String value = getTypeClusterHash();
		// no rdf:type links outgoing from this instance
		if (types.size() == 0) {
			// set return value to defined no-types-URI
			value = "tcnotypes";
		}
		return value;
	}

//	public static void main(String[] args) {
//		InstanceSimple x = new InstanceSimple("test");
//		System.out.println(x.getInstance_uri());
//	}
}

	
