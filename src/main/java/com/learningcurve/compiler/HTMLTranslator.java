package com.learningcurve.compiler;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.learningcurve.compiler.adapter.ErrorListener;
import com.learningcurve.compiler.adapter.Translator;

public class HTMLTranslator implements Translator {

	private PrintStream out;
	private int errorCounter = 0;
	private int contadorHeader = 0;
	private int conceptsCounter = 0;
	private int listCounter = 0;
	private int scenarioCounter = 0;
	private int stepCounter = 0;
	private int imgCounter = 0;
	private int videoCounter = 0;
	private int paragraphCounter = 0;
	private int codeCounter = 0;
	private int questionCounter = 0;
	private int optionCounter = 0;
	private int tableCounter = 0;
	private int tableRowCounter = 0;
	private String contentCheckFunction = null;
	private List<String> tableAlignment;
	private List<MarkingLanguage> m;

	public HTMLTranslator() {
		m = new ArrayList<>();
		m.add(new MarkingLanguage(MarkingTypes.BOLD, "(.*)\\*\\*(.+)\\*\\*(.*)"));
		m.add(new MarkingLanguage(MarkingTypes.ITALIC, "(.*)\\*(.+)\\*(.*)"));
		m.add(new MarkingLanguage(MarkingTypes.STRIKE, "(.*)~~(.+)~~(.*)"));
		m.add(new MarkingLanguage(MarkingTypes.UNDERLINE, "(.*)__(.+)__(.*)"));
		m.add(new MarkingLanguage(MarkingTypes.SUPERSCRIPT, "(.*)\\^(.+)\\^(.*)"));
		m.add(new MarkingLanguage(MarkingTypes.SUBSCRIPT, "(.*)~(.+)~(.*)"));
		m.add(new MarkingLanguage(MarkingTypes.LARGER, "(.*)\\+\\+(.+)\\+\\+(.*)"));
		m.add(new MarkingLanguage(MarkingTypes.LINK, "(.*)\\[(.+)\\]\\((.+)\\)(.*)"));

	}

	private void convertH1(String title) {
		this.out.printf("<h1 class=\"content_h1\" id=\"header%s\">%s</h1>\r\n", ++this.contadorHeader, title);
	}

	private void convertH2(String title) {
		this.out.printf("<h2 class=\"content_h2\" id=\"header%s\">%s</h2>\r\n", ++this.contadorHeader, title);
	}

	private void convertH3(String title) {
		this.out.printf("<h3 class=\"content_h3\" id=\"header%s\">%s</h3>\r\n", ++this.contadorHeader, title);
	}

	private void convertBeginConcepts() {
		this.out.printf("<div class=\"content_concepts\" id=\"concepts%s\">\r\n", ++this.conceptsCounter);
	}

	private void converConcept(String concept) {
		this.out.printf("<p>%s</p>\r\n", concept);
	}

	private void convertEndConcepts() {
		this.out.print("</div>\r\n");
	}

	private void convertBeginList() {
		this.out.printf("<ul  class=\"content_list\" id=\"list%s\">\r\n", ++this.listCounter);
	}

	private void converListItem(String item) {
		this.out.printf("<li>%s</li>\r\n", item);
	}

	private void convertEndList() {
		this.out.print("</ul>\r\n");
	}

	private void convertBeginScenario() {
		this.out.printf("<div class=\"content_scenario\" id=\"scenario%s\">\r\n", ++this.scenarioCounter);
		this.stepCounter = 0;
	}

	private void convertStep(String step) {
		this.out.printf("<h3>%s.%s</h3>\r\n", ++this.stepCounter, step);
	}

	private void convertEndScenario() {
		this.out.print("</div>\r\n");
	}

	private String convertMarkingText(String texto) {
		String convertedText = texto;
		for (int i = 0; i < m.size(); i++) {
			String result = m.get(i).match(convertedText);
			if (result != null) {
				convertedText = result;
			}
		}
		return convertedText;
	}

	private void convertImage(String src, String alt, String width, String height) {
		this.out.printf("<div class=\"content_img\" id=\"img%s\">\r\n<img src=\"%s\"", ++this.imgCounter, src);

		if (alt != null) {
			this.out.printf(" alt=%s", alt);
		}

		if (width != null && height != null) {
			this.out.printf(" style=\"width:%spx;height:%spx;\"", width, height);
		}

		this.out.print(">\r\n</div>\r\n<br>\r\n");
	}

	private void convertVideo(String src, String width, String height) {
		String size = null;
		String base = "<div class=\"content_video\" id=\"" + "video" + ++this.videoCounter + "\">\r\n";
		boolean isYoutubeVideo = src.contains("youtube");
		if (width != null && height != null) {
			size = " width=\"" + width + "\" height=\"" + height + "\"";
		}

		if (isYoutubeVideo) {
			base += "<iframe";
		} else {
			base += "<video controls";
		}

		if (size != null) {
			base += size;
		}

		this.out.print(base);

		if (isYoutubeVideo) {
			this.out.printf(" src=\"%s\"></iframe>\r\n</div>\r\n", src);
		} else {
			this.out.printf(">\r\n<source src=\"%s\"/>\r\n</video>\r\n</div>\r\n<br>\r\n", src);
		}
	}

	private void convertBeginParagraph() {
		this.out.printf("<p class=\"content_p\" id=\"p%s\">", ++this.paragraphCounter);
	}

	private void convertText(String text) {
		String content = text.trim();
		char lastCharacter = text.charAt(text.length() - 1);
		if (lastCharacter == '\\') {
			content = content.substring(0, content.length() - 1);
			content += "<br>";
		}
		this.out.printf("%s", content);
	}

	private void convertEndParagraph() {
		this.out.print("</p>\r\n");
	}

	private void convertBreak() {
		this.out.print("<br>\r\n");
	}

	private void convertBeginCode() {
		this.out.printf("<div class=\"content_code\" id=\"code%s\">\r\n<code>\r\n", ++this.codeCounter);
	}

	private void convertCode(String code) {
		String ignoreSpaces = "&nbsp;";
		String convertedCode = code.replace("\t", ignoreSpaces + ignoreSpaces + ignoreSpaces);
		convertedCode = convertedCode.replace(" ", ignoreSpaces);
		this.out.printf("%s<br>\r\n", convertedCode);
	}

	private void convertEndCode() {
		this.out.print("</code>\r\n</div>\r\n");
	}

	private void convertBeginQuestion(String check) {
		this.questionCounter++;
		this.optionCounter = 0;
		this.contentCheckFunction = check.isEmpty() ? null : check;

		this.out.printf("<div class=\"content_question\" id=\"question%s\">\r\n", this.questionCounter);
	}

	private void convertAnswer(String option, String isCorrect) {
		String radioButton = "<input type='radio' id='%s' name='%s' value='%s'>";
		String questionName = "q" + this.questionCounter;
		String optionName = "a" + ++this.optionCounter;
		String optionValue = " ";

		if (isCorrect != "") {
			optionValue = isCorrect;
		}

		if (this.errorCounter == 0) {
			String label = "<label for='%s'>%s</label><br>\r\n";

			this.out.printf(radioButton, questionName + "_" + optionName, questionName, optionValue);
			this.out.printf(label, questionName + "_" + optionName, option);
		}

	}

	private void convertEndQuestion() {
		out.printf("<br><button ");

		if (contentCheckFunction == null)
			out.printf(
					"""
							onclick='(function (rg) {
							let answers= document.getElementsByName(rg);
							for (let i=0; i<answers.length; i++) if (answers[i].checked && answers[i].value===\"*\") { alert(\"Resposta correta!\"); return; }
							alert(\"Resposta incorreta :-(\");})(\"q%d\");'
							""",
					questionCounter);
		else
			out.printf("onclick='%s(\"q%d\")'", contentCheckFunction, questionCounter);

		out.printf(">Verificar</button>\r\n</div>\r\n");
	}

	private void convertBeginTable(String border) {
		String tableName = "table" + ++this.tableCounter;
		String tableStyle = "<style>%s, %s th, %s td {border: 1px solid black; border-collapse: collapse; padding:3px;}</style>\r\n";
		this.tableRowCounter = 0;

		if (border != null)
			this.out.printf(tableStyle, "#" + tableName, "#" + tableName, "#" + tableName);

		this.out.printf("<table class='content_table' id='%s'>\r\n", tableName);
	}

	private void convertTableRow(Map<String, Pair<String, Integer>> row) {
		this.tableRowCounter++;
		int indexCell = 1;
		Pair<String, Integer> cell;
		String cellValue = "";
		this.out.print("<tr>\r\n");

		if (tableRowCounter == 1) {
			this.tableAlignment = new ArrayList<>();
			do {
				cell = row.get("cell" + indexCell);
				if (cell != null) {
					cellValue = cell.left().trim();
					char firstCharacter = cellValue.charAt(0);
					String cellAlignment = "left";
					if (firstCharacter == '>' || firstCharacter == '=' || firstCharacter == '<') {
						cellValue = cellValue.substring(1, cellValue.length());
						cellValue = this.convertMarkingText(cellValue);
						if (firstCharacter == '>') {
							cellAlignment = "right";
						} else if (firstCharacter == '=')
							cellAlignment = "center";
					}
					this.tableAlignment.add(cellAlignment);
					this.out.printf("<th align='center'>%s</th>\r\n", cellValue);
					indexCell++;
				}
			} while (cell != null);
		} else {
			do {
				cell = row.get("cell" + indexCell);
				if (cell != null) {
					cellValue = cell.left().trim();
					cellValue = this.convertMarkingText(cellValue);
					this.out.printf("<td align='%s'>%s</td>\r\n", tableAlignment.get(indexCell - 1), cellValue);
					indexCell++;
				}
			} while (cell != null);
		}
		this.out.print("</tr>\r\n");
	}

	private void convertEndTable() {
		this.out.print("</table>\r\n<br>\r\n");
	}

	@Override
	public int translate(List<TokenNode> syntax, PrintStream out, ErrorListener errorListener) {
		this.out = out;
		String textContent = "";

		this.out.print("<!DOCTYPE html>\r\n<html>\r\n<body>\r\n");

		for (int i = 0; i < syntax.size(); i++) {
			if (this.errorCounter > 0) {
				break;
			}
			switch (syntax.get(i).type()) {
				case HEADER1:
					this.convertH1(syntax.get(i).params().get("title").left());
					break;
				case HEADER2:
					this.convertH2(syntax.get(i).params().get("title").left());
					break;
				case HEADER3:
					this.convertH3(syntax.get(i).params().get("title").left());
					break;
				case BEGIN_CONCEPTS:
					this.convertBeginConcepts();
					break;
				case CONCEPT:
					textContent = syntax.get(i).params().get("concept").left();
					textContent = this.convertMarkingText(textContent);
					this.converConcept(textContent);
					break;
				case END_CONCEPTS:
					this.convertEndConcepts();
					break;

				case BEGIN_LIST:
					this.convertBeginList();
					break;
				case LIST_ITEM:
					textContent = syntax.get(i).params().get("item").left();
					textContent = this.convertMarkingText(textContent);
					this.converListItem(textContent);
					break;
				case END_LIST:
					this.convertEndList();
					break;

				case IMAGE:
					this.convertImage(
							syntax.get(i).params().get("url").left(),
							syntax.get(i).params().get("alt").left(),
							syntax.get(i).params().get("width").left(),
							syntax.get(i).params().get("height").left());
					break;

				case VIDEO:
					this.convertVideo(
							syntax.get(i).params().get("url").left(),
							syntax.get(i).params().get("width").left(),
							syntax.get(i).params().get("height").left());
					break;

				case BEGIN_SCENARIO:
					this.convertBeginScenario();
					;
					break;
				case STEP:
					textContent = syntax.get(i).params().get("step").left();
					textContent = this.convertMarkingText(textContent);
					this.convertStep(textContent);
					break;
				case END_SCENARIO:
					this.convertEndScenario();
					break;

				case BEGIN_PARAGRAPH:
					this.convertBeginParagraph();
					break;
				case TEXT:
					textContent = syntax.get(i).params().get("paragraph").left();
					textContent = this.convertMarkingText(textContent);
					this.convertText(textContent);
					break;
				case BREAK:
					this.convertBreak();
					break;
				case END_PARAGRAPH:
					this.convertEndParagraph();
					break;

				case BEGIN_SOURCE:
					this.convertBeginCode();
					break;

				case SOURCE_CODE:
					this.convertCode(syntax.get(i).params().get("source").left());
					break;

				case END_SOURCE:
					this.convertEndCode();
					break;

				case BEGIN_QUESTION:
					this.convertBeginQuestion(syntax.get(i).params().get("check").left());
					break;

				case ANSWER:
					textContent = syntax.get(i).params().get("option").left();
					textContent = this.convertMarkingText(textContent);
					this.convertAnswer(
							textContent,
							syntax.get(i).params().get("iscorrect").left());
					break;
				case END_QUESTION:
					this.convertEndQuestion();
					break;

				case BEGIN_TABLE:
					this.convertBeginTable(syntax.get(i).params().get("hasborder").left());
					break;

				case TABLE_ROW:
					this.convertTableRow(syntax.get(i).params());
					break;

				case END_TABLE:
					this.convertEndTable();
					break;

				default:
					break;
			}
		}

		this.out.print("</body>\r\n</html>");

		return errorCounter;
	}
}
