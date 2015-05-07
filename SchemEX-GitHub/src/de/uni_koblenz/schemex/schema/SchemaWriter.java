package de.uni_koblenz.schemex.schema;

import de.uni_koblenz.schemex.util.maps.MultiMap;
import de.uni_koblenz.schemex.util.strategies.FlushStrategy;

/**
 * This interface provides methods, which handle the output of a given
 * {@link Schema}
 * 
 * 
 * 
 */
public interface SchemaWriter {

	/**
	 * 
	 * @return True, if the current configuration will append write operations
	 *         at the end of a file, false else
	 */
	public boolean isAppend();

	/**
	 * If the concrete writer supports appending at the end of a file, this
	 * method will change it's current configuration
	 * 
	 * @param append
	 *            Toggles, if write operations should append to a file
	 */
	public void setAppend(boolean append);

	/**
	 * 
	 * @return True, if the concrete writer supports appending write operations
	 *         at the end of a file, false else
	 */
	public boolean isAppendable();

	/**
	 * Writes the schema to a file
	 * 
	 * @param _filename
	 *            Filename
	 */
	public void writeSchema(String _filename);

	/**
	 * Writes all type-clusters to a CSV file
	 * 
	 * @param _filename
	 *            Filename
	 */
	public void writeTypeClusterCSV(String _filename);

	/**
	 * writes schema statistics
	 * 
	 * @param _directory
	 *            Directory used for statistics
	 */
	public void writeStatistics(String _directory);

	/**
	 * Writes all type-cluster URIs to a file
	 * 
	 * @param _filename
	 *            Filename
	 */
	public void writeTypeClusterURIs(String _filename);

	/**
	 * Writes all equivalence-class URIs to a file
	 * 
	 * @param _filename
	 *            Filename
	 */
	public void writeEquivalenceClassURIs(String _filename);

	/**
	 * Writes instances per equivalence class per datasource to a file
	 * 
	 * @param _filename
	 *            Filename
	 * 
	 */
	public void writeInstancesPerEQCDatasources(String _filename);

	/**
	 * Flushes schema concepts below a threshold (number of instances) to a
	 * file. Note that this method will not delete anything from the underlying
	 * schema. If you want the flushed equivalence classes to be deleted, call
	 * the deleteTcEqc - method of the {@link Schema}
	 * 
	 * @param _filename
	 *            Filename
	 * @return A {@link MultiMap} containing typecluster-hashes with a set of
	 *         associated equivalence classes. These are the classes, that have
	 *         been flushed. If this method is not implemented, some error
	 *         occured, null will be returned
	 * 
	 */
	public MultiMap<Integer, EquivalenceClass> flushSchema(String _filename,
			FlushStrategy fs);

}
