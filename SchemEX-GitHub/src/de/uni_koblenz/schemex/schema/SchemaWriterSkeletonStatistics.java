package de.uni_koblenz.schemex.schema;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.*;

import com.csvreader.CsvWriter;

import de.uni_koblenz.schemex.cache.Datasource;
import de.uni_koblenz.schemex.util.ValueArray;

/**
 * Implements standard output for all {@link SchemaWriter} methods, besides
 * writeSchema
 * 
 * 
 */
public class SchemaWriterSkeletonStatistics extends SchemaWriterSkeleton {

	protected static Logger logger = LogManager.getRootLogger();
	protected final Schema schema;

	/**
	 * 
	 * @param schema
	 *            Schema to be used
	 */
	public SchemaWriterSkeletonStatistics(Schema schema) {
		this.schema = schema;
	}

	@Override
	public void writeTypeClusterCSV(String _filename) {
		logger.info("Start writing type-cluster CSV file: " + _filename);
		Map<Integer, TypeCluster> type_clusters = schema.type_clusters;
		try {
			CsvWriter writer = new CsvWriter(new FileWriter(_filename, append),
					',');

			// helper variables
			TypeCluster tc;

			Collection<TypeCluster> tcol = type_clusters.values();
			// obtain an Iterator for Collection
			Iterator<TypeCluster> tc_itr = tcol.iterator();
			// iterate through HashMap
			while (tc_itr.hasNext()) {
				tc = tc_itr.next();
				// print the types of the type cluster
				for (String type : tc.getTypes()) {
					writer.write(type);
				}
				writer.endRecord();
			}
			// close the file writer
			writer.close();
			logger.info("File written.");
		} catch (IOException e) {
			logger.fatal("Could not write to: " + _filename);
		}

	}

	@Override
	public void writeStatistics(String _directory) {
		logger.info("Writing statistics.");

		Map<Integer, TypeCluster> type_clusters = schema.type_clusters;

		// helper variables
		TypeCluster tc;
		EquivalenceClass eqc;

		// value arrays
		ValueArray eqc_per_tc = new ValueArray();
		ValueArray classes_per_tc = new ValueArray();
		ValueArray ds_per_tc = new ValueArray();
		ValueArray ds_per_eqc = new ValueArray();

		Collection<TypeCluster> tcol = type_clusters.values();
		// obtain an Iterator for Collection
		Iterator<TypeCluster> tc_itr = tcol.iterator();
		// iterate through type clusters HashMap
		while (tc_itr.hasNext()) {
			tc = tc_itr.next();
			eqc_per_tc.addValue(tc.getEqClassCount() * 1.0);
			classes_per_tc.addValue(tc.getTypeCount() * 1.0);
			ds_per_tc.addValue(tc.getDatasourceCount() * 1.0);

			Collection<EquivalenceClass> eqc_col = tc.getEqClasses().values();
			// obtain an Iterator for Collection
			Iterator<EquivalenceClass> eqc_itr = eqc_col.iterator();
			// iterate through type clusters HashMap
			while (eqc_itr.hasNext()) {
				eqc = eqc_itr.next();
				ds_per_eqc.addValue(eqc.getDatasourceCount() * 1.0);
			}
		}

		// write statistic files
		eqc_per_tc.toFile(_directory + "/eqc_per_tc.txt");
		classes_per_tc.toFile(_directory + "/classes_per_tc.txt");
		ds_per_tc.toFile(_directory + "/ds_per_tc.txt");
		ds_per_eqc.toFile(_directory + "/ds_per_eqc.txt");
		logger.info("Statistics written.");

	}

	@Override
	public void writeTypeClusterURIs(String _filename) {

		logger.info("Start writing type-cluster URI file: " + _filename);

		Map<Integer, TypeCluster> type_clusters = schema.type_clusters;
		try {

			FileWriter writer = new FileWriter(_filename, append);
			BufferedWriter out = new BufferedWriter(writer);

			// helper variables
			TypeCluster tc;

			Collection<TypeCluster> tcol = type_clusters.values();
			// obtain an Iterator for Collection
			Iterator<TypeCluster> tc_itr = tcol.iterator();
			// iterate through HashMap
			while (tc_itr.hasNext()) {
				tc = tc_itr.next();
				// print the URIs of the type cluster
				out.write(tc.getURI() + "\r\n");
			}
			// close the file writer
			out.close();
			logger.info("File written.");
		} catch (IOException e) {
			logger.fatal("Could not write to: " + _filename);
		}

	}

	@Override
	public void writeEquivalenceClassURIs(String _filename) {
		logger.info("Start writing equivalence class URI file: " + _filename);

		Map<Integer, TypeCluster> type_clusters = schema.type_clusters;
		try {

			FileWriter writer = new FileWriter(_filename, append);
			BufferedWriter out = new BufferedWriter(writer);

			// helper variables
			TypeCluster tc;

			Collection<TypeCluster> tc_col = type_clusters.values();
			// obtain an Iterator for Collection
			Iterator<TypeCluster> tc_itr = tc_col.iterator();
			// iterate through HashMap
			while (tc_itr.hasNext()) {
				tc = tc_itr.next();

				Collection<EquivalenceClass> eqc_col = tc.getEqClasses()
						.values();
				Iterator<EquivalenceClass> eqc_itr = eqc_col.iterator();
				// print the URIs of the equivalence class
				while (eqc_itr.hasNext()) {
					out.write(eqc_itr.next().getURI() + "\r\n");
				}

			}
			// close the file writer
			out.close();
			logger.info("File written.");
		} catch (IOException e) {
			logger.fatal("Could not write to: " + _filename);
		}

	}

	@Override
	public void writeInstancesPerEQCDatasources(String _filename) {

		Map<Integer, TypeCluster> type_clusters = schema.type_clusters;
		try {
			FileWriter writer = new FileWriter(new File(_filename), true);
			for (TypeCluster tq : type_clusters.values()) {
				for (EquivalenceClass eq : tq.eq_classes.values()) {
					writer.append("Equivalence class: " + eq.getURI() + "\n");
					writer.append("--------------------\n");
					for (Map.Entry<Datasource, Integer> entry : eq.datasources
							.entrySet()) {
						writer.append(entry.getKey().getURI() + ": "
								+ entry.getValue() + "\n");
					}
					writer.append("\n");
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
