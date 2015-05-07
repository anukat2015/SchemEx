package de.uni_koblenz.schemex;

import org.semanticweb.yars.nx.Node;

import de.uni_koblenz.schemex.util.Input;

public interface SchemExInterface {

	/**
	 * Starts the parsing and schema extraction process
	 * 
	 * @throws Exception
	 */
	public abstract void startProcess();

	/**
	 * Stops the parsing / processing. Starts post-processing.
	 * 
	 * @throws Exception
	 */
	public abstract void stopProcess();

	/**
	 * Processes a given NQuad input stream.
	 * 
	 * @throws Exception
	 */
	public abstract void processStream(Input _input) throws Exception;

	/**
	 * process triple/quadruple
	 * 
	 * @param _ns
	 *            N-Quad from N-Quad stream
	 * @throws Exception
	 */
	public abstract void processNQuad(Node[] _ns) throws Exception;

	/**
	 * write schema to file
	 * 
	 * @param _filename
	 *            destination file
	 */
	public abstract void writeSchemaToFile(String _name);

	/**
	 * Flushs data sources to the schema file
	 * 
	 * @param _name
	 *            schema file name
	 */
	public abstract void flushDatasourcesToFile(String _name);

	/**
	 * prints statistics via logger info function
	 */
	public abstract void printStats();

	/**
	 * write CSV files
	 * 
	 * @param _filename
	 *            destination file
	 */
	public abstract void writeAdditionalFiles(String _name);

	/**
	 * set RDFS triple extraction
	 */
	public abstract void setRDFSExtraction();

	/**
	 * Flushs data sources to the schema file
	 * 
	 * @param _name
	 *            output folder
	 * 
	 */
	void flushSchemaToFile(String _name);

	public abstract void setIgnore_types(boolean ignore_types);

}