package de.uni_koblenz.schemex.schema;

import de.uni_koblenz.schemex.util.maps.MultiMap;
import de.uni_koblenz.schemex.util.strategies.FlushStrategy;

/**
 * Empty implementations of all {@link SchemaWriter} methods
 * 
 * 
 */
public class SchemaWriterSkeleton implements SchemaWriter {

	protected boolean append = true;

	@Override
	public void writeSchema(String _filename) {

	}

	@Override
	public void writeTypeClusterCSV(String _filename) {

	}

	@Override
	public void writeStatistics(String _directory) {

	}

	@Override
	public void writeTypeClusterURIs(String _filename) {

	}

	@Override
	public void writeEquivalenceClassURIs(String _filename) {

	}

	@Override
	public void writeInstancesPerEQCDatasources(String _filename) {

	}

	@Override
	public boolean isAppend() {

		return append;
	}

	@Override
	public void setAppend(boolean append) {
		this.append = append;

	}

	@Override
	public boolean isAppendable() {
		return true;
	}

	@Override
	public MultiMap<Integer,EquivalenceClass> flushSchema(String _filename, FlushStrategy fs) {

		return null;
	}



}
