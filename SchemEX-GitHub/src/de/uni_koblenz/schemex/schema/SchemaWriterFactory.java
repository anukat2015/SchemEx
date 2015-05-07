package de.uni_koblenz.schemex.schema;

/**
 * Abstract superclass of all factories, that produce SchemaWriters.
 * 
 * 
 */
public abstract class SchemaWriterFactory {

	/**
	 * 
	 * @param schema The {@link Schema} object, the writer uses
	 * @return A concrete SchemaWriter implementation, chosen according to the
	 *         factories specification
	 */
	public abstract SchemaWriter getSchemaWriter(Schema schema);

	
	/**
	 * Produces a concrete factory, which produces a {@link CleanBTC2012Writer}
	 * @return A {@link CleanBTC2012WriterFactory} object
	 */
	public static SchemaWriterFactory getCleanBTC2012WriterFactory() {
		return new CleanBTC2012WriterFactory();
	}
	
	public static SchemaWriterFactory getCleanBTC2011WriterFactory()
	{
		return new CleanBTC2011WriterFactory();
	}
	
	public static SchemaWriterFactory getSimpleBTC2012WriterFactory()
	{
		return new SimpleBTC2012WriterFactory();
	}
}
