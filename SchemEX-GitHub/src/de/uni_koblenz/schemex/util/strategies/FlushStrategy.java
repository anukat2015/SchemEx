package de.uni_koblenz.schemex.util.strategies;

import java.util.Collection;
import java.util.Set;

import de.uni_koblenz.schemex.schema.EquivalenceClass;
import de.uni_koblenz.schemex.schema.TypeCluster;

public interface FlushStrategy {

	/**
	 * This method should be called, if the underlying data has changed. If the
	 * data was changed and isFlushable is executed, there is no garantuee, the
	 * result is still valid (Though it may be, depending on the strategy used)
	 */
	public void setUp(Collection<TypeCluster> tcs);

	/**
	 * Used to determine, whether a given equivalence class should be flushed
	 * 
	 * @param eqc
	 *            equivalence class to be inspected
	 * @return <i>true</i> if this equivalence class can be flushed,
	 *         <i>false</i> otherwise
	 */
	public boolean isFlushable(EquivalenceClass eqc);

	/**
	 * This method should be called after the current round of decision is
	 * finished to free up possibly used memory
	 */
	public void finishUp();
}
