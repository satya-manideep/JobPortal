package com.jobportal.util;

public class HtmlUtil {

    private HtmlUtil() { }

    // Makes user-typed text safe to print inside a page
    public static String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    // Escapes the text first, then turns line breaks into <br>
    public static String escapeWithBreaks(String text) {
        return escape(text).replace("\r\n", "<br>").replace("\n", "<br>").replace("\r", "<br>");
    }
}