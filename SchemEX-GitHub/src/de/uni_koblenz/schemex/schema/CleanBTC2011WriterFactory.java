package de.uni_koblenz.schemex.schema;

public class CleanBTC2011WriterFactory extends SchemaWriterFactory {

	@Override
	public SchemaWriter getSchemaWriter(Schema schema) {

		return new CleanBTC2011Writer(schema);
	}

}
