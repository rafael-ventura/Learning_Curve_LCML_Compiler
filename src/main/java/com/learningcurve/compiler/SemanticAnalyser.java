package com.learningcurve.compiler;

import java.util.List;

import com.learningcurve.compiler.adapter.ErrorListener;

public class SemanticAnalyser {
    private int errorCounter = 0;
    private JsonErrorListener errorListener;
    private int correctAnswerCounter = 0;
    private int optionCounter = 0;

    private void addError(String mensagem) {
        this.errorListener.semanticError(mensagem);
        this.errorCounter++;
    }

    /**
     * Analisa a estrutura sintática para verificar se a sua semântica está correta.
     * Por enquanto, apenas na estrutura de
     * Questão, até o momento, possui condições que apesar de não serem erradas do
     * ponto de vista sintático, não fazem
     * sentido do ponto de vista semântico.
     *
     * @param syntax
     * @return errorCounter
     */
    public int analyse(List<TokenNode> syntax, JsonErrorListener errorListener) {
        this.errorListener = errorListener;

        for (int i = 0; i < syntax.size(); i++) {
            switch (syntax.get(i).type()) {
                case ANSWER:
                    this.optionCounter++;
                    if (syntax.get(i).params().get("iscorrect").left() != "") {
                        if (++this.correctAnswerCounter > 1) {
                            this.addError("Uma questão não pode ter mais de uma alternativa correta.");
                        }
                    }

                    break;
                case END_QUESTION:
                    if (this.optionCounter == 1) {
                        this.addError("Uma questão deve ter mais de uma opção.");
                    } else if (this.correctAnswerCounter == 0) {
                        this.addError("Uma questão deve ter ao menos uma alternativa correta.");
                    }
                    this.correctAnswerCounter = 0;
                    this.optionCounter = 0;
                    break;
                default:
                    break;
            }
        }

        return this.errorCounter;

    }
}
