package de.uni_koblenz.schemex.util.maps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This interface represents a multimap. A multimap differs from a normal map,
 * in that it maps each key to multiple values. Otherwise the same rules apply.
 * 
 * When calling any put method multiple times with the same key, the value this
 * key maps to won't be replaced, instead each new value will be added to the
 * mappings of the key. Likewise, calling the get method won't return a single
 * value, but a {@link Collection} of values
 * 
 * @author Bastian
 * 
 * @param <K>
 *            Type of the keys
 * @param <V>
 *            Type of the values
 */
public interface MultiMap<K, V> {

	/**
	 * Removes all mappings from this map. (Optional operation)
	 */
	public void clear();

	/**
	 * Checks, whether this map contains a mapping for the specified key. More
	 * formally, returns true if and only if this map contains a mapping for a
	 * key k such that (key==null ? k==null : key.equals(k)). (There can be at
	 * most one such mapping.)
	 * 
	 * @param key
	 *            key whose presence in this map is to be tested
	 * @return <i>true</i> if this map contains a mapping for the specified key,
	 *         <i>false</i> otherwise
	 */
	public boolean containsKey(Object key);

	/**
	 * Returns a Set view of the mappings contained in this map. The set is
	 * backed by the map, so changes to the map are reflected in the set, and
	 * vice-versa. If the map is modified while an iteration over the set is in
	 * progress (except through the iterator's own remove operation, or through
	 * the setValue operation on a map entry returned by the iterator) the
	 * results of the iteration are undefined. The set supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * Iterator.remove, Set.remove, removeAll, retainAll and clear operations.
	 * It does not support the add or addAll operations.
	 * 
	 * @return a set view of the mappings contained in this map
	 */
	public Set<Map.Entry<K, Set<V>>> entrySet();

	/**
	 * Checks, whether this map is empty
	 * 
	 * @return <i>true</i> if this map contains no mappings, <i>false</i>
	 *         otherwise
	 */
	public boolean isEmpty();

	/**
	 * Returns a Set view of the keys contained in this map. The set is backed
	 * by the map, so changes to the map are reflected in the set, and
	 * vice-versa. If the map is modified while an iteration over the set is in
	 * progress (except through the iterator's own remove operation), the
	 * results of the iteration are undefined. The set supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * Iterator.remove, Set.remove, removeAll, retainAll, and clear operations.
	 * It does not support the add or addAll operations.
	 * 
	 * @return a set view of the keys contained in this map
	 */
	public Set<K> keySet();

	/**
	 * Returns true if this map maps one or more keys to the specified value.
	 * More formally, returns true if and only if this map contains at least one
	 * mapping to a value v such that (value==null ? v==null : value.equals(v)).
	 * This operation will probably require time linear in the map size for most
	 * implementations of the {@link MultiMap} interface
	 * 
	 * @param value
	 *            value whose presence in this map is to be tested
	 * @return <i>true</i> if this map maps one or more keys to the specified
	 *         value, <i>false</i> otherwise
	 */
	public boolean containsValue(Object value);

	/**
	 * Returns a {@link Collection} of values to which the specified key is
	 * mapped, or null if this map contains no mapping for the key. More
	 * formally, if this map contains a mapping from a key k to a set of values
	 * v such that (key==null ? k==null : key.equals(k)), then this method
	 * returns v; otherwise it returns null. (There can be at most one such
	 * mapping.)
	 * 
	 * @param key
	 *            the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this
	 *         map contains no mapping for the key
	 */
	public Collection<V> get(Object key);

	/**
	 * Associates the specified value with the specified key in this map . If
	 * the map previously contained a mapping for the key, the new value is
	 * added to the set of mappings of this key. (A map m is said to contain a
	 * mapping for a key k if and only if m.containsKey(k) would return true.)
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key. (A null return can also indicate that the map
	 *         previously associated null with key, if the implementation
	 *         supports null values.)
	 */
	public V put(K key, V value);

	/**
	 * Removes the mapping for a key from this map if it is present (optional
	 * operation). More formally, if this map contains a mapping from key k to a
	 * set of values v such that (key==null ? k==null : key.equals(k)), that
	 * mapping is removed. (The map can contain at most one such mapping.)
	 * Returns the set of values to which this map previously associated the
	 * key, or null if the map contained no mapping for the key.
	 * 
	 * If this map permits null values, then a return value of null does not
	 * necessarily indicate that the map contained no mapping for the key; it's
	 * also possible that the map explicitly mapped the key to null.
	 * 
	 * The map will not contain a mapping for the specified key once the call
	 * returns.
	 * 
	 * @param key
	 *            key whose mapping is to be removed from the map
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key.
	 */
	public Collection<V> remove(K key);

	/**
	 * Removes the specified value from the mapping for a key from this map if
	 * it is present (optional operation). More formally, if this map contains a
	 * mapping from key k to a set of values v such that (key==null ? k==null :
	 * key.equals(k)), the given value will be removed from v, such that the
	 * removed value v1 satisfies (value==null ? v1==null : value.equals(v1)).
	 * (The map can contain at most one such mapping.) Returns the given value ,
	 * or null if the map contained no mapping for the key and value pair.
	 * 
	 * If this map permits null values, then a return value of null does not
	 * necessarily indicate that the map contained no mapping for the key; it's
	 * also possible that the map explicitly mapped the key to null.
	 * 
	 * The mapping for the specified key will not contain the given value once
	 * the call returns.
	 * 
	 * @param key
	 *            key, in whose mapping the value is to be removed
	 * @return the previous value, or null if there was no mapping for key/value
	 *         pair.
	 */
	public V remove(K key, V value);

	/**
	 * Returns the number of key-value mappings in this map. If the map contains
	 * more than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
	 * 
	 * @return the number of key-value mappings in this map
	 */
	public int size();

	/**
	 * Returns a Collection view of the values contained in this map, that means
	 * all values of all keys. The collection is backed by the map, so changes
	 * to the map are reflected in the collection, and vice-versa. If the map is
	 * modified while an iteration over the collection is in progress (except
	 * through the iterator's own remove operation), the results of the
	 * iteration are undefined. The collection supports element removal, which
	 * removes the corresponding mapping from the map, via the Iterator.remove,
	 * Collection.remove, removeAll, retainAll and clear operations. It does not
	 * support the add or addAll operations.
	 * 
	 * @return a collection view of the values contained in this map
	 */
	public Collection<V> values();

	/**
	 * Copies all of the mappings from the specified MultiMap to this map
	 * (optional operation). The behavior of this operation is undefined if the
	 * specified map is modified while the operation is in progress.
	 * 
	 * @param map
	 *            mappings to be stored in this map
	 */
	void putAll(Map<? extends K, ? extends Collection<? extends V>> map);

}
