package com.learningcurve.compiler.adapter;

public interface TextReader {
	
	/**
	 * Read and return the next line or null, if eof
	 */
	String readLine();
	
	/**
	 * Returns the current reading position (1, 2, ...) or zero if there was no reading
	 * 
	 */
	int currentLineNumber();
}
