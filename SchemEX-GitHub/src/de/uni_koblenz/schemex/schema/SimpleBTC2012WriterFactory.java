package de.uni_koblenz.schemex.schema;

public class SimpleBTC2012WriterFactory extends SchemaWriterFactory {

	@Override
	public SchemaWriter getSchemaWriter(Schema schema) {
		
		return new SimpleBTC2012Writer(schema);
	}

}
