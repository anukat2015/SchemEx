package de.uni_koblenz.schemex.util.maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MultiHashMap<K, V> implements MultiMap<K, V> {

	private Map<K, Set<V>> map;

	public MultiHashMap() {
		map = new HashMap<K, Set<V>>();
	}

	@Override
	public void clear() {
		map.clear();

	}

	@Override
	public boolean containsKey(Object key) {

		return map.containsKey(key);
	}

	@Override
	public Set<Entry<K, Set<V>>> entrySet() {

		return map.entrySet();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public void putAll(Map<? extends K,? extends Collection<? extends V>> map) {

		for (Entry<? extends K,? extends  Collection<? extends V>> e : map.entrySet()) {
			K key = e.getKey();
			Collection<? extends V> value = e.getValue();

			Set<V> set = this.map.get(key);
			if (set != null) {
				set.addAll(value);
			} else {
				set = new HashSet<V>();
				set.addAll(value);
				this.map.put(key, set);
			}
		}

	}

	@Override
	public boolean containsValue(Object value) {

		for (Entry<K, Set<V>> e : map.entrySet()) {
			if (e.getValue().contains(value))
				return true;
		}
		return false;
	}

	@Override
	public Collection<V> get(Object key) {

		return map.get(key);
	}

	@Override
	public V put(K key, V value) {

		Set<V> set = map.get(key);

		if (set != null) {
			if (set.add(value))
				return value;
			else
				return null;
		} else {
			set = new HashSet<V>();

			map.put(key, set);
			if (set.add(value))
				return value;
			else
				return null;
		}

	}

	@Override
	public Collection<V> remove(K key) {

		return map.remove(key);
	}

	@Override
	public V remove(K key, V value) {

		Set<V> set = map.get(key);

		if (set != null && set.remove(value))
			return value;
		else
			return null;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		
		Set<V> set = new HashSet<V>();
		for(Entry<K,Set<V>> e:map.entrySet() )
		{
			set.addAll(e.getValue());
		}
		return set;
	}

}
