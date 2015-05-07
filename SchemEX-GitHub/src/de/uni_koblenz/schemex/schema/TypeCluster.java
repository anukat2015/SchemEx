package de.uni_koblenz.schemex.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.uni_koblenz.schemex.util.Hash;

/**
 * This class represents the type clusters that are used to model the schema
 * index.
 * 
 * @author Mathias
 * 
 */
public class TypeCluster {

	/**
	 * Set of types that make the type cluster.
	 */
	protected Set<String> types;
	/**
	 * Equivalence classes that belong to this type cluster.
	 */
	protected Map<Integer, EquivalenceClass> eq_classes;

	/**
	 * instance count for this type cluster
	 */
	protected int instance_count;

	/**
	 * Number of flushed equivalence classes
	 */
	protected int flushed_eqc = 0;

	/**
	 * Creates a type cluster with an URI and a set of types
	 * 
	 * @param _uri
	 *            URI of the created type cluster (to be generated)
	 * @param _types
	 *            set of types that belong to this type cluster
	 */
	public TypeCluster(Set<String> _types) {
		types = _types;
		eq_classes = new HashMap<Integer, EquivalenceClass>();
		instance_count = 1;
	}

	/**
	 * Adds a equivalence class to the type cluster
	 * 
	 * @param _eq_class
	 *            Equivalence class that should be added to type cluster
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key. (A null return can also indicate that the map
	 *         previously associated null with key.)
	 */
	public EquivalenceClass addEqClass(EquivalenceClass _eq_class) {
		return eq_classes.put(_eq_class.hashCode(), _eq_class);
	}

	/**
	 * Checks, if a proper equivalence class is already contained by the schema
	 * 
	 * @param _eq_class_uri
	 *            The URI of the EQ class
	 * @return <code>true</code>, if the EQ class is already in the schema
	 */
	public boolean containsEqClass(int _eq_class_hash) {
		return eq_classes.containsKey(_eq_class_hash);
	}

	/**
	 * removes a equivalence class from a type cluster
	 * 
	 * @param _eq_class
	 *            equivalence class to be removed
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key. (A null return can also indicate that the map
	 *         previously associated null with key.)
	 */
	public EquivalenceClass flushEqClass(EquivalenceClass _eq_class) {
		flushed_eqc++;
		return eq_classes.remove(_eq_class.hashCode());
	}

	/**
	 * Returns the equivalence class by the given URI
	 * 
	 * @param _eq_class_uri
	 *            The URI of the EQ class
	 * @return the equivalence class of the given URI
	 */
	public EquivalenceClass getEqClass(int _eq_class_hash) {
		return eq_classes.get(_eq_class_hash);
	}

	/**
	 * Returns the number of eq classes in this type cluster
	 * 
	 * @return number of eq classes
	 */
	public int getEqClassCount() {
		return eq_classes.size() + flushed_eqc;
	}

	/**
	 * Creates a MD5 hash value based on the footprint / signature of the types
	 * that are attached to an type-cluster.
	 * 
	 * @return MD5 hash value
	 */
	protected String getTypeClusterHash() {
		return Hash.md5(Integer.toString(types.hashCode()));
	}

	/**
	 * Creates an RDF URI for a type cluster based on a URI prefix and a MD5
	 * hash value. If there are no types attached to this instance, a
	 * no-types-URI is set.
	 * 
	 * @return generated URI for a specific type cluster
	 */
	public String getURI() {
		String value = SchemaConstants.TC_URI_PREFIX + getTypeClusterHash();
		// no rdf:type links outgoing from this instance
		if (types.size() == 0) {
			// set return value to defined no-types-URI
			value = SchemaConstants.TC_URI_NO_TYPES;
		}
		return value;
	}

	/**
	 * Creates a postfix address for a type cluster based on MD5 hash value. If
	 * there are no types attached to this instance, a no-types-postfix is set.
	 * 
	 * @return generated URI for a specific type cluster
	 */
	public String getTypesPostfix() {
		String value = getTypeClusterHash();
		// no rdf:type links outgoing from this instance
		if (types.size() == 0) {
			// set return value to defined no-types-URI
			value = SchemaConstants.TC_URI_NO_TYPES_POSTFIX;
		}
		return value;

	}

	/**
	 * Increments the instance count by 1
	 * 
	 * @return instance count
	 */
	public int incInstanceCount() {
		return instance_count++;
	}

	/**
	 * Returns the number of instances assigned to this type cluster
	 * 
	 * @return instance count
	 */
	public int getInstanceCount() {
		return instance_count;
	}

	/**
	 * Returns the datasource count for this type cluster
	 * 
	 * @return instance count
	 */
	public int getDatasourceCount() {
		int count = 0;

		// summing up data sources for each eqc of this type cluster
		for (EquivalenceClass eqc : eq_classes.values()) {
			count += eqc.getDatasourceCount();
		}

		return count;
	}

	/**
	 * Returns the type / class count for this type cluster
	 * 
	 * @return type /class count
	 */
	public int getTypeCount() {
		return types.size();
	}

	@Override
	public int hashCode() {
		return types.hashCode();
	}

	// getter and setter

	public Set<String> getTypes() {
		return types;
	}

	public void setTypes(Set<String> types) {
		this.types = types;
	}

	public Map<Integer, EquivalenceClass> getEqClasses() {
		return eq_classes;
	}

}
