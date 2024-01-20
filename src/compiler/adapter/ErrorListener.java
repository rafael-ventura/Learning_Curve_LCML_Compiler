package compiler.adapter;

public interface ErrorListener {

	void syntaxError(final String errorMsg, final String line, final int lineNumber, final int position);

	void semanticError(final String errorMsg);

}
