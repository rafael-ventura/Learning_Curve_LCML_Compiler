package com.learningcurve.compiler;


import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import com.learningcurve.compiler.adapter.ErrorListener;

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
     * Salva os erros de compilação num arquivo JSON
     * Caso não haja erros, não faz nada.
     */
    public void saveErrors() {
        var gson = new Gson();
        var json = gson.toJson(errors);

        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = "errors_" + timestamp + ".json";
        try {
            var writer = new java.io.FileWriter("C:\\LearningCurve\\" + filename);
            writer.write(json);
            writer.close();
        } catch (java.io.IOException e) {
            System.err.println("Erro ao salvar os erros em um arquivo JSON");
        }
    }
}