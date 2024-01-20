package compiler;

import java.util.Map;

/**
 * Registro que representa um comando e os seus parâmetros
 */
public record TokenNode(TokenType type, Map<String, Pair<String, Integer>> params) {

}
 