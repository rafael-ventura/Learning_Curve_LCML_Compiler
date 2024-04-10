package compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import compiler.adapter.ErrorListener;
import compiler.adapter.TextReader;
import compiler.adapter.Translator;

/**
 * Classe responsável pela Análise Sintática
 */
public class Parser {

    // Leitor das linhas do programa
    private final TextReader reader;

    // Saída gerada pelo tradutor
    private final PrintStream output;

    // Analisador léxico
    private final Lexer lexer;

    // Analisador semântico
    private final SemanticAnalyser semanticAnalyser;

    // Tratador de erros
    private ErrorListener errorListener;

    // Tratador de erros JSON
    private JsonErrorListener jsonErrorListener;

    // Tradutor
    private Translator translator;

    // Lista de tokens gerada pelo analisador sintático
    private List<TokenNode> tokens;

    // Contador de erros
    private int errorCounter;

    // Pilha de contexto
    private final Deque<Context> context;

    public Parser(TextReader reader, PrintStream output) {
        super();

        this.reader = reader;
        this.output = output;
        this.lexer = new Lexer();
        this.semanticAnalyser = new SemanticAnalyser();
        this.context = new LinkedList<>();

        // Tratador de erro default: imprime erro no console
        this.errorListener = new DefaultErrorListener();

        // Tratador de erro JSON : armazena os erros em uma lista
        this.jsonErrorListener = new JsonErrorListener();

        // Tradutor default: HTML
        this.translator = new HTMLTranslator();
    }

    /**
     * Define um novo tratador de erros
     *
     * @param errorListener Tratador de erros
     */
    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    /**
     * Define um novo tradutor
     *
     * @param translator Tradutor
     */
    public void setTranslator(Translator translator) {
        this.translator = translator;
    }

    /**
     * Compila o programa e indica se houve erro ou não.
     *
     * @return V se não houve erro; e F, caso contrário.
     */
    public boolean compile() {

        errorCounter = 0;
        tokens = new ArrayList<>();

        // Coloca na pilha o contexto DEFAULT que é o contexto atual (inicial)
        // O topo da pilha é o contexto atual
        context.push(Context.DEFAULT);

        for (; ; ) {
            // Lê uma linha do programa e seu número
            var line = reader.readLine();
            var lineNumber = reader.currentLineNumber();

            // Consulta o contexto do topo da pilha.
            // Esse é o contexto atual
            var currentContext = context.peek();

            // O Analisador Léxico classifica essa linha
            // Para isso, passa o contexto atual para o analisador
            var lineContent = lexer.classify(line, currentContext);

            // Se for uma linha inválida, trata o erro
            // Senão, processa essa linha
            if (lineContent.left() == TokenType.INVALID)
                addError("Invalid line type", line, lineNumber, -1);
            else
                analyze(currentContext, lineContent.left(), lineContent.right(), line, lineNumber);

            if (lineContent.left() == TokenType.EOF)
                break;
        }

        // Somente para ver se está tudo ok!
        printSyntax(tokens);
        // ---------------------------------

        // Se não houver nenhum erro, faz análise semântica
        if (this.errorCounter == 0) {
            this.errorCounter += this.semanticAnalyser.analyse(tokens, errorListener, jsonErrorListener);
        }

        // Se não houve nenhum erro, então traduz a lista de tokens
        if (errorCounter == 0) {
            errorCounter += translator.translate(tokens, output, errorListener);
            translator.translate(tokens, output, jsonErrorListener);
        }

        System.out.println("Erros de compilação: " + errorCounter);
        System.out.println("Erros na lista de json: " + jsonErrorListener.getErrors().size());
        // Verifica se houve erros durante a compilação
        if (errorCounter > 0) {
            // Chama o método printErrors para processar e gravar os erros
            loadErrors();
        }

        return errorCounter == 0;
    }

    /**
     * Método de depuração apenas imprime a lista de tokens
     */
    private void printSyntax(List<TokenNode> tokens) {
        for (var token : tokens) {
            System.out.printf("Token: %s", token.type().name());

            if (token.params() != null)
                token.params().forEach((key, value) -> System.out.printf("\n       %s: [%s] [posicao %d]", key,
                        value.left(), value.right()));

            System.out.println();
        }
    }

    /**
     * Analisa o token para verificar a corretude sintática de acordo com o contexto
     *
     * @param context    Contexto atual do analisador
     * @param type       Tipo do token
     * @param params     Parâmetros do token
     * @param line       Linh do programa
     * @param lineNumber Número da linha
     */
    private void analyze(Context context, TokenType type, Map<String, Pair<String, Integer>> params, String line,
                         int lineNumber) {

        // Chama o metodo para analisar o token deacordo com o contexto atual
        switch (context) {
            case DEFAULT:
                analyzeDefault(type, params, line, lineNumber);
                break;

            case PARAGRAPH:
                analyzeParagraph(type, params, line, lineNumber);
                break;

            case CODE:
                analyzeCode(type, params, line, lineNumber);
                break;

            case LIST:
                analyseList(type, params, line, lineNumber);
                break;

            case CONCEPTS:
                analyzeConcepts(type, params, line, lineNumber);
                break;

            case TABLE:
                analyzeTable(type, params, line, lineNumber);
                break;

            case SCENARIO:
                analyzeScenario(type, params, line, lineNumber);
                break;

            case QUESTION:
                analyzeQuestion(type, params, line, lineNumber);
                break;
        }
    }

    /**
     * Analisa o token para verificar a corretude sintática no contexto DEFAULT
     *
     * @param type       Tipo do token
     * @param params     Parâmetros do token
     * @param line       Linh do programa
     * @param lineNumber Número da linha
     */
    private void analyzeDefault(TokenType type, Map<String, Pair<String, Integer>> params, String line,
                                int lineNumber) {

        switch (type) {
            case HEADER1:
                tokens.add(new TokenNode(type, params));
                break;

            case HEADER2:
                tokens.add(new TokenNode(type, params));
                break;

            case HEADER3:
                tokens.add(new TokenNode(type, params));
                break;

            case IMAGE:
                tokens.add(new TokenNode(type, params));
                break;

            case VIDEO:
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_SOURCE:
                // Muda para o contexto CODE
                context.push(Context.CODE);
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_LIST:
                // Muda para o contexto LIST
                context.push(Context.LIST);
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_CONCEPTS:
                // Muda para o contexto CONCEPTS
                context.push(Context.CONCEPTS);
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_TABLE:
                // Muda para o contexto TABLE
                context.push(Context.TABLE);
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_SCENARIO:
                // Muda para o contexto SCENARIO
                context.push(Context.SCENARIO);
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_QUESTION:
                // Muda para o contexto QUESTION
                context.push(Context.QUESTION);
                tokens.add(new TokenNode(type, params));
                break;

            case TEXT:
                // Muda para o contexto PARAGRAPH
                context.push(Context.PARAGRAPH);
                tokens.add(new TokenNode(TokenType.BEGIN_PARAGRAPH, null));
                tokens.add(new TokenNode(type, params));
                break;

            default:
                break;
        }
    }

    /**
     * Analisa o token para verificar a corretude sintática no contexto CODE
     *
     * @param type       Tipo do token
     * @param params     Parâmetros do token
     * @param line       Linh do programa
     * @param lineNumber Número da linha
     */
    private void analyzeCode(TokenType type, Map<String, Pair<String, Integer>> params, String line, int lineNumber) {

        switch (type) {
            case SOURCE_CODE:
                tokens.add(new TokenNode(type, params));
                break;

            case END_SOURCE:
                // Termina o CODE e volta para o contexto anterior
                context.pop();
                tokens.add(new TokenNode(type, params));
                break;

            case EOF:
                addError("Unexpected EOF", line, lineNumber, 0);
                break;

            default:
                break;
        }
    }

    /**
     * Analisa o token para verificar a corretude sintática no contexto LIST
     *
     * @param type       Tipo do token
     * @param params     Parâmetros do token
     * @param line       Linh do programa
     * @param lineNumber Número da linha
     */
    private void analyseList(TokenType type, Map<String, Pair<String, Integer>> params, String line, int lineNumber) {

        switch (type) {
            case LIST_ITEM:
                tokens.add(new TokenNode(type, params));
                break;

            case END_LIST:
                // Termina o LIST e volta para o contexto anterior
                context.pop();
                tokens.add(new TokenNode(type, params));
                break;

            case EOF:
                addError("Unexpected EOF", line, lineNumber, 0);
                break;

            default:
                break;
        }
    }

    /**
     * Analisa o token para verificar a corretude sintática no contexto CONCEPTS
     *
     * @param type       Tipo do token
     * @param params     Parâmetros do token
     * @param line       Linh do programa
     * @param lineNumber Número da linha
     */
    private void analyzeConcepts(TokenType type, Map<String, Pair<String, Integer>> params, String line,
                                 int lineNumber) {

        switch (type) {
            case CONCEPT:
                tokens.add(new TokenNode(type, params));
                break;

            case END_CONCEPTS:
                // Termina o CONCEPTS e volta para o contexto anterior
                context.pop();
                tokens.add(new TokenNode(type, params));
                break;

            case IMAGE:
                tokens.add(new TokenNode(type, params));
                break;

            case VIDEO:
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_SOURCE:
                // Muda para o contexto CODE
                context.push(Context.CODE);
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_LIST:
                // Muda para o contexto LIST
                context.push(Context.LIST);
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_TABLE:
                // Muda para o contexto TABLE
                context.push(Context.TABLE);
                tokens.add(new TokenNode(type, params));
                break;

            case TEXT:
                // Muda para o contexto PARAGRAPH
                context.push(Context.PARAGRAPH);
                tokens.add(new TokenNode(TokenType.BEGIN_PARAGRAPH, null));
                tokens.add(new TokenNode(type, params));
                break;

            case EOF:
                addError("Unexpected EOF", line, lineNumber, 0);
                break;

            default:
                break;
        }
    }

    /**
     * Analisa o token para verificar a corretude sintática no contexto PARAGRAPH
     *
     * @param type       Tipo do token
     * @param params     Parâmetros do token
     * @param line       Linh do programa
     * @param lineNumber Número da linha
     */
    private void analyzeParagraph(TokenType type, Map<String, Pair<String, Integer>> params, String line,
                                  int lineNumber) {

        switch (type) {
            case TEXT:
                tokens.add(new TokenNode(type, params));
                break;

            case BREAK:
                tokens.add(new TokenNode(type, params));
                break;

            case EMPTY_LINE:
                // Termina o PARAGRAPH e volta para o contexto anterior
                context.pop();
                tokens.add(new TokenNode(TokenType.END_PARAGRAPH, null));
                break;

            default:
                break;
        }
    }

    /**
     * Analisa o token para verificar a corretude sintática no contexto TABLE
     *
     * @param type       Tipo do token
     * @param params     Parâmetros do token
     * @param line       Linh do programa
     * @param lineNumber Número da linha
     */
    private void analyzeTable(TokenType type, Map<String, Pair<String, Integer>> params, String line, int lineNumber) {

        switch (type) {
            case TABLE_ROW:
                tokens.add(new TokenNode(type, params));
                break;

            case END_TABLE:
                // Termina o TABLE e volta para o contexto anterior
                context.pop();
                tokens.add(new TokenNode(type, params));
                break;

            case EOF:
                this.addError("Unexpected EOF", line, lineNumber, lineNumber);
                break;

            default:
                break;
        }
    }

    /**
     * Analisa o token para verificar a corretude sintática no contexto SCENARIO
     *
     * @param type       Tipo do token
     * @param params     Parâmetros do token
     * @param line       Linh do programa
     * @param lineNumber Número da linha
     */
    private void analyzeScenario(TokenType type, Map<String, Pair<String, Integer>> params, String line,
                                 int lineNumber) {

        switch (type) {

            case IMAGE:
                tokens.add(new TokenNode(type, params));
                break;

            case VIDEO:
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_SOURCE:
                // Muda para o contexto CODE
                context.push(Context.CODE);
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_LIST:
                // Muda para o contexto LIST
                context.push(Context.LIST);
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_TABLE:
                // Muda para o contexto TABLE
                context.push(Context.TABLE);
                tokens.add(new TokenNode(type, params));
                break;

            case TEXT:
                // Muda para o contexto PARAGRAPH
                context.push(Context.PARAGRAPH);
                tokens.add(new TokenNode(TokenType.BEGIN_PARAGRAPH, null));
                tokens.add(new TokenNode(type, params));
                break;

            case STEP:
                tokens.add(new TokenNode(type, params));
                break;

            case END_SCENARIO:
                // Termina o SCENARIO e volta para o contexto anterior
                context.pop();
                tokens.add(new TokenNode(type, params));
                break;

            case EOF:
                this.addError("Unexpected EOF", line, lineNumber, lineNumber);
                break;

            default:
                break;
        }
    }

    /**
     * Analisa o token para verificar a corretude sintática no contexto QUESTION
     *
     * @param type       Tipo do token
     * @param params     Parâmetros do token
     * @param line       Linh do programa
     * @param lineNumber Número da linha
     */
    private void analyzeQuestion(TokenType type, Map<String, Pair<String, Integer>> params, String line,
                                 int lineNumber) {

        switch (type) {
            case IMAGE:
                tokens.add(new TokenNode(type, params));
                break;

            case VIDEO:
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_SOURCE:
                // Muda para o contexto CODE
                context.push(Context.CODE);
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_LIST:
                // Muda para o contexto LIST
                context.push(Context.LIST);
                tokens.add(new TokenNode(type, params));
                break;

            case BEGIN_TABLE:
                // Muda para o contexto TABLE
                context.push(Context.TABLE);
                tokens.add(new TokenNode(type, params));
                break;

            case TEXT:
                // Muda para o contexto PARAGRAPH
                context.push(Context.PARAGRAPH);
                tokens.add(new TokenNode(TokenType.BEGIN_PARAGRAPH, null));
                tokens.add(new TokenNode(type, params));
                break;

            case ANSWER:
                tokens.add(new TokenNode(type, params));
                break;

            case END_QUESTION:
                // Termina o QUESTION e volta para o contexto anterior
                context.pop();
                tokens.add(new TokenNode(type, params));
                break;

            case EOF:
                this.addError("Unexpected EOF", line, lineNumber, lineNumber);
                break;

            default:
                break;
        }
    }

    /**
     * Chama o tratador de erros e incrementa o contador
     *
     * @param errorMsg   Mensagem de erro
     * @param line       Linha do programa
     * @param lineNumber Número da linha
     * @param position   Posição do erro na linha
     */
    private void addError(String errorMsg, String line, int lineNumber, int position) {
        errorListener.syntaxError(errorMsg, line, lineNumber, position);
        jsonErrorListener.syntaxError(errorMsg, line, lineNumber, position);
        errorCounter++;
    }

    /**
     * Salva os erros de compilação em um arquivo JSON
     * Caso não haja erros, não faz nada.
     */
    public void loadErrors() {
        System.out.println("Erros de compilação:");
        if (!jsonErrorListener.getErrors().isEmpty()) {
            String jsonErrors = jsonErrorListener.toJson();
            try (PrintStream out = new PrintStream(new File("errors.json"))) {
                out.print(jsonErrors);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            System.out.println("Erros de compilação salvos em compilation_errors.json.");
        }
    }

}
