package com.learningcurve.compiler;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Classe que armazena o token e sua expressão regular
 */
public class Command {

	// Token
	private TokenType type;

	// Expressão regular compilada
	private Pattern pattern;

	// Define em quais contextos esse comando é válido
	private EnumSet<Context> context;

	/**
	 * Cria um novo comando
	 * 
	 * @param type    Tipo do comando
	 * @param regex   Regex que o identifica
	 * @param context Contextos nos quais esse comsndo é válido
	 */
	public Command(TokenType type, String regex, EnumSet<Context> context) {
		this.type = type;
		this.pattern = Pattern.compile(regex);
		this.context = context;
	}

	/**
	 * Retorna o tipo do token
	 * 
	 * @return Tipo do token
	 */
	public TokenType getType() {
		return type;
	}

	/**
	 * Retorna o contextos nos quais o comando é válido
	 * 
	 * @return COntextos válidos para o comando
	 */
	public EnumSet<Context> getContext() {
		return context;
	}

	/**
	 * Tenta fazer o match da linha com a expressão regular
	 * Se coseguir retorna os grupos capturados pela expressão regular
	 * Se não conseguir retorna nulo
	 * 
	 * @param line Linha do programa
	 * @return Mapa com:
	 *         chave: nome do grupo
	 *         valor: par<texto do grupo, posição na linha>
	 */
	public Map<String, Pair<String, Integer>> match(String line) {
		var matcher = pattern.matcher(line);

		if (matcher.find()) {
			Map<String, Pair<String, Integer>> params = new HashMap<>();

			// Monta um Map com todos os grupos do regex. O Map contém: <nome do grupo,
			// <texto, posição>>
			matcher.namedGroups()
					.forEach((name, index) -> params.put(name, new Pair<>(matcher.group(index), matcher.start(index))));

			return params;
		}

		// Se não fez match
		return null;
	}

	/**
	 * Tenta fazer o match da linha com a expressão regular para o caso específico
	 * da tabela
	 * Divide o conteúdo da linha em células da tabela
	 * 
	 * @param line Linha do programa
	 * @return Mapa com:
	 *         chave: nome do grupo
	 *         valor: par<texto do grupo, posição na linha>
	 */
	public Map<String, Pair<String, Integer>> matchTable(String line) {
		var matcher = pattern.matcher(line);
		int count = 0;

		if (matcher.find()) {
			Map<String, Pair<String, Integer>> params = new HashMap<>();
			params.put("cell" + ++count, new Pair<>(matcher.group(1), matcher.start(1)));

			var cellPattern = Pattern.compile("\\|(?<cell>[^\\|]+)");
			matcher = cellPattern.matcher(line);

			while (matcher.find()) {
				params.put("cell" + ++count, new Pair<>(matcher.group(1), matcher.start(1)));
			}
			return params;
		}

		// Se não fez match
		return null;
	}
}
