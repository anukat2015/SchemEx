package de.uni_koblenz.schemex.schema;

/**
 * Produces a {@link SchemaWriterFactory}, that produces a
 * {@link CleanBTC2012Writer}
 * 
 * 
 */
public class CleanBTC2012WriterFactory extends SchemaWriterFactory {

	@Override
	public SchemaWriter getSchemaWriter(Schema schema) {
		return new CleanBTC2012Writer(schema);
	}

}
