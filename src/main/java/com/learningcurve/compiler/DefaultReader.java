package com.learningcurve.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.learningcurve.compiler.adapter.TextReader;

public class DefaultReader implements TextReader {

    // Armazena as linhas do arquivo na memória
    private final List<String> memory;

    // Posição de leitura na lista
    private int cursor;

    /**
     * Lê o arquivo inteiro para a memória
     *
     * @param inputReader Entrada de dados no modo texto
     * @throws IOException EM caso de erro de leitura
     */
    public DefaultReader(Reader inputReader) throws IOException {
        memory = new ArrayList<>();

        final var reader = new BufferedReader(inputReader);

        var line = reader.readLine();

        while (line != null) {
            memory.add(line.replaceAll("\\s+$", "")); // Remove os espaços no final da linha
            line = reader.readLine();
        }

        reader.close();

        cursor = -1;
    }

    /**
     * Retorna a próxima linha ou null, se o arquivo terminou
     */
    @Override
    public String readLine() {
        return cursor + 1 < memory.size() ? memory.get(++cursor) : null;
    }

    /**
     * Retorna a posição de leitura atual (1, 2, ...) ou zero, se ainda não foi feita nenhuma leitura
     */
    @Override
    public int currentLineNumber() {
        return cursor + 1;
    }
}
