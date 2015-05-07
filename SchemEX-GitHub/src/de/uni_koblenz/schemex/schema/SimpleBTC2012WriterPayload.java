package de.uni_koblenz.schemex.schema;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.yars.nx.Triple;

import de.uni_koblenz.schemex.SchemExConstants;
import de.uni_koblenz.schemex.cache.Datasource;
import de.uni_koblenz.schemex.cache.Link;
import de.uni_koblenz.schemex.util.Hash;
import de.uni_koblenz.schemex.util.NodeMethods;
import de.uni_koblenz.schemex.util.Pair;
import de.uni_koblenz.schemex.util.maps.MultiHashMap;
import de.uni_koblenz.schemex.util.maps.MultiMap;
import de.uni_koblenz.schemex.util.strategies.FlushStrategy;

public class SimpleBTC2012WriterPayload extends SchemaWriterSkeletonStatistics {

	public SimpleBTC2012WriterPayload(Schema schema) {
		super(schema);

	}

	// public String encodeLiteral(String literal) {
	// String result = literal.replaceAll("\"", "\\\\\"");
	// // if (literal.contains("\"")) {
	// // // Need to replace something ...
	// // System.out.println(" TODO: " + literal);
	// // System.out.println(" DONE: " + result);
	// // }
	// return result;
	// }
	//
	@Override
	public void writeSchema(String _filename) {

		Map<Integer, TypeCluster> type_clusters = schema.type_clusters;

		logger.info("Start writing output file: " + _filename);
		try {
			String schemaName;
			String payloadName;
			String subclassName;
			String fileprefix;
			int lastIndex = _filename.lastIndexOf('.');
			if (lastIndex < 0)
				fileprefix = _filename;
			else {
				fileprefix = _filename.substring(0, lastIndex);
			}

			schemaName = fileprefix + "_schema_s12.nt";
			payloadName = fileprefix + "_payload_s12.nt";
			subclassName = fileprefix + "_subclasses_s12.nt";
			TripleWriter twSchema = new TripleWriter(schemaName, append);
			TripleWriter twPayload = new TripleWriter(payloadName, append);
			TripleWriter twSubclass = new TripleWriter(subclassName, append);

			// Write subclasses
			for (Pair<String, String> p : schema.getSubclasses()) {
				twSubclass.printUriTriple(p.getA(),
						SchemaBTC2012Constants.RDFS_SUBCLASSOF, p.getB());
			}

			// helper variables
			// TypeCluster tc;
			// EquivalenceClass eqc;
			Map<Integer, EquivalenceClass> eq_classes;

			// Print out pseudo-typeclusters for Literal types
			for (String literal : SchemExConstants.LITERAL_TYPES) {
				String tcHash = Hash.md5(literal);

				String tcURI = SchemaBTC2012Constants.TC_URI_PREFIX + tcHash;
				// TC type
				twSchema.printUriTriple(tcURI, SchemaBTC2012Constants.RDF_TYPE,
						SchemaBTC2012Constants.TC_TYPE);

				// TC classes -> just one
				twSchema.printUriTriple(tcURI,
						SchemaBTC2012Constants.TC_TO_CLASS_PREDICATE_URI,
						literal);

				// TC instance count
				twSchema.printLiteralTriple(tcURI,
						SchemaBTC2012Constants.INSTANCE_COUNT_PREDICATE_URI,
						Integer.toString(0));
				// Pseudo-equivalence class
				String eqcHash = Hash.md5(literal + "EquivalenceClass");

				String eqcURI = SchemaBTC2012Constants.EQC_URI_PREFIX + eqcHash;

				// EQC type
				twSchema.printUriTriple(eqcURI,
						SchemaBTC2012Constants.RDF_TYPE,
						SchemaBTC2012Constants.EQC_TYPE);

				// TC to EQC
				twSchema.printUriTriple(tcURI,
						SchemaBTC2012Constants.TC_TO_EQC_PREDICATE_URI, eqcURI);

				// EQC instance count
				twSchema.printLiteralTriple(eqcURI,
						SchemaBTC2012Constants.INSTANCE_COUNT_PREDICATE_URI,
						Integer.toString(0));
			}

			for (TypeCluster tc : type_clusters.values()) {

				String tcURI = SchemaBTC2012Constants.TC_URI_PREFIX
						+ tc.getTypesPostfix();
				twSchema.printUriTriple(tcURI, SchemaBTC2012Constants.RDF_TYPE,
						SchemaBTC2012Constants.TC_TYPE);

				// print the types of the type cluster
				for (String type : tc.getTypes()) {
					twSchema.printUriTriple(tcURI,
							SchemaBTC2012Constants.TC_TO_CLASS_PREDICATE_URI,
							type);
				}
				// print instance count
				twSchema.printLiteralTriple(tcURI,
						SchemaBTC2012Constants.INSTANCE_COUNT_PREDICATE_URI,
						Integer.toString(tc.getInstanceCount()));

				// get the equivalence classes
				eq_classes = tc.getEqClasses();

				for (EquivalenceClass eqc : eq_classes.values()) {
					String eqcURI = SchemaBTC2012Constants.EQC_URI_PREFIX
							+ eqc.getPostfix();
					twSchema.printUriTriple(eqcURI,
							SchemaBTC2012Constants.RDF_TYPE,
							SchemaBTC2012Constants.EQC_TYPE);

					// type cluster has equivalence class
					twSchema.printUriTriple(tcURI,
							SchemaBTC2012Constants.TC_TO_EQC_PREDICATE_URI,
							eqcURI);
					// print instance count
					twSchema.printLiteralTriple(
							eqcURI,
							SchemaBTC2012Constants.INSTANCE_COUNT_PREDICATE_URI,
							Integer.toString(eqc.getInstanceCount()));
					// print outgoing links
					Iterator<Link> link_itr = eqc.getLinks().iterator();
					Link l;
					String eqcHash = "eq"
							+ Hash.md5(Integer.toString(eqc.getLinks()
									.hashCode() - eqc.type_cluster_hash));
					while (link_itr.hasNext()) {
						l = link_itr.next();

						// Triples describing the relation of a ECQ and a
						// Typecluster

						String objString = l.getObject().toString();
						if (!NodeMethods.isLiteralURI(objString)) {
							objString = "<"
									+ SchemaBTC2012Constants.TC_URI_PREFIX
									+ l.getObject().toString() + ">";

						} else {
							// objString = l.getObject().toN3();
							objString = "<"
									+ SchemaBTC2012Constants.TC_URI_PREFIX
									+ Hash.md5(l.getObject().toString()) + ">";
						}

						twSchema.printTriple('<' + eqcURI + '>', l
								.getProperty().toN3(), objString);
					}
					// EQC - DS linksets
					for (Map.Entry<Datasource, Integer> entry : eqc
							.getDatasources().entrySet()) {

						String linkSetUri = SchemaBTC2012Constants.EQC_TO_DS_LINK_URI_PREFIX
								+ eqcHash
								+ "-"
								+ Hash.md5(Integer.toString(entry.getKey()
										.hashCode()));

						twSchema.printUriTriple(
								eqcURI,
								SchemaBTC2012Constants.EQC_TO_DS_LINK_PREDICATE,
								linkSetUri);
						twPayload
								.printUriTriple(
										linkSetUri,
										SchemaBTC2012Constants.EQC_TO_DS_LINK_TO_DS_PREDICATE,
										entry.getKey().getURI());

						twPayload
								.printLiteralTriple(
										linkSetUri,
										SchemaBTC2012Constants.TRIPLE_PER_EQC_PER_DS_PROPERTY,
										Integer.toString(entry.getValue()));

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
									twPayload
											.printUriTriple(
													linkSetUri,
													SchemaBTC2012Constants.SNIPPET_PROPERTY,
													e.getKey());
									for (String s : snippetSet) {
										twPayload
												.printLiteralTriple(
														e.getKey(),
														SchemaBTC2012Constants.RDFS_LABEL,
														s);
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
				twSchema.printTriple(t.getSubject().toN3(), t.getPredicate()
						.toN3(), t.getObject().toN3());
			}
			// close the file writer
			twPayload.close();
			twSchema.close();
			twSubclass.close();
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
			String schemaName;
			String payloadName;
			String fileprefix;
			int lastIndex = _filename.lastIndexOf('.');
			if (lastIndex < 0)
				fileprefix = _filename;
			else {
				fileprefix = _filename.substring(0, lastIndex);
			}

			schemaName = fileprefix + "_schema_s12.nt";
			payloadName = fileprefix + "_payload_s12.nt";
			TripleWriter twSchema = new TripleWriter(schemaName, append);
			TripleWriter twPayload = new TripleWriter(payloadName, append);
			// helper variables
			// Map<Integer, TypeCluster> tc_delete = new HashMap<Integer,
			// TypeCluster>();
			MultiMap<Integer, EquivalenceClass> eqc_delete = new MultiHashMap<Integer, EquivalenceClass>();
			// TypeCluster tc;
			// EquivalenceClass eqc;
			Map<Integer, EquivalenceClass> eq_classes;

			// Set up the strategy
			fs.setUp(schema.type_clusters.values());

			for (TypeCluster tc : schema.type_clusters.values()) {
				String tcURI = SchemaBTC2012Constants.TC_URI_PREFIX
						+ tc.getTypesPostfix();
				// get the equivalence classes
				eq_classes = tc.getEqClasses();

				for (EquivalenceClass eqc : eq_classes.values()) {

					// Check strategy, if the equivalence class is flushable
					if (fs.isFlushable(eqc)) {
						String eqcURI = SchemaBTC2012Constants.EQC_URI_PREFIX
								+ eqc.getPostfix();
						// print datasources
						String eqcHash = "eq"
								+ Hash.md5(Integer.toString(eqc.getLinks()
										.hashCode() - eqc.type_cluster_hash));

						twSchema.printUriTriple(eqcURI,
								SchemaBTC2012Constants.RDF_TYPE,
								SchemaBTC2012Constants.EQC_TYPE);

						// type cluster has equivalence class
						twSchema.printUriTriple(tcURI,
								SchemaBTC2012Constants.TC_TO_EQC_PREDICATE_URI,
								eqcURI);
						// print instance count
						twSchema.printLiteralTriple(
								eqcURI,
								SchemaBTC2012Constants.INSTANCE_COUNT_PREDICATE_URI,
								Integer.toString(eqc.getInstanceCount()));
						// print outgoing links
						Iterator<Link> link_itr = eqc.getLinks().iterator();
						Link l;

						while (link_itr.hasNext()) {
							l = link_itr.next();

							String objString = l.getObject().toString();
							if (!NodeMethods.isLiteralURI(objString)) {
								objString = "<"
										+ SchemaBTC2012Constants.TC_URI_PREFIX
										+ l.getObject().toString() + ">";
							} else {
								objString = "<"
										+ SchemaBTC2012Constants.TC_URI_PREFIX
										+ Hash.md5(l.getObject().toString())
										+ ">";
							}

							// Triples describing the relation of a ECQ and a
							// Typecluster

							twSchema.printTriple('<' + eqcURI + '>', l
									.getProperty().toN3(), objString);

						}

						for (Map.Entry<Datasource, Integer> entry : eqc
								.getDatasources().entrySet()) {
							String linkSetUri = SchemaBTC2012Constants.EQC_TO_DS_LINK_URI_PREFIX
									+ eqcHash
									+ "-"
									+ Hash.md5(Integer.toString(entry.getKey()
											.hashCode()));
							;
							twSchema.printUriTriple(
									eqcURI,
									SchemaBTC2012Constants.EQC_TO_DS_LINK_PREDICATE,
									linkSetUri);
							twPayload
									.printUriTriple(
											linkSetUri,
											SchemaBTC2012Constants.EQC_TO_DS_LINK_TO_DS_PREDICATE,
											entry.getKey().getURI());
							twPayload
									.printLiteralTriple(
											linkSetUri,
											SchemaBTC2012Constants.TRIPLE_PER_EQC_PER_DS_PROPERTY,
											Integer.toString(entry.getValue()));
							twSchema.printUriTriple(linkSetUri,
									SchemaBTC2012Constants.RDF_TYPE,
									SchemaBTC2012Constants.EQC_TO_DS_LINK_TYPE);
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
										twPayload
												.printUriTriple(
														linkSetUri,
														SchemaBTC2012Constants.SNIPPET_PROPERTY,
														e.getKey());
										for (String s : snippetSet) {

											twPayload
													.printLiteralTriple(
															e.getKey(),
															SchemaBTC2012Constants.RDFS_LABEL,
															s);
										}

									}
								}
							}

							// remove equivalence class from type cluster
							// adding to the "to delete" map
							eqc_delete.put(tc.hashCode(), eqc);

						}
					}

				}

				// write explicit RDFS triples
				for (Triple t : schema.getRDFSTriples()) {
					twSchema.printTriple(t.getSubject().toN3(), t
							.getPredicate().toN3(), t.getObject().toN3());
				}
				// close the file writer
			}

			// perform finishing operations
			fs.finishUp();

			twSchema.close();
			twPayload.close();
			logger.info("Schema flushed to file.");
			return eqc_delete;
		} catch (IOException e) {
			logger.fatal("Could not write to: " + _filename);
		}
		return null;
	}

	@Override
	public String toString() {

		return "SimpleBTC2012Writer_Payload-Schema-Split";
	}
}
