package de.uni_koblenz.schemex.schema;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Triple;

import de.uni_koblenz.schemex.SchemEx;
import de.uni_koblenz.schemex.cache.Datasource;
import de.uni_koblenz.schemex.cache.Link;
import de.uni_koblenz.schemex.util.Hash;
import de.uni_koblenz.schemex.util.NodeMethods;
import de.uni_koblenz.schemex.util.maps.MultiHashMap;
import de.uni_koblenz.schemex.util.maps.MultiMap;
import de.uni_koblenz.schemex.util.strategies.FlushStrategy;

public class SimpleBTC2011Writer extends SchemaWriterSkeletonStatistics {

	public SimpleBTC2011Writer(Schema schema) {
		super(schema);
	}

	@Override
	public void writeSchema(String _filename) {

		logger.info("Start writing output file: " + _filename);
		Map<Integer, TypeCluster> type_clusters = schema.type_clusters;
		try {
			TripleWriter tw = new TripleWriter(_filename, append);

			// helper variables
			TypeCluster tc;
			EquivalenceClass eqc;
			Map<Integer, EquivalenceClass> eq_classes;

			for (Map.Entry<Integer, TypeCluster> tc_entry : type_clusters
					.entrySet()) {
				tc = tc_entry.getValue();
				String tcURI = SchemaConstants.TC_URI_PREFIX
						+ tc.getTypesPostfix();
				tw.printTriple('<' + tcURI + '>',
						'<' + SchemaConstants.RDF_TYPE + '>', "<"
								+ SchemaConstants.TC_TYPE + ">");
				// print the types of the type cluster
				for (String type : tc.getTypes()) {
					tw.printTriple(
							'<' + tcURI + '>',
							'<' + SchemaConstants.TC_TO_CLASS_PREDICATE_URI + '>',
							"<" + type + ">");
				}

				// get the equivalence classes
				eq_classes = tc.getEqClasses();

				for (Map.Entry<Integer, EquivalenceClass> eqc_entry : eq_classes
						.entrySet()) {
					eqc = eqc_entry.getValue();
					String eqcURI = SchemaConstants.EQC_URI_PREFIX
							+ eqc.getPostfix();
					tw.printTriple('<' + eqcURI + '>',
							'<' + SchemaConstants.RDF_TYPE + '>', "<"
									+ SchemaConstants.EQC_TC_TYPE + ">");
					// type cluster has equivalence class
					tw.printTriple(
							'<' + tcURI + '>',
							'<' + SchemaConstants.TC_TO_EQC_PREDICATE_URI + '>',
							'<' + eqcURI + '>');

					// print outgoing links
					Iterator<Link> link_itr = eqc.getLinks().iterator();
					Link l;

					while (link_itr.hasNext()) {
						l = link_itr.next();

						// Triples describing the relation of a ECQ and a
						// Typecluster

						// Literal types will not appear in the list of type
						// clusters, but they can show up in links
						String objString = l.getObject().toString();
						if (!NodeMethods.isLiteralURI(objString)) {
							objString = "<" + SchemaConstants.TC_URI_PREFIX
									+ l.getObject().toString() + ">";
						} else
							objString = l.getObject().toN3();
						tw.printTriple('<' + eqcURI + '>', l.getProperty()
								.toN3(), objString);

					}
					// print datasources
					for (Datasource ds : eqc.getDatasourcesAsSet()) {
						tw.printTriple(
								'<' + eqcURI + '>',
								'<' + SchemaConstants.DATASOURCE_PREDICATE_URI + '>',
								'<' + ds.getURI() + '>');
					}
				}

			}

			// write explicit RDFS triples
			for (Triple t : schema.getRDFSTriples()) {
				tw.printTriple(t.getSubject().toN3(), t.getPredicate().toN3(),
						t.getObject().toN3());
			}
			// close the file writer
			tw.close();
			logger.info("File written.");
		} catch (IOException e) {
			logger.fatal("Could not write to: " + _filename);
		}

	}

	@Override
	public MultiMap<Integer, EquivalenceClass> flushSchema(String _filename,
			FlushStrategy fs) {

		logger.info("Start writing output file: " + _filename);
		try {
			TripleWriter tw = new TripleWriter(_filename, true);

			// helper variables
			// Map<Integer, TypeCluster> tc_delete = new HashMap<Integer,
			// TypeCluster>();
			MultiMap<Integer, EquivalenceClass> eqc_delete = new MultiHashMap<Integer, EquivalenceClass>();
			TypeCluster tc;
			EquivalenceClass eqc;
			Map<Integer, EquivalenceClass> eq_classes;

			// Set up the strategy
			fs.setUp(schema.type_clusters.values());

			for (Map.Entry<Integer, TypeCluster> tc_entry : schema.type_clusters
					.entrySet()) {
				tc = tc_entry.getValue();
				String tcURI = SchemaConstants.TC_URI_PREFIX
						+ tc.getTypesPostfix();
				// get the equivalence classes
				eq_classes = tc.getEqClasses();

				for (Map.Entry<Integer, EquivalenceClass> eqc_entry : eq_classes
						.entrySet()) {
					eqc = eqc_entry.getValue();
					String eqcURI = SchemaConstants.EQC_URI_PREFIX
							+ eqc.getPostfix();
					// Check strategy, if the equivalence class is flushable
					if (fs.isFlushable(eqc)) {

						String eqcHash = "eq"
								+ Hash.md5(Integer.toString(eqc.getLinks()
										.hashCode() - eqc.type_cluster_hash));

						tw.printTriple('<' + eqcURI + '>',
								'<' + SchemaConstants.RDF_TYPE + '>', "<"
										+ SchemaConstants.EQC_TC_TYPE + ">");

						// type cluster has equivalence class
						tw.printTriple(
								'<' + tcURI + '>',
								'<' + SchemaConstants.TC_TO_EQC_PREDICATE_URI + '>',
								'<' + eqcURI + '>');

						// print outgoing links
						Iterator<Link> link_itr = eqc.getLinks().iterator();
						Link l;

						while (link_itr.hasNext()) {
							l = link_itr.next();

							// Triples describing the relation of a ECQ and a
							// Typecluster

							// Literal types will not appear in the list of type
							// clusters, but they can show up in links
							String objString = l.getObject().toString();
							if (!NodeMethods.isLiteralURI(objString)) {
								objString = "<" + SchemaConstants.TC_URI_PREFIX
										+ l.getObject().toString() + ">";
							} else
								objString = l.getObject().toN3();

							tw.printTriple('<' + eqcURI + '>', l.getProperty()
									.toN3(), objString);
						}
						// print datasources
						for (Datasource ds : eqc.getDatasourcesAsSet()) {
							tw.printTriple(
									'<' + eqcURI + '>',
									'<' + SchemaConstants.DATASOURCE_PREDICATE_URI + '>',
									'<' + ds.getURI() + '>');
						}

						// remove equivalence class from type cluster
						// adding to the "to delete" map
						eqc_delete.put(tc.hashCode(), eqc);

					}

				}

				// write explicit RDFS triples
				for (Triple t : schema.getRDFSTriples()) {
					tw.printTriple(t.getSubject().toN3(), t.getPredicate()
							.toN3(), t.getObject().toN3());
				}
				// close the file writer
			}

			// perform finishing operations
			fs.finishUp();

			tw.close();
			logger.info("Schema flushed to file.");
			return eqc_delete;
		} catch (IOException e) {
			logger.fatal("Could not write to: " + _filename);
		}
		return null;

	}

	@Override
	public String toString() {

		return "SimpleBTC2011Writer";
	}
}
