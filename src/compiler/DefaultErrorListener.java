package compiler;

import compiler.adapter.ErrorListener;

public class DefaultErrorListener implements ErrorListener {

	@Override
	public void syntaxError(final String errorMsg, final String line, final int lineNumber, final int position) {
		if (position == -1)
			System.err.printf("Error at line %d: %s\n", lineNumber, errorMsg);
		else
			System.err.printf("Error at line %d (position %d): %s\n", lineNumber, position, errorMsg);

		if (line != null)
			System.err.printf("(%s)\n", line);
	}

	@Override
	public void semanticError(String errorMsg) {
		System.err.printf("Erro: %s", errorMsg);
	}
}
