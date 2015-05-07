package de.uni_koblenz.schemex.schema;

import java.util.ArrayList;
import java.util.List;

import de.uni_koblenz.schemex.util.maps.MultiMap;
import de.uni_koblenz.schemex.util.strategies.FlushStrategy;

/**
 * This implementation of a {@link SchemaWriter} doesn't write anything besides
 * inherited statistics operations by itself. Instead, it encapsulated a list of
 * other {@link SchemaWriter}s, to which the schema-writing methods are then
 * delegated. Filenames will be extended with a number, starting from 0 and
 * incre
 * 
 * @author Bastian
 * 
 */
public class MultiSchemaWriter extends SchemaWriterSkeletonStatistics {

	List<SchemaWriter> writers;

	public MultiSchemaWriter(Schema schema, List<SchemaWriterFactory> facs) {
		super(schema);
		writers = new ArrayList<SchemaWriter>();
		for (SchemaWriterFactory f : facs) {
			writers.add(f.getSchemaWriter(schema));
		}
	}

	@Override
	public void writeSchema(String _filename) {

		String filePrefix = "";
		String fileType = "";
		String fName;
		int dotIndex = _filename.lastIndexOf('.');
		if (dotIndex != -1) {
			filePrefix = _filename.substring(0, dotIndex);
		} else
			filePrefix = _filename;

		if (dotIndex < _filename.length() - 1) {
			fileType = _filename.substring(dotIndex + 1);
		}

		for (int i = 0; i < writers.size(); i++) {
			fName = filePrefix + "_" + writers.get(i) + "." + fileType;
			writers.get(i).writeSchema(fName);
		}
	}

	@Override
	public MultiMap<Integer, EquivalenceClass> flushSchema(String _filename,
			FlushStrategy fs) {

		String filePrefix = "";
		String fileType = "";
		String fName;
		int dotIndex = _filename.lastIndexOf('.');

		if (dotIndex != -1) {
			filePrefix = _filename.substring(0, dotIndex);
		} else
			filePrefix = _filename;

		if (dotIndex < _filename.length() - 1) {
			fileType = _filename.substring(dotIndex + 1);
		}

		MultiMap<Integer, EquivalenceClass> map = null;
		for (int i = 0; i < writers.size(); i++) {
			fName = filePrefix + "_" + writers.get(i) + "." + fileType;
			MultiMap<Integer, EquivalenceClass> tempMap = writers.get(i)
					.flushSchema(fName, fs);

			// just in case one writer fails
			if (tempMap != null)
				map = tempMap;
		}

		return map;

	}

	@Override
	public String toString() {

		return "MultiSchemaWriter";
	}
}
