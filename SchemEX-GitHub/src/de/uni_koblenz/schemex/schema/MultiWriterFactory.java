package de.uni_koblenz.schemex.schema;

import java.util.ArrayList;

import java.util.List;

/**
 * This implementation holds a List of {@link SchemaWriterFactory}s, which are
 * used to provide multiple Schema outputs with just one SchemEx operation.
 * As of now, using the flush option for this won't work
 */
public class MultiWriterFactory extends SchemaWriterFactory {

	List<SchemaWriterFactory> facs;

	public MultiWriterFactory() {
		facs = new ArrayList<SchemaWriterFactory>();
	}

	/**
	 * 
	 * @param facs
	 *            A List containing {@link SchemaWriterFactory}s to be used
	 *            later in the writer
	 */
	public MultiWriterFactory(List<SchemaWriterFactory> facs) {
		this.facs = facs;
	}

	/**
	 * Adds a single {@link SchemaWriterFactory} to the list held by this factory
	 * @param fac
	 */
	public void addWriterFactory(SchemaWriterFactory fac) {
		facs.add(fac);
	}

	/**
	 * @return A {@link MultiSchemaWriter} instance, which uses the
	 *         {@link SchemaWriterFactory}s specified before the call to this
	 *         method
	 */
	@Override
	public SchemaWriter getSchemaWriter(Schema schema) {

		return new MultiSchemaWriter(schema, facs);
	}

}
