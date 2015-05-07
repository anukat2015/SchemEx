package test.uni_koblenz.de.schemex.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.Assert;

import de.uni_koblenz.schemex.cache.Datasource;

public class DatasourceTest {

	@Test
	public void testEquals() {
		Datasource a = new Datasource("test");
		Datasource b = new Datasource("test");
		Datasource c = new Datasource("test");
		// Reflexivity
		Assert.assertEquals(a, a);
		Assert.assertEquals(b, b);
		Assert.assertEquals(c, c);

		// Symmetry
		Assert.assertEquals(a, b);
		Assert.assertEquals(b, a);

		// Transivity
		Assert.assertEquals(a, b);
		Assert.assertEquals(b, c);
		Assert.assertEquals(a, c);

		// "Non-Nullity"
		Assert.assertNotSame(a, null);

		// Unequality
		Datasource d = new Datasource("test2");
		Assert.assertNotSame(a, d);

	}

	@Test
	public void testHash()
	{
		Datasource a = new Datasource("test");
		Datasource b = new Datasource("test");
		Datasource c = new Datasource("test");
		// Not required
		// Reflexivity
		Assert.assertEquals(a.hashCode(), a.hashCode());
		Assert.assertEquals(b.hashCode(), b.hashCode());
		Assert.assertEquals(c.hashCode(), c.hashCode());

		// Symmetry
		Assert.assertEquals(a.hashCode(), b.hashCode());
		Assert.assertEquals(b.hashCode(), a.hashCode());

		// Transivity
		Assert.assertEquals(a.hashCode(), b.hashCode());
		Assert.assertEquals(b.hashCode(), c.hashCode());
		Assert.assertEquals(a.hashCode(), c.hashCode());

		
		// Unequality
		Datasource d = new Datasource("test2");
		Assert.assertNotSame(a.hashCode(), d.hashCode());
	}
	@Test
	public void testHashInSetAndMap() {
		Set<Datasource> set = new HashSet<Datasource>();
		Datasource a = new Datasource("test");
		Datasource b = new Datasource("test");
		Datasource c = new Datasource("test");
		Datasource d = new Datasource("test2");

		Assert.assertEquals(0, set.size());
		set.add(a);
		Assert.assertEquals(1, set.size());
		set.add(a);
		Assert.assertEquals(1, set.size());
		set.add(b);
		Assert.assertEquals(1, set.size());
		set.add(c);
		Assert.assertEquals(1, set.size());
		set.add(d);
		Assert.assertEquals(2, set.size());

		Map<Datasource, Integer> map = new HashMap<Datasource, Integer>();

		Datasource[] sources = { a, b, c, d };

		for (Datasource ds : sources)
			Assert.assertFalse(map.containsKey(ds));
		for (Datasource ds : sources) {
			if (map.containsKey(ds)) {
				map.put(ds, map.get(ds) + 1);
			} else {
				map.put(ds, 1);
			}
		}
		for (Datasource ds : sources)
			Assert.assertTrue(map.containsKey(ds));
		
		Assert.assertEquals(3, (int)map.get(a));
		Assert.assertEquals(3, (int)map.get(b));
		Assert.assertEquals(3, (int)map.get(c));
		Assert.assertEquals(1, (int)map.get(d));
	}
}
