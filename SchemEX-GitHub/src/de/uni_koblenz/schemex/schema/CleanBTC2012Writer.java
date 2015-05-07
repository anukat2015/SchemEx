package de.uni_koblenz.schemex.schema;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.yars.nx.Triple;

import de.uni_koblenz.schemex.SchemEx;
import de.uni_koblenz.schemex.cache.Datasource;
import de.uni_koblenz.schemex.cache.Link;
import de.uni_koblenz.schemex.util.Hash;
import de.uni_koblenz.schemex.util.NodeMethods;
import de.uni_koblenz.schemex.util.maps.MultiHashMap;
import de.uni_koblenz.schemex.util.maps.MultiMap;
import de.uni_koblenz.schemex.util.strategies.FlushStrategy;

/**
 * A {@link SchemaWriter} with clean RDF-output of a given {@link Schema}
 * 
 * 
 */
public class CleanBTC2012Writer extends SchemaWriterSkeletonStatistics {
	/**
	 * 
	 * @param schema
	 *            Schema to be used
	 */
	public CleanBTC2012Writer(Schema schema) {
		super(schema);
	}

	@Override
	public void writeSchema(String _filename) {
		Map<Integer, TypeCluster> type_clusters = schema.type_clusters;

		logger.info("Start writing output file: " + _filename);
		try {
			TripleWriter tw = new TripleWriter(_filename, append);

			// helper variables
			TypeCluster tc;
			EquivalenceClass eqc;
			Map<Integer, EquivalenceClass> eq_classes;

			for (Map.Entry<Integer, TypeCluster> tc_entry : type_clusters
					.entrySet()) {
				tc = tc_entry.getValue();
				String tcURI = SchemaBTC2012Constants.TC_URI_PREFIX
						+ tc.getTypesPostfix();

				tw.printTriple('<' + tcURI + '>',
						'<' + SchemaBTC2012Constants.RDF_TYPE + '>', "<"
								+ SchemaBTC2012Constants.TC_TYPE + ">");

				// print the types of the type cluster
				for (String type : tc.getTypes()) {
					tw.printTriple(
							'<' + tcURI + '>',
							'<' + SchemaBTC2012Constants.TC_TO_CLASS_PREDICATE_URI + '>',
							"<" + type + ">");
				}
				// print instance count
				tw.printTriple(
						'<' + tcURI + '>',
						'<' + SchemaBTC2012Constants.INSTANCE_COUNT_PREDICATE_URI + '>',
						'"' + Integer.toString(tc.getInstanceCount()) + '"');

				// get the equivalence classes
				eq_classes = tc.getEqClasses();

				for (Map.Entry<Integer, EquivalenceClass> eqc_entry : eq_classes
						.entrySet()) {
					eqc = eqc_entry.getValue();
					String eqcURI = SchemaBTC2012Constants.EQC_URI_PREFIX
							+ eqc.getPostfix();
					tw.printTriple('<' + eqcURI + '>',
							'<' + SchemaBTC2012Constants.RDF_TYPE + '>', "<"
									+ SchemaBTC2012Constants.EQC_TYPE + ">");

					// type cluster has equivalence class
					tw.printTriple(
							'<' + tcURI + '>',
							'<' + SchemaBTC2012Constants.TC_TO_EQC_PREDICATE_URI + '>',
							'<' + eqcURI + '>');
					// print instance count
					tw.printTriple(
							'<' + eqcURI + '>',
							'<' + SchemaBTC2012Constants.INSTANCE_COUNT_PREDICATE_URI + '>',
							'"' + Integer.toString(eqc.getInstanceCount()) + '"');

					// print outgoing links

					String eqcHash = "eq"
							+ Hash.md5(Integer.toString(eqc.getLinks()
									.hashCode() - eqc.type_cluster_hash));

					// Find all Links, that point to the same typecluster. Every
					// links property will be associated with that cluster
					Map<String, Set<String>> links = new HashMap<String, Set<String>>();

					Iterator<Link> link_itr = eqc.getLinks().iterator();
					Link l;
					while (link_itr.hasNext()) {
						l = link_itr.next();

						// Triples describing the relation of a ECQ and a
						// Typecluster

						// The generated URI of the new Linkset

						Set<String> s = links.get(l.getObject().toString());
						if (s == null) {
							Set<String> newS = new HashSet<String>();
							newS.add(l.getProperty().toN3());
							links.put(l.getObject().toString(), newS);
						} else {
							s.add(l.getProperty().toN3());
						}

					}

					// EQC - TC linksets

					for (Entry<String, Set<String>> e : links.entrySet()) {

						// Literal types will not appear in the list of type
						// clusters, but they can show up in links
						String objString = e.getKey();
						if (!NodeMethods.isLiteralURI(objString)) {
							objString = "<"
									+ SchemaBTC2012Constants.TC_URI_PREFIX
									+ e.getKey() + ">";
						} else
							objString = "<" + e.getKey() + ">";
						String linkSetUri = SchemaBTC2012Constants.EQC_TO_TC_LINK_URI_PREFIX
								+ eqcHash
								+ "-"

								+ Hash.md5(Integer.toString(e.getKey()
										.hashCode()));
						tw.printTriple(
								"<" + linkSetUri + ">",
								"<"
										+ SchemaBTC2012Constants.OBJECTS_TARGET_PREDICATE_URI
										+ ">", objString);
						tw.printTriple(
								"<" + linkSetUri + ">",
								"<"
										+ SchemaBTC2012Constants.SUBJECTS_TARGET_PREDICATE_URI
										+ ">", "<" + eqcURI + ">");
						tw.printTriple("<" + linkSetUri + ">", "<"
								+ SchemaBTC2012Constants.RDF_TYPE + ">", "<"
								+ SchemaBTC2012Constants.EQC_TO_TC_LINK_TYPE
								+ ">");

						// print all properties for this EQC - TC connection
						for (String s : e.getValue()) {
							tw.printTriple(
									"<" + linkSetUri + ">",
									"<"
											+ SchemaBTC2012Constants.PROPERTY_PREDICATE_URI
											+ ">", s);
						}

					}

					// EQC - DS linksets
					for (Map.Entry<Datasource, Integer> entry : eqc
							.getDatasources().entrySet()) {
						String linkSetUri = SchemaBTC2012Constants.EQC_TO_DS_LINK_URI_PREFIX
								+ eqcHash
								+ "-"
								+ Hash.md5(Integer.toString(entry.getKey()
										.hashCode()));
						;
						tw.printTriple(
								'<' + eqcURI + '>',
								"<"
										+ SchemaBTC2012Constants.EQC_TO_DS_LINK_PREDICATE
										+ ">", '<' + linkSetUri + '>');
						tw.printTriple(
								'<' + linkSetUri + '>',
								"<"
										+ SchemaBTC2012Constants.EQC_TO_DS_LINK_TO_DS_PREDICATE
										+ ">",
								'<' + entry.getKey().getURI() + '>');

						tw.printTriple(
								"<" + linkSetUri + ">",

								"<"
										+ SchemaBTC2012Constants.TRIPLE_PER_EQC_PER_DS_PROPERTY
										+ ">", "\"" + entry.getValue() + "\"");
						tw.printTriple("<" + linkSetUri + ">", "<"
								+ SchemaBTC2012Constants.RDF_TYPE + ">", "<"
								+ SchemaBTC2012Constants.EQC_TO_DS_LINK_TYPE
								+ ">");
						// Snippets

						Map<String, Map<String, Set<String>>> snippets = eqc
								.getSnippets();

						if (snippets.get(entry.getKey().getURI()) != null) {
							Map<String, Set<String>> map = snippets.get(entry
									.getKey().getURI());

							for (Map.Entry<String, Set<String>> e : map
									.entrySet()) {
								Set<String> snippetSet = e.getValue();
								if (snippetSet != null
										&& snippetSet.size() != 0) {
									tw.printTriple(
											"<" + linkSetUri + ">",
											"<"
													+ SchemaBTC2012Constants.SNIPPET_PROPERTY
													+ ">", "<" + e.getKey()
													+ ">");
									for (String s : snippetSet) {

										tw.printTriple(
												"<" + e.getKey() + ">",
												"<"
														+ SchemaBTC2012Constants.RDFS_LABEL
														+ ">",
												"\""
														+ SchemEx
																.normalizeLiteral(s)
														+ "\"");
									}
								}
							}

						}
						// Old output
						// tw.printTriple(
						// '<' + eqc.getURI() + '>',
						// '<' +
						// SchemaBTC2012Constants.DATASOURCE_PREDICATE_URI
						// + '>',
						// '<' + entry.getKey().getURI() + '>');
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
				String tcURI = SchemaBTC2012Constants.TC_URI_PREFIX
						+ tc.getTypesPostfix();
				// get the equivalence classes
				eq_classes = tc.getEqClasses();

				for (Map.Entry<Integer, EquivalenceClass> eqc_entry : eq_classes
						.entrySet()) {
					eqc = eqc_entry.getValue();
					String eqcURI = SchemaBTC2012Constants.EQC_URI_PREFIX
							+ eqc.getPostfix();
					// Check strategy, if the equivalence class is flushable
					if (fs.isFlushable(eqc)) {
						// print datasources
						String eqcHash = "eq"
								+ Hash.md5(Integer.toString(eqc.getLinks()
										.hashCode() - eqc.type_cluster_hash));

						tw.printTriple('<' + eqcURI + '>',
								'<' + SchemaBTC2012Constants.RDF_TYPE + '>',
								"<" + SchemaBTC2012Constants.EQC_TYPE + ">");

						// type cluster has equivalence class
						tw.printTriple(
								'<' + tcURI + '>',
								'<' + SchemaBTC2012Constants.TC_TO_EQC_PREDICATE_URI + '>',
								'<' + eqcURI + '>');
						// print instance count
						tw.printTriple(
								'<' + eqcURI + '>',
								'<' + SchemaBTC2012Constants.INSTANCE_COUNT_PREDICATE_URI + '>',
								'"' + Integer.toString(eqc.getInstanceCount()) + '"');
						// print outgoing links
						// Find all Links, that point to the same typecluster.
						// Every
						// links property will be associated with that cluster
						Map<String, Set<String>> links = new HashMap<String, Set<String>>();

						Iterator<Link> link_itr = eqc.getLinks().iterator();
						Link l;
						while (link_itr.hasNext()) {
							l = link_itr.next();

							// Triples describing the relation of a ECQ and a
							// Typecluster

							// The generated URI of the new Linkset

							String tcString = l.getObject().toString();

							Set<String> s = links.get(tcString);
							if (s == null) {
								Set<String> newS = new HashSet<String>();
								newS.add(l.getProperty().toN3());
								links.put(tcString, newS);
							} else {
								s.add(l.getProperty().toN3());
							}

						}

						// EQC - TC linksets

						for (Entry<String, Set<String>> e : links.entrySet()) {

							// Literal types will not appear in the list of type
							// clusters, but they can show up in links
							String objString = e.getKey();
							if (!NodeMethods.isLiteralURI(objString)) {
								objString = "<"
										+ SchemaBTC2012Constants.TC_URI_PREFIX
										+ e.getKey() + ">";
							} else
								objString = "<" + e.getKey() + ">";

							String linkSetUri = SchemaBTC2012Constants.EQC_TO_TC_LINK_URI_PREFIX
									+ eqcHash
									+ "-"

									+ Hash.md5(Integer.toString(e.getKey()
											.hashCode()));
							tw.printTriple(
									"<" + linkSetUri + ">",
									"<"
											+ SchemaBTC2012Constants.OBJECTS_TARGET_PREDICATE_URI
											+ ">", objString);
							tw.printTriple(
									"<" + linkSetUri + ">",
									"<"
											+ SchemaBTC2012Constants.SUBJECTS_TARGET_PREDICATE_URI
											+ ">", "<" + eqcURI + ">");
							tw.printTriple(
									"<" + linkSetUri + ">",
									"<" + SchemaBTC2012Constants.RDF_TYPE + ">",
									"<"
											+ SchemaBTC2012Constants.EQC_TO_TC_LINK_TYPE
											+ ">");

							// print all properties for this EQC - TC connection
							for (String s : e.getValue()) {
								tw.printTriple(
										"<" + linkSetUri + ">",
										"<"
												+ SchemaBTC2012Constants.PROPERTY_PREDICATE_URI
												+ ">", s);
							}

						}

						for (Map.Entry<Datasource, Integer> entry : eqc
								.getDatasources().entrySet()) {
							String linkSetUri = SchemaBTC2012Constants.EQC_TO_DS_LINK_URI_PREFIX
									+ eqcHash
									+ "-"
									+ Hash.md5(Integer.toString(entry.getKey()
											.hashCode()));
							;
							tw.printTriple(
									'<' + eqcURI + '>',
									"<"
											+ SchemaBTC2012Constants.EQC_TO_DS_LINK_PREDICATE
											+ ">", '<' + linkSetUri + '>');
							tw.printTriple(
									'<' + linkSetUri + '>',
									"<"
											+ SchemaBTC2012Constants.EQC_TO_DS_LINK_TO_DS_PREDICATE
											+ ">", '<' + entry.getKey()
											.getURI() + '>');

							tw.printTriple(
									"<" + linkSetUri + ">",

									"<"
											+ SchemaBTC2012Constants.TRIPLE_PER_EQC_PER_DS_PROPERTY
											+ ">", "\"" + entry.getValue()
											+ "\"");
							tw.printTriple(
									"<" + linkSetUri + ">",
									"<" + SchemaBTC2012Constants.RDF_TYPE + ">",
									"<"
											+ SchemaBTC2012Constants.EQC_TO_DS_LINK_TYPE
											+ ">");
							// Snippets

							Map<String, Map<String, Set<String>>> snippets = eqc
									.getSnippets();

							if (snippets.get(entry.getKey().getURI()) != null) {
								Map<String, Set<String>> map = snippets
										.get(entry.getKey().getURI());
								for (Map.Entry<String, Set<String>> e : map
										.entrySet()) {
									Set<String> snippetSet = e.getValue();
									if (snippetSet != null
											&& snippetSet.size() != 0) {
										tw.printTriple(
												"<" + linkSetUri + ">",
												"<"
														+ SchemaBTC2012Constants.SNIPPET_PROPERTY
														+ ">", "<" + e.getKey()
														+ ">");
										for (String s : snippetSet) {

											tw.printTriple(
													"<" + e.getKey() + ">",
													"<"
															+ SchemaBTC2012Constants.RDFS_LABEL
															+ ">",
													"\""
															+ SchemEx
																	.normalizeLiteral(s)
															+ "\"");
										}
									}
								}
							}

							// remove equivalence class from type cluster
							// adding to the "to delete" map
							eqc_delete.put(tc.hashCode(), eqc);

							// tc.removeEqClass(eqc);
						}
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

		return "CleanBTC2012Writer";
	}
}
