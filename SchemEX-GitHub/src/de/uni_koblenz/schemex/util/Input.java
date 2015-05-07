/**
 * Detec input streams (gz,gzip,bz2,zip)
 */
package de.uni_koblenz.schemex.util;

import java.io.*;
import java.util.zip.ZipInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/**
 * @author Mathias
 * 
 */
public class Input {

	private String _filename;

	public String get_filename() {
		return _filename;
	}

	public void set_filename(String _filename) {
		this._filename = _filename;
	}

	public Input() {
		_filename = null;
	}

	public Input(String _filename) {
		this._filename = _filename;
	}

	/**
	 * Detects the adequate input stream depending on the archive type (gz,
	 * gzip, bz2, zip). The method will use the currently set filename. If it
	 * has not been set, a {@link FileNotFoundException} will be thrown
	 * 
	 * @return inputstream of the set filename
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		return getInputStream(_filename);
	}

	/**
	 * Detects the adequate input stream depending on the archive type (gz,
	 * gzip, bz2, zip)
	 * 
	 * @param _filename
	 *            name of the input file.
	 * @return inputstream of the given filename
	 * @throws IOException
	 */
	public InputStream getInputStream(String _filename) throws IOException {
		File file = new File(_filename);
		if (!file.exists()) {
			throw new FileNotFoundException("File not found: " + _filename);
		}
		// create filereader
		InputStream input = new BufferedInputStream(new FileInputStream(
				_filename));
		// uncompress bz2-files
		if (_filename.contains(".bz2")) {
			input = new BZip2CompressorInputStream(input);
		}
		// uncompress gzip-files
		if (_filename.contains(".gz") || _filename.contains(".gzip")) {
			input = new GzipCompressorInputStream(input);
		}
		// uncompress zip-files
		if (_filename.contains(".zip")) {
			input = new ZipInputStream(input);
		}
		return input;

	}

}
