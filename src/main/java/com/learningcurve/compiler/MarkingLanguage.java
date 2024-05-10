package com.learningcurve.compiler;

import java.util.regex.Pattern;

public class MarkingLanguage {
  private MarkingTypes type;
  private Pattern pattern;

  public MarkingLanguage(MarkingTypes type, String regex) {
    this.type = type;
    this.pattern = Pattern.compile(regex);
  }

  public String match(String text) {
    var matcher = pattern.matcher(text);
    if (matcher.find()) {
      switch (this.type) {
        case BOLD:
          return matcher.replaceFirst("$1<b>$2</b>$3");
        case ITALIC:
          return matcher.replaceFirst("$1<i>$2</i>$3");
        case STRIKE:
          return matcher.replaceFirst("$1<del>$2</del>$3");
        case UNDERLINE:
          return matcher.replaceFirst("$1<u>$2</u>$3");
        case SUPERSCRIPT:
          return matcher.replaceFirst("$1<sup>$2</sup>$3");
        case SUBSCRIPT:
          return matcher.replaceFirst("$1<sub>$2</sub>$3");
        case LARGER:
          return matcher.replaceFirst("$1<span style='font-size:larger;'>$2</span>$3");
        case LINK:
          return matcher.replaceFirst("$1<a href='$3'>$2</a>$4");

        default:
          return text;
      }

    }

    return null;
  }
}
