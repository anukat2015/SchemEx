package de.uni_koblenz.schemex.schema;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.semanticweb.yars.nx.Triple;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import de.uni_koblenz.schemex.cache.Link;
import de.uni_koblenz.schemex.util.Hash;
import de.uni_koblenz.schemex.util.maps.MultiMap;
import de.uni_koblenz.schemex.util.strategies.FlushStrategy;

public class SchegiWriter extends SchemaWriterSkeletonStatistics {

	public final static String PROP_URI_PREFIX = "http://btc2011.west.uni-koblenz.de/prop";
	public final static String PROP_TYPE = SchemaConstants.PROPERTY_PREDICATE_URI;
	

	
	public SchegiWriter(Schema schema) {
		super(schema);
	}

	
	@Override
	public void writeSchema(String _filename) {

		Map<Integer, TypeCluster> type_clusters = schema.type_clusters;

		logger.info("Start writing output file: " + _filename);
		try {
			TripleWriter tw = new TripleWriter(_filename, append);

			// helper variables
//			TypeCluster tc;
//			EquivalenceClass eqc;
			Map<Integer, EquivalenceClass> eq_classes;

			for (TypeCluster tc : type_clusters.values()) {
				tw.printTriple('<' + tc.getURI() + '>',
						'<' + SchemaBTC2012Constants.RDF_TYPE + '>', "<"
								+ SchemaConstants.TC_TYPE + ">");

				// print the types of the type cluster
				for (String type : tc.getTypes()) {
					tw.printTriple(
							'<' + tc.getURI() + '>',
							'<' + SchemaConstants.TC_TO_CLASS_PREDICATE_URI + '>',
							"<" + type + ">");
				}
				// print instance count
				tw.printTriple(
						'<' + tc.getURI() + '>',
						'<' + SchemaConstants.INSTANCE_COUNT_PREDICATE_URI + '>',
						'"' + Integer.toString(tc.getInstanceCount()) + '"');

				// get the equivalence classes
				eq_classes = tc.getEqClasses();

				// Map property to (Map TC to count)
				HashMap<String, HashMap<String,Integer>> propertyCount = new HashMap<String, HashMap<String,Integer>>();
				
				for (EquivalenceClass eqc : eq_classes.values()) {
					Iterator<Link> link_itr = eqc.getLinks().iterator();
					Link l;
					int count = eqc.getInstanceCount();
					while (link_itr.hasNext()) {
						l = link_itr.next();

						String propertyUri = l
								.getProperty().toN3();
						
						String tcTarget = l.getObject().toN3();
						if (! propertyCount.containsKey(propertyUri)) {
							propertyCount.put(propertyUri, new HashMap<String, Integer>());
						}
						HashMap<String, Integer> propertyTargets = propertyCount.get(propertyUri);
						if (! propertyTargets.containsKey(tcTarget)) {
							propertyTargets.put(tcTarget, 0);
						}
						int oldCount = propertyTargets.get(tcTarget);
						propertyTargets.put(tcTarget, oldCount+count);
					}

				}
				
				for (String property : propertyCount.keySet()) {
					HashMap<String, Integer> propertyTargets = propertyCount.get(property);
					for (String tcTarget : propertyTargets.keySet()) {
						int instanceCount =  propertyTargets.get(tcTarget);
						
						// Define link node URI
						String concat = tc.getURI()+" "+property+" "+tcTarget;
						String linkNodeUri = SchegiWriter.PROP_URI_PREFIX+Hash.md5(concat);
						
//						tw.printTriple(
//								'<' + linkNodeUri + '>',
//								'<' + SchemaConstants.RDF_TYPE + '>',
//								'<' + SchegiWriter.PROP_TYPE + '>');

						tw.printTriple(
								'<' + linkNodeUri + '>',
								'<' + SchemaConstants.PROPERTY_PREDICATE_URI + '>',
								property);

						tw.printTriple(
								'<' + linkNodeUri + '>',
								'<' + SchemaConstants.INSTANCE_COUNT_PREDICATE_URI + '>',
								"\"" + instanceCount+'"');

						tw.printTriple(
								'<' + linkNodeUri + '>',
								'<' + SchemaConstants.SUBJECTS_TARGET_PREDICATE_URI + '>',
								'<' + tc.getURI()+ '>');

						tw.printTriple(
								'<' + linkNodeUri + '>',
								'<' + SchemaConstants.OBJECTS_TARGET_PREDICATE_URI + '>',
								tcTarget);
						
					}
				}
				// now flush infos

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
		throw new NotImplementedException();
	}
	
	@Override
	public String toString() {
		
		return "SchegiWriter";
	}

}
