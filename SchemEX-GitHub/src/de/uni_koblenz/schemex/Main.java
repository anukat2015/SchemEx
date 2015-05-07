package de.uni_koblenz.schemex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_koblenz.schemex.cache.CacheConstants;
import de.uni_koblenz.schemex.schema.CleanBTC2011WriterFactory;
import de.uni_koblenz.schemex.schema.CleanBTC2012WriterFactory;
import de.uni_koblenz.schemex.schema.MultiWriterFactory;
import de.uni_koblenz.schemex.schema.SchegiWriterFactory;
import de.uni_koblenz.schemex.schema.SchemaWriterFactory;
import de.uni_koblenz.schemex.schema.SimpleBTC2011WriterFactory;
import de.uni_koblenz.schemex.schema.SimpleBTC2012WriterFactory;
import de.uni_koblenz.schemex.schema.SimpleBTC2012WriterPayloadFactory;
import de.uni_koblenz.schemex.util.Input;
import de.uni_koblenz.schemex.util.strategies.FlushStrategy;
import de.uni_koblenz.schemex.util.strategies.PercentageStrategy;

public class Main {

	private static  Logger logger = LogManager.getRootLogger();

	/**
	 * @param args
	 * @throws Exception
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws Exception {

		/*
		 * command line parser - derived from ldspider project:
		 * http://code.google.com/p/ldspider/
		 */
		// set of all options
		Options options = new Options();

		// input options
		OptionGroup input = new OptionGroup();

		// read an input file
		Option file = new Option("f", "file", true, "location of input file");
		file.setArgName("file");
		input.addOption(file);
		// read a complete directory
		Option dir = new Option("d", "directory", true,
				"location of input directory");
		dir.setArgName("dir");
		input.addOption(dir);
		// there must be set up an input file or directory
		input.setRequired(true);

		options.addOptionGroup(input);

		// processing options - scheme extraction
		OptionGroup process = new OptionGroup();
		// cachesize option
		Option cachesize = new Option("c", "cachesize", true,
				"cache size (max cached number of RDF instances)");
		cachesize.setArgName("int");
		// cachesize.setRequired(true);
		process.addOption(cachesize);

		options.addOptionGroup(process);

		// secondary cachesize option
		Option sec_cachesize = new Option("sc", "sec-cachesize", true,
				"secondary cache size");
		sec_cachesize.setArgName("int");
		options.addOption(sec_cachesize);

		Option output = new Option("o", "output", true, "output folder");
		output.setArgName("folder");
		options.addOption(output);

		Option rdfs = new Option("rdfs", "rdfs", false,
				"extract RDFS triples (domain, range, subPropertyOf, subClassOf)");
		options.addOption(rdfs);

		Option types = new Option("ignoretypes", "ignoretypes", false,
				"ignore all rdf:type triples");
		options.addOption(types);

		Option flush = new Option("flush", "flush", false,
				"flush schema data to file after each processed file.");
		options.addOption(flush);

		Option gold = new Option("g", "gold", false, "gold standard mode");
		options.addOption(gold);

		// CAn be activated in log4j2.xml config file
//		Option debug = new Option("debug", "debug", false, "debug log");
//		options.addOption(debug);

		Option help = new Option("h", "help", false, "print help");
		options.addOption(help);

		// Outputwriter
		Option cleanBTC2012 = new Option("c12", "CleanBTC2012", false,
				"Writes in clean BTC 2012 mode");
		Option cleanBTC2011 = new Option("c11", "CleanBTC2011", false,
				"Writes in clean BTC 2011 mode");
		Option simpleBTC2012 = new Option("s12", "SimpleBTC2012", false,
				"Writes in simple BTC 2012 mode");
		Option simpleBTC2011 = new Option("s11", "SimpleBTC2011", false,
				"Writes in simple BTC 2011 mode");
		Option schegi = new Option("schegi", "Schegi", false,
				"Writes in Schegi mode");
		Option payloadSplit = new Option("ps", "Split Schema and Payload",
				false,
				"Writes schema and payload in seperate files using the simple 2012 schema");
		options.addOption(cleanBTC2011);
		options.addOption(cleanBTC2012);
		options.addOption(simpleBTC2012);
		options.addOption(simpleBTC2011);
		options.addOption(schegi);
		options.addOption(payloadSplit);

		OptionGroup flushing = new OptionGroup();
		Option percFlush = new Option("p", true,
				"Flushes given percentages of equivalence classes");
		flushing.addOption(percFlush);

		options.addOptionGroup(flushing);

		CommandLineParser parser = new BasicParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			// parse the command line arguments with the defined options
			cmd = parser.parse(options, args, true);
			// print help
			if (cmd.hasOption("h") || cmd.hasOption("help")) {
				formatter.printHelp(80, " ",
						"Extract a schema index from NQuads.\n", options, "\n",
						true);
				System.exit(0);
			}

			// everything's fine, run the program
			try {
				run(cmd);
			} catch (IOException e) {
				//
				e.printStackTrace();
			}
		} catch (org.apache.commons.cli.ParseException e) {
			formatter
					.printHelp(
							80,
							" ",
							"ERROR: " + e.getMessage() + "\n",
							options,
							"\nError occured! Please see the error message above",
							true);
			System.exit(-1);
		}

	}

	/**
	 * run-method called by the main-method
	 * 
	 * @param _cmd
	 *            parsed command line options
	 * @throws Exception
	 */
	public static void run(CommandLine _cmd) throws Exception {
		// SchemEx object
		SchemExInterface extractor;

		// parameters
		int cachesize = CacheConstants.STD_CACHESIZE;
		int sec_cachesize = 1;
		ArrayList<String> input_files = new ArrayList<String>();
		String output = "";

		if (_cmd.hasOption("c")) {
			try {
				cachesize = Integer.parseInt(_cmd.getOptionValue("c"));
			} catch (Exception e) {
				System.out.println("Cache size parameter is no integer value: "
						+ e.getMessage());
			}
			logger.info("Cache size: " + cachesize);
		}
		if (_cmd.hasOption("sc")) {

			try {
				sec_cachesize = Integer.parseInt(_cmd.getOptionValue("sc"));
			} catch (Exception e) {
				System.out.println("Cache size parameter is no integer value: "
						+ e.getMessage());
			}
			logger.info("Secondary cache size: " + sec_cachesize);
		}

		// Output writer
		List<SchemaWriterFactory> writerFacs = new ArrayList<SchemaWriterFactory>();
		if (_cmd.hasOption("c11")) {
			writerFacs.add(new CleanBTC2011WriterFactory());
		}
		if (_cmd.hasOption("c12")) {
			writerFacs.add(new CleanBTC2012WriterFactory());
		}
		if (_cmd.hasOption("s11")) {
			writerFacs.add(new SimpleBTC2011WriterFactory());
		}
		if (_cmd.hasOption("s12")) {
			writerFacs.add(new SimpleBTC2012WriterFactory());
		}

		if (_cmd.hasOption("schegi")) {
			writerFacs.add(new SchegiWriterFactory());
		}
		if (_cmd.hasOption("ps")) {
			writerFacs.add(new SimpleBTC2012WriterPayloadFactory());
		}

		// In case, no writer was specified, use the clean version
		// If just one writer was specified, use that one
		// Else use a multiwriter
		SchemaWriterFactory fac;
		if (writerFacs.size() == 0) {
			fac = new CleanBTC2012WriterFactory();
		} else if (writerFacs.size() == 1)
			fac = writerFacs.get(0);
		else
			fac = new MultiWriterFactory(writerFacs);

		FlushStrategy fs;
		if (_cmd.hasOption("p")) {
			fs = new PercentageStrategy(Double.parseDouble(_cmd
					.getOptionValue("p")));
		} else {
			fs = new PercentageStrategy(0.3);
		}
		if (_cmd.hasOption("g")) {
			// create gold standard schema extractor
			extractor = new SchemExGold(cachesize, sec_cachesize, fac, fs);
		} else {
			// create schema extractor
			extractor = new SchemEx(cachesize, sec_cachesize, fac, fs);
		}

		if (_cmd.hasOption("ignoretypes")) {
			extractor.setIgnore_types(true);
		}

		if (_cmd.hasOption("rdfs")) {
			// enable RDFS triple extraction
			extractor.setRDFSExtraction();
		}

		// option d - read and process a directory
		if (_cmd.hasOption("d")) {
			String input_directory = _cmd.getOptionValue("d");
			String input_file = "";
			File dir = new File(input_directory);
			// read all files within the directory
			if (dir.isDirectory()) {
				String files[] = dir.list();
				for (int i = 0; i < files.length; ++i) {
					input_file = input_directory + files[i];
					if (new File(input_file).isFile()) {
						input_files.add(input_file);
					}

				}
			}
		}

		// option f - read and process a file
		if (_cmd.hasOption("f")) {
			String input_filename = _cmd.getOptionValue("f");
			input_files.add(input_filename);
		}

		if (_cmd.hasOption("o")) {
			output = _cmd.getOptionValue("o");

			// create folder, if it does not exist
			File output_folder = new File(output);
			if (!output_folder.exists()) {
				try {
					output_folder.mkdir();
					logger.info("Directory created: " + output);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				logger.info("Directory already exists: " + output);
			}
		}

		Collections.sort(input_files);

		/*
		 * START EXTRACTION PROCESS
		 */
		extractor.startProcess();

		Iterator<String> file_iterator = input_files.iterator();
		int file_count = 0;
		while (file_iterator.hasNext()) {
			file_count++;
			String file = file_iterator.next();
			logger.info("Start processing file: " + file);

			extractor.processStream(new Input(file));
			extractor.printStats();
			if (_cmd.hasOption("flush")) {
				// extractor.flushDatasourcesToFile(output);
				extractor.flushSchemaToFile(output);
			}
			System.gc();
		}

		logger.info("No more files to process.");
		extractor.stopProcess();

		extractor.printStats();
		// if (_cmd.hasOption("flush")) {
		// extractor.flushDatasourcesToFile(output);
		// // extractor.flushSchemaToFile(output, 5);
		// }

		// write schema and CSV files
		logger.info("Write schema.");
		extractor.writeSchemaToFile(output);
		logger.info("Write additional files.");
		extractor.writeAdditionalFiles(output);

		logger.info("Finished.");

	}

}
