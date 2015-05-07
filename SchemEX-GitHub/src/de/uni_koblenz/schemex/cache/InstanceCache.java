package de.uni_koblenz.schemex.cache;

public interface InstanceCache {

	/**
	 * Add a new instance to the Cache.
	 * 
	 * @param _instance Instance to be inserted to the cache.
	 * @return <code>true</code>, if instance could be inserted, <code>false</code>, if cache
	 * is fully loaded
	 */
	boolean add(Instance _instance);
	
	/**
	 * Checks, if the cache contains an instance with a given URI.
	 * 
	 * @param _instance_uri The URI to look for.
	 * @return <code>true</code>, if the cache contains an instance with the given URI.
	 */
	boolean containsInstance(String _instance_uri);
	
	/**
	 * Checks, if the cache is empty.
	 * 
	 * @return <code>true</code>, if the cache is empty.
	 */
	boolean isEmpty();
	
	/**
	 * Returns the size / number of instances in the cache.
	 * 
	 * @return number of items
	 */
	int size();
	
	/**
	 * Returns the max size / number of instances in the cache.
	 * 
	 * @return number of max items
	 */
	int maxSize();
	
	/**
	 * Returns the Instance with the given URI.
	 * 
	 * @param _instance_uri The URI to look for.
	 * @return Returns the instance with the URI _instance_uri
	 */
	Instance getInstance(String _instance_uri);
	
	/**
	 * Removes an instance from the cache
	 * 
	 * @return Instance-object
	 */
	Instance remove() throws Exception;

}
