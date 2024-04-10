package compiler;

import compiler.adapter.ErrorListener;

import java.util.ArrayList;
import java.util.List;

public class JsonErrorListener implements ErrorListener {

    private List<Error> errors = new ArrayList<>();

    @Override
    public void syntaxError(String errorMsg, String line, int lineNum, int position) {
        errors.add(new Error(errorMsg, line, lineNum));
    }

    @Override
    public void semanticError(String errorMsg) {
        errors.add(new Error(errorMsg, null, null));
    }

    public List<Error> getErrors() {
        return errors;
    }


    /**
     * Converte a lista de erros para um JSON
     *
     * @return JSON com a lista de erros
     */
    public String toJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        for (Error error : errors) {
            jsonBuilder.append("{");
            jsonBuilder.append("\"errorMessage\": \"").append(error.getErrorMessage().replace("\"", "\\\"")).append("\"");

            if (error.getLineContent() != null && error.getLineNumber() != null) {
                jsonBuilder.append(", \"lineContent\": \"").append(error.getLineContent().replace("\"", "\\\"")).append("\"");
                jsonBuilder.append(", \"lineNumber\": ").append(error.getLineNumber());
            }

            jsonBuilder.append("},");
        }

        if (!errors.isEmpty()) {
            // Remove a última vírgula
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }

        // Fecha o array
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }
}
