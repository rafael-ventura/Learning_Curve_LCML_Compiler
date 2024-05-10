package com.learningcurve.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;


import com.learningcurve.compiler.DefaultReader;
import com.learningcurve.compiler.Parser;
import com.learningcurve.compiler.JsonErrorListener;


public class Main {
    public static void main(String[] args) {
        final var inputFile = args[0];
        final var outputFile = args[1];

        try (final var reader = new FileReader(inputFile);
             final var writer = new PrintStream(outputFile);) {

            // Passar o nosso jsonErrorListener para o parser
            final var jsonErrorListener = new JsonErrorListener();
            final var textReader = new DefaultReader(reader);
            final var parser = new Parser(textReader, writer, jsonErrorListener);

            boolean resultado = parser.compile();
            if (resultado == true) {
                System.out.println("Conversão para HTML concluída.");
            } else {
                if (!jsonErrorListener.getErrors().isEmpty()) {
                    jsonErrorListener.saveErrors();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.printf("Arquivo %s não encontrado!\n", outputFile);
        } catch (IOException e) {
            System.out.printf("Erro de leitura no arquivo %s!\n", outputFile);
        }
    }

}
