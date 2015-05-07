package de.uni_koblenz.schemex.schema;

public class SimpleBTC2011WriterFactory extends SchemaWriterFactory {

	@Override
	public SchemaWriter getSchemaWriter(Schema schema) {

		return new SimpleBTC2011Writer(schema);
	}

}
