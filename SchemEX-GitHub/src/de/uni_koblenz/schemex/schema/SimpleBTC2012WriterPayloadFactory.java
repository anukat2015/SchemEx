package de.uni_koblenz.schemex.schema;

public class SimpleBTC2012WriterPayloadFactory extends SchemaWriterFactory {

	@Override
	public SchemaWriter getSchemaWriter(Schema schema) {
		return new SimpleBTC2012WriterPayload(schema);
	}

}
