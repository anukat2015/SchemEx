package de.uni_koblenz.schemex.schema;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.yars.nx.Triple;

import de.uni_koblenz.schemex.cache.Datasource;
import de.uni_koblenz.schemex.util.Pair;
import de.uni_koblenz.schemex.util.maps.MultiMap;
import de.uni_koblenz.schemex.util.strategies.FlushStrategy;

/**
 * schema
 * 
 * @author Mathias
 */
public class Schema {

	/**
	 * logger
	 */
	private static Logger logger = LogManager.getRootLogger();

	/**
	 * 'set' of type clusters
	 */
	protected Map<Integer, TypeCluster> type_clusters;

	protected Set<Triple> rdfs_triples;

	protected Set<Pair<String, String>> subclassRelationships;

	/**
	 * number of flushed data sources
	 */
	protected int flushed_ds;

	/**
	 * number of flushed equivalence classes
	 */
	protected int flushed_eqc;

	/**
	 * Schema writer to allow different output formats
	 */
	protected SchemaWriter sWriter;

	/**
	 * creates a schema
	 * 
	 * @param factory
	 *            {@link SchemaWriterFactory} to produce a {@link SchemaWriter}
	 *            for this schema
	 */
	public Schema(SchemaWriterFactory factory) {
		type_clusters = new HashMap<Integer, TypeCluster>();
		rdfs_triples = new HashSet<Triple>();
		sWriter = factory.getSchemaWriter(this);
		subclassRelationships = new HashSet<Pair<String, String>>();
	}

	/**
	 * Checks, if a type cluster is contained by the schema
	 * 
	 * @param _uri
	 *            type cluster URI
	 * @return <code>true</code>, if the type cluster is contained by the schema
	 */
	public boolean containsTypeCluster(int _hash) {
		return type_clusters.containsKey(_hash);
	}

	/**
	 * Returns the type cluster for a given type cluster URI
	 * 
	 * @param _uri
	 *            type cluster URI
	 * @return type cluster
	 */
	public TypeCluster getTypeCluster(int _hash) {
		return type_clusters.get(_hash);
	}

	/**
	 * Adds a type cluster to the schema
	 * 
	 * @param _type_cluster
	 *            Type cluster to be added to the schema
	 * @return <code>true</code> if this set did not already contain the
	 *         specified element
	 */
	public void addTypeCluster(TypeCluster _type_cluster) {
		type_clusters.put(_type_cluster.hashCode(), _type_cluster);
	}

	/**
	 * Removes a type cluster from the schema
	 * 
	 * @param _type_cluster
	 *            Type cluster to be removed to the schema
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key. (A null return can also indicate that the map
	 *         previously associated null with key.)
	 */
	public TypeCluster removeTypeCluster(TypeCluster _type_cluster) {
		return type_clusters.remove(_type_cluster.hashCode());
	}

	/**
	 * Adds an equivalence class to a specified type cluster (by URI)
	 * 
	 * @param _type_cluster_uri
	 *            type cluster URI
	 * @param _eq_class
	 *            equivalence class object
	 */
	public void addEquivalenceClass(int _type_cluster_hash,
			EquivalenceClass _eq_class) {
		getTypeCluster(_type_cluster_hash).addEqClass(_eq_class);
	}

	/**
	 * Returns the number of distinct type clusters
	 * 
	 * @return number of type clusters in the schema
	 */
	public int getTypeClusterCount() {
		return type_clusters.size();

	}

	/**
	 * Returns the number of equivalence classes
	 * 
	 * @return number of equivalence classes in the schema
	 */
	public int getEqClassCount() {
		int sum = 0;

		// iterate through HashMap
		for (TypeCluster tc : type_clusters.values()) {
			sum += tc.getEqClassCount();
		}
		return sum;

	}

	/**
	 * Adds an RDFS triple to the RDFS triples set
	 * 
	 * @param _triple
	 *            Triple to be added
	 * @return <code>true</code> if this set did not already contain the
	 *         specified element
	 */
	public boolean addRDFSTriple(Triple _triple) {
		return rdfs_triples.add(_triple);
	}

	/**
	 * Returns the number of RDFS triples in the schema
	 * 
	 * @return number of RDFS triples
	 */
	public int getRDFSTripleCount() {
		return rdfs_triples.size();
	}

	/**
	 * Returns the set of RDFS triples
	 * 
	 * @return RDFS triples set
	 */
	public Set<Triple> getRDFSTriples() {
		return rdfs_triples;
	}

	/**
	 * Add a subclass relationship to the schema
	 * 
	 * @param subclass
	 *            The subclass
	 * @param superclass
	 *            The superclass
	 */
	public void addSubclass(String subclass, String superclass) {
		subclassRelationships
				.add(new Pair<String, String>(subclass, superclass));
	}

	/**
	 * Get all subclasses
	 * 
	 * @return A set containing all subclass relationships
	 */
	public Set<Pair<String, String>> getSubclasses() {
		return subclassRelationships;
	}

	/**
	 * returns the number of instances that were processed
	 * 
	 * @return number of instances
	 */
	public int getInstanceCount() {
		// helper variables
		int count = 0;
		for (TypeCluster tc : type_clusters.values()) {
			// counts and sums the instances
			count += tc.getInstanceCount();
		}
		return count;
	}

	/**
	 * returns the number of data sources
	 * 
	 * @return number of data sources
	 */
	public int getDatasourceCount() {
		// helper variables
		int count = 0;
		for (TypeCluster tc : type_clusters.values()) {
			// counts and sums the instances
			count += tc.getDatasourceCount();
		}
		return count;
	}

	/**
	 * writes schema statistics
	 */
	public void writeStatistics(String _directory) {
		sWriter.writeStatistics(_directory);
	}

	/**
	 * Writes the schema to a file
	 * 
	 * @param _filename
	 *            Filename
	 */
	public void writeSchema(String _filename) {
		sWriter.writeSchema(_filename);
	}

	/**
	 * Writes schema data sources to a file
	 * 
	 * @param _filename
	 *            Filename
	 */
	public void flushDatasources(String _filename) {
		logger.info("Start writing output file: " + _filename);
		try {
			TripleWriter tw = new TripleWriter(_filename, true);

			// helper variables
			Map<Integer, EquivalenceClass> eq_classes;

			for (TypeCluster tc : type_clusters.values()) {

				// get the equivalence classes
				eq_classes = tc.getEqClasses();

				for (EquivalenceClass eqc : eq_classes.values()) {

					// print datasources
					for (Datasource ds : eqc.getDatasourcesAsSet()) {
						tw.printTriple(
								'<' + eqc.getURI() + '>',
								'<' + SchemaConstants.DATASOURCE_PREDICATE_URI + '>',
								'<' + ds.getURI() + '>');
					}

					// flush datasources
					eqc.flushDatasources();
				}

			}

			// write explicit RDFS triples
			for (Triple t : getRDFSTriples()) {
				tw.printTriple(t.getSubject().toN3(), t.getPredicate().toN3(),
						t.getObject().toN3());
			}
			// close the file writer
			tw.close();

			logger.info("Schema data sources flushed to file.");
		} catch (IOException e) {
			logger.fatal("Could not write to: " + _filename);
		}
	}

	/**
	 * Flushes schema concepts below a threshold (number of instances) to a file
	 * 
	 * @param _filename
	 *            Filename
	 */
	public void flushSchema(String _filename, FlushStrategy fs) {
		MultiMap<Integer, EquivalenceClass> m = sWriter.flushSchema(_filename,
				fs);

		deleteTcEqc(m);
		m.clear();
		m = null;
	}

	/**
	 * Writes all type-clusters to a CSV file
	 * 
	 * @param _filename
	 *            Filename
	 */
	public void writeTypeClusterCSV(String _filename) {
		sWriter.writeTypeClusterCSV(_filename);
	}

	/**
	 * Writes all type-cluster URIs to a file
	 * 
	 * @param _filename
	 *            Filename
	 */
	public void writeTypeClusterURIs(String _filename) {
		sWriter.writeTypeClusterURIs(_filename);
	}

	/**
	 * Writes all equivalence-class URIs to a file
	 * 
	 * @param _filename
	 *            Filename
	 */
	public void writeEquivalenceClassURIs(String _filename) {
		sWriter.writeEquivalenceClassURIs(_filename);
	}

	public void writeInstancesPerEQCDatasources(String _filename) {
		sWriter.writeInstancesPerEQCDatasources(_filename);
	}

	public void deleteTcEqc(MultiMap<Integer, EquivalenceClass> tcHashToEqc) {

		if (tcHashToEqc == null)
			return;

		for (Entry<Integer, Set<EquivalenceClass>> entry : tcHashToEqc
				.entrySet()) {

			TypeCluster tc = getTypeCluster(entry.getKey());
			for (EquivalenceClass e : entry.getValue()) {
				// flush datasources
				e.flushDatasources();
				// delete eqc
				tc.flushEqClass(e);
			}
			// if type cluster doesn't contain any more eqcs, delete it
			// if(tc.eq_classes.size() == 0)
			// {
			// schema.removeTypeCluster(tc);
			// }
		}

	}

}
