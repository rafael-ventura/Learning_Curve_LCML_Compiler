package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import compiler.DefaultReader;
import compiler.Parser;

public class Main {
	public static void main(String[] args) {
		final var inputFile = args[0];
		final var outputFile = args[1];

		try (final var reader = new FileReader(inputFile);
				final var writer = new PrintStream(outputFile);) {

			final var textReader = new DefaultReader(reader);
			final var parser = new Parser(textReader, writer);

			boolean resultado = parser.compile();
			if (resultado == true) {
				System.out.println("Conversão para HTML concluída.");
			}
		} catch (FileNotFoundException e) {
			System.out.printf("Arquivo %s não encontrado!\n", outputFile);
		} catch (IOException e) {
			System.out.printf("Erro de leitura no arquivo %s!\n", outputFile);
		}
	}

}
