package de.uni_koblenz.schemex.util.strategies;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import de.uni_koblenz.schemex.schema.EquivalenceClass;
import de.uni_koblenz.schemex.schema.TypeCluster;
import de.uni_koblenz.schemex.util.maps.MultiHashMap;
import de.uni_koblenz.schemex.util.maps.MultiMap;

public class PercentageStrategy implements FlushStrategy {

	private MultiMap<Integer, EquivalenceClass> eqcs;
	private double perc;

	public PercentageStrategy(double d) {
		this.perc = d;
		eqcs = new MultiHashMap<Integer, EquivalenceClass>();
	}

	@Override
	public void setUp(Collection<TypeCluster> tcs) {
		eqcs.clear();
		Map<Integer, Set<EquivalenceClass>> m = new TreeMap<Integer, Set<EquivalenceClass>>();
		// Number of equivalence classes
		int n = 0;
		for (TypeCluster tc : tcs) {
			for (EquivalenceClass e : tc.getEqClasses().values()) {

				Set<EquivalenceClass> s = m.get(e.getInstanceCount());
				if (s != null) {
					s.add(e);
				} else {
					s = new HashSet<EquivalenceClass>();
					s.add(e);
					m.put(e.getInstanceCount(), s);
				}

				n++;
			}
		}

		int maxFlushable = (int) (n * perc);

		// Since the keys are sorted in ascending order, when we iterate through
		// the map, we will remove the equivalence classes with the least
		// instances
		for (Entry<Integer, Set<EquivalenceClass>> e : m.entrySet()) {
			for (EquivalenceClass eqc : e.getValue()) {
				if (maxFlushable == 0)
					return;

				eqcs.put(eqc.getTypeClusterHash(), eqc);
				maxFlushable--;
			}
		}
		m.clear();

	}

	@Override
	public boolean isFlushable(EquivalenceClass eqc) {
		Collection<EquivalenceClass> s = eqcs.get(eqc.getTypeClusterHash());
		if (s == null)
			return false;
		return s.contains(eqc);
	}

	@Override
	public void finishUp() {
		eqcs.clear();

	}

}
