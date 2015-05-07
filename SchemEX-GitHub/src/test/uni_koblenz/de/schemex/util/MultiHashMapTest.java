package test.uni_koblenz.de.schemex.util;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.schemex.util.maps.MultiHashMap;

public class MultiHashMapTest {

	private MultiHashMap<Integer, Integer> map;

	@Before
	public void setUp() {
		map = new MultiHashMap<Integer, Integer>();
	}

	@Test
	public void testClear() {

		assertEquals(0, map.size());

		map.put(0, 0);
		map.put(1, 0);
		map.put(2, 0);

		assertEquals(3, map.size());
		map.clear();
		assertEquals(0, map.size());
	}

	@Test
	public void testContainsKey() {

		assertFalse(map.containsKey(0));

		map.put(0, 0);
		assertTrue(map.containsKey(0));

		map.put(1, 0);
		assertTrue(map.containsKey(1));

		map.put(2, 0);
		assertTrue(map.containsKey(2));

		map.put(3, 0);
		assertTrue(map.containsKey(3));

		assertFalse(map.containsKey(4));

	}

	@Test
	public void testEntrySet() {

		Set<Map.Entry<Integer, Set<Integer>>> set = map.entrySet();
		Entry<Integer, Set<Integer>> entry;
		assertEquals(0, set.size());

		Iterator<Entry<Integer, Set<Integer>>> it;

		map.put(0, 0);
		set = map.entrySet();
		assertEquals(1, set.size());

		it = set.iterator();
		entry = it.next();
		assertEquals(new Integer(0), entry.getKey());
		assertEquals(1, entry.getValue().size());

		map.put(1, 0);
		map.put(2, 0);
		set = map.entrySet();
		assertEquals(3, set.size());

		it = set.iterator();
		it.next();
		entry = it.next();
		assertEquals(new Integer(1), entry.getKey());
		assertEquals(1, entry.getValue().size());

		entry = it.next();
		assertEquals(new Integer(2), entry.getKey());
		assertEquals(1, entry.getValue().size());

		map.put(2, 1);
		set = map.entrySet();
		assertEquals(3, set.size());

		it = set.iterator();
		it.next();
		it.next();
		entry = it.next();
		assertEquals(new Integer(2), entry.getKey());
		assertEquals(2, entry.getValue().size());
	}

	@Test
	public void testIsEmpty() {

		assertTrue(map.isEmpty());

		map.put(0, 0);

		assertFalse(map.isEmpty());

		map.remove(0);

		assertTrue(map.isEmpty());

	}

	@Test
	public void testKeySet() {

		Set<Integer> set = map.keySet();

		assertEquals(0, set.size());

		map.put(0, 0);
		set = map.keySet();
		assertEquals(1, set.size());

		map.put(1, 0);
		map.put(2, 0);
		set = map.keySet();
		assertEquals(3, set.size());

		map.remove(0);
		set = map.keySet();
		assertEquals(2, set.size());
	}

	@Test
	public void testPutAll() {

		assertEquals(0, map.size());
		Map<Integer, Set<Integer>> m = new HashMap<Integer, Set<Integer>>();

		Set<Integer> set = new HashSet<Integer>();
		set.add(new Integer(0));
		m.put(new Integer(0), set);

		map.putAll(m);

		assertEquals(1, map.size());

		Iterator<Entry<Integer, Set<Integer>>> it = map.entrySet().iterator();
		Entry<Integer, Set<Integer>> e = it.next();

		assertEquals(1, e.getValue().size());
		assertTrue(e.getValue().contains(0));

		m = new HashMap<Integer, Set<Integer>>();

		set = new HashSet<Integer>();
		set.add(new Integer(0));
		set.add(new Integer(1));
		m.put(new Integer(0), set);

		set = new HashSet<Integer>();
		set.add(new Integer(2));
		set.add(new Integer(4));
		m.put(new Integer(1), set);

		set = new HashSet<Integer>();
		set.add(new Integer(6));
		m.put(new Integer(2), set);

		map.putAll(m);

		assertEquals(3, map.size());
		it = map.entrySet().iterator();
		e = it.next();

		assertEquals(2, e.getValue().size());
		assertTrue(e.getValue().contains(0));
		assertTrue(e.getValue().contains(1));

		e = it.next();
		assertEquals(2, e.getValue().size());
		assertTrue(e.getValue().contains(2));
		assertTrue(e.getValue().contains(4));
		assertFalse(e.getValue().contains(0));

		e = it.next();
		assertEquals(1, e.getValue().size());
		assertTrue(e.getValue().contains(6));
		assertFalse(e.getValue().contains(0));

	}

	@Test
	public void testContainsValue() {

		assertFalse(map.containsValue(0));
		assertFalse(map.containsValue(1));
		assertFalse(map.containsValue(2));
		assertFalse(map.containsValue(3));
		assertFalse(map.containsValue(4));

		map.put(0, 0);
		assertTrue(map.containsValue(0));

		map.put(0, 1);
		assertTrue(map.containsValue(0));
		assertTrue(map.containsValue(1));

		map.put(0, 2);
		assertTrue(map.containsValue(0));
		assertTrue(map.containsValue(2));
		assertTrue(map.containsValue(1));

		map.put(1, 0);
		assertTrue(map.containsValue(0));
		assertTrue(map.containsValue(2));
		assertTrue(map.containsValue(1));

		map.put(2, 4);
		assertTrue(map.containsValue(0));
		assertTrue(map.containsValue(2));
		assertTrue(map.containsValue(1));
		assertTrue(map.containsValue(4));

	}

	@Test
	public void testGet() {
		assertNull(map.get(0));
		assertNull(map.get(1));
		assertNull(map.get(2));
		assertNull(map.get(3));

		Collection<Integer> s;

		map.put(0, 0);
		s = map.get(0);

		assertEquals(1, s.size());
		assertTrue(s.contains(0));

		map.put(0, 1);
		s = map.get(0);
		assertEquals(2, s.size());
		assertTrue(s.contains(0));
		assertTrue(s.contains(1));
	}

	@Test
	public void testPut() {

		assertEquals(0, map.size());
		map.put(0, 0);
		map.put(0, 1);
		assertEquals(1, map.size());
		assertEquals(2, map.get(0).size());

		map.put(1, 0);
		assertEquals(2, map.size());
		assertEquals(2, map.get(0).size());
		assertEquals(1, map.get(1).size());
	}

	@Test
	public void testRemoveK() {

		assertEquals(0, map.size());
		assertNull(map.remove(0));

		map.put(0, 0);
		Collection<Integer> i = map.remove(0);

		assertEquals(0, map.size());
		assertEquals(1, i.size());
		assertTrue(i.contains(0));

		map.put(0, 0);
		map.put(0, 1);
		map.put(1, 0);

		i = map.remove(0);

		assertEquals(1, map.size());
		assertEquals(2, i.size());
		assertTrue(i.contains(0));
		assertTrue(i.contains(1));
	}

	@Test
	public void testRemoveKV() {

		assertNull(map.remove(0, 0));
		Integer i;

		map.put(0, 0);
		assertEquals(new Integer(0), map.remove(0, 0));
		assertEquals(0, map.get(0).size());

		map.put(0, 0);
		map.put(0, 1);
		assertEquals(new Integer(0), map.remove(0,0));
		assertEquals(1, map.get(0).size());
	}

	@Test
	public void testSize() {

		assertEquals(0, map.size());
		map.put(0, 0);
		assertEquals(1,map.size());
		map.put(0, 0);
		assertEquals(1,map.size());
		map.put(0, 1);
		assertEquals(1,map.size());
		map.put(1, 0);
		assertEquals(2,map.size());
		map.put(2, 0);
		assertEquals(3,map.size());
		map.remove(0, 0);
		assertEquals(3,map.size());
		map.remove(0);
		assertEquals(2, map.size());
	}

	@Test
	public void testValues() {
		
		assertEquals(0,map.values().size());
		
		Collection<Integer> c;
		
		map.put(0, 0);
		c = map.values();
		
		assertEquals(1, c.size());
		assertTrue(c.contains(0));
		
		map.put(0, 1);
		c = map.values();
		assertEquals(2, map.values().size());
		assertTrue(c.contains(0));
		assertTrue(c.contains(1));
		
		map.remove(0, 0);
		assertEquals(1, map.values().size());
		assertTrue(c.contains(1));
		
		map.put(0, 0);
		c = map.values();
		assertEquals(2, map.values().size());
		assertTrue(c.contains(0));
		assertTrue(c.contains(1));
		
		map.put(1, 0);
		c = map.values();
		assertEquals(2, map.values().size());
		assertTrue(c.contains(0));
		assertTrue(c.contains(1));
		
		map.put(1, 2);
		c = map.values();
		assertEquals(3, map.values().size());
		assertTrue(c.contains(0));
		assertTrue(c.contains(1));
		assertTrue(c.contains(2));
	}

}
