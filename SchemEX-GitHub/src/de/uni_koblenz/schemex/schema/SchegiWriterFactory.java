package de.uni_koblenz.schemex.schema;

public class SchegiWriterFactory extends SchemaWriterFactory {

	@Override
	public SchemaWriter getSchemaWriter(Schema schema) {
		return new SchegiWriter(schema);
	}

}
