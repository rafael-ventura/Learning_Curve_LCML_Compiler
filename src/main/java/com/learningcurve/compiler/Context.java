package com.learningcurve.compiler;

import java.util.EnumSet;

/**
 * Tipo enumerado que representa os contextos do analisador sintático
 */
public enum Context {
	DEFAULT,
	PARAGRAPH,
	CODE,
	LIST,
	CONCEPTS,
	TABLE,
	SCENARIO,
	QUESTION;
	
	/**
	 * Método helper que gera um EnumSet com todos os contextos
	 * 
	 * @return COnjunto com todos os contextos
	 */
	public static EnumSet<Context> ALL() {
		return EnumSet.of(DEFAULT, PARAGRAPH, CODE, LIST, CONCEPTS, TABLE, SCENARIO, QUESTION);
	}
	
}
