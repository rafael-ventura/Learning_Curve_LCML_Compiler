package com.learningcurve.compiler;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Classe que analisa uma linha do conteúdo e define o seu tipo
 * e os prâmetros
 */
public class Lexer {

	// Lista de comandos da linguagem
	// Cada comando é formado por um token e pela expressão regular que o define
	private List<Command> commands;

	public Lexer() {
		commands = new ArrayList<>();

		// Cria a lista de comandos. A ordem é importante!
		commands.add(new Command(TokenType.HEADER1,
				"^#1[\\s]+(?<title>.+)$",
				EnumSet.of(Context.DEFAULT)));
		commands.add(new Command(TokenType.HEADER2,
				"^#2[\\s]+(?<title>.+)$",
				EnumSet.of(Context.DEFAULT)));
		commands.add(new Command(TokenType.HEADER3,
				"^#3[\\s]+(?<title>.+)$",
				EnumSet.of(Context.DEFAULT)));
		commands.add(new Command(TokenType.IMAGE,
				"^#img[\\s]+(?<url>[^\\s]+)(?:[\\s]+(?<alt>\\\".+\\\"))?(?:[\\s]+(?<width>\\d+)[\\s]+(?<height>\\d+))?[\\s]*$",
				EnumSet.of(Context.DEFAULT, Context.CONCEPTS, Context.SCENARIO, Context.QUESTION)));
		commands.add(new Command(TokenType.VIDEO,
				"^#video[\\s]+(?<url>[^\\s]+)(?:[\\s]+(?<width>\\d+)[\\s]+(?<height>\\d+))?[\\s]*$",
				EnumSet.of(Context.DEFAULT, Context.CONCEPTS, Context.SCENARIO, Context.QUESTION)));
		commands.add(new Command(TokenType.BEGIN_SOURCE,
				"^#code[\\s]*$",
				EnumSet.of(Context.DEFAULT, Context.CONCEPTS, Context.SCENARIO, Context.QUESTION)));
		commands.add(new Command(TokenType.END_SOURCE,
				"^#code[\\s]*$",
				EnumSet.of(Context.CODE)));
		commands.add(new Command(TokenType.SOURCE_CODE,
				"^(?<source>.*)$",
				EnumSet.of(Context.CODE)));
		commands.add(new Command(TokenType.BEGIN_LIST,
				"^#list[\\s]*$",
				EnumSet.of(Context.DEFAULT, Context.CONCEPTS, Context.SCENARIO, Context.QUESTION)));
		commands.add(new Command(TokenType.END_LIST,
				"^#list[\\s]*$",
				EnumSet.of(Context.LIST)));
		commands.add(new Command(TokenType.LIST_ITEM,
				"^(?<item>.+)$",
				EnumSet.of(Context.LIST)));
		commands.add(new Command(TokenType.BEGIN_CONCEPTS,
				"^#concepts[\\s]*$",
				EnumSet.of(Context.DEFAULT)));
		commands.add(new Command(TokenType.END_CONCEPTS,
				"^#concepts[\\s]*$",
				EnumSet.of(Context.CONCEPTS)));
		commands.add(new Command(TokenType.CONCEPT,
				"^(?:-\\s+)(?<concept>.+)$",
				EnumSet.of(Context.CONCEPTS)));
		commands.add(new Command(TokenType.BEGIN_TABLE,
				"^#table[\\s]*(?<hasborder>border)?$",
				EnumSet.of(Context.DEFAULT, Context.CONCEPTS, Context.SCENARIO, Context.QUESTION)));
		commands.add(new Command(TokenType.END_TABLE,
				"^#table[\\s]*$",
				EnumSet.of(Context.TABLE)));
		commands.add(new Command(TokenType.TABLE_ROW,
				"^(?<cell>[^\\|]+)(\\|[^\\|]+)*$",
				EnumSet.of(Context.TABLE)));
		commands.add(new Command(TokenType.BEGIN_SCENARIO,
				"^#scenario[\\s]*$",
				EnumSet.of(Context.DEFAULT)));
		commands.add(new Command(TokenType.END_SCENARIO,
				"^#scenario[\\s]*$",
				EnumSet.of(Context.SCENARIO)));
		commands.add(new Command(TokenType.STEP,
				"^(?:-\\s+)(?<step>.+)$",
				EnumSet.of(Context.SCENARIO)));
		commands.add(new Command(TokenType.BEGIN_QUESTION,
				"^#question[\\s]*(?<check>([^\\s]+)?)$",
				EnumSet.of(Context.DEFAULT)));
		commands.add(new Command(TokenType.END_QUESTION,
				"^#question[\\s]*$",
				EnumSet.of(Context.QUESTION)));
		commands.add(new Command(TokenType.ANSWER,
				"^(?:\\()(?<iscorrect>\\*?)(?:\\))(?<option>.+)$",
				EnumSet.of(Context.QUESTION)));
		commands.add(new Command(TokenType.BREAK,
				"^(?:\\\\)$",
				EnumSet.of(Context.PARAGRAPH)));
		commands.add(new Command(TokenType.TEXT,
				"^(?<paragraph>[^#(-].+)$",
				EnumSet.of(Context.DEFAULT, Context.PARAGRAPH, Context.CONCEPTS, Context.SCENARIO, Context.QUESTION)));
		commands.add(new Command(TokenType.EMPTY_LINE,
				"^[\\s]*$",
				EnumSet.of(Context.DEFAULT, Context.PARAGRAPH, Context.CONCEPTS, Context.SCENARIO, Context.QUESTION)));

		commands.add(new Command(TokenType.INVALID,
				".*",
				Context.ALL())); // Se não for nenhum dos anteriores, então é ERRO!
	}

	/**
	 * Analisa uma linha e verifica qual o comando associado a ela
	 * 
	 * @param line Linha do programa
	 * @return Um Par contendo: <Token, Mapa>
	 *         O Mapa contém:
	 *         Chave: nome do grupo
	 *         Valor: Par<Texto do grupo, Posição na linha>
	 */
	public Pair<TokenType, Map<String, Pair<String, Integer>>> classify(String line, Context currentContext) {

		// Se a linha é NULO, então chegou no EOF
		if (line == null)
			return new Pair<>(TokenType.EOF, null);

		// Verifica qual comando faz match com a linha
		for (var command : commands) {

			// Verifica se o comando pertence ao contexto atual
			// Se pertence, verifica se a linha contem o comando
			// Senão ignora o comando
			if (command.getContext().contains(currentContext)) {
				var params = (command.getType().compareTo(TokenType.TABLE_ROW) != 0) ? command.match(line)
						: command.matchTable(line);

				if (params != null)
					return new Pair<>(command.getType(), params);
			}
		}

		// A princípio, nunca vai chegar aqui, porque, no pior caso, vai fazer match com
		// INVALID.
		return null;
	}
}
