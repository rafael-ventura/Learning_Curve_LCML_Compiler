package com.learningcurve.compiler.adapter;

import java.io.PrintStream;
import java.util.List;

import com.learningcurve.compiler.TokenNode;

public interface Translator {
	
	int translate(List<TokenNode> syntax, PrintStream output, ErrorListener errorListener);
}
