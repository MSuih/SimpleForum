package simplforum.utils;

import java.util.regex.Pattern;

/** Processes user-inputted strings to web-friendly format.
 */
public class StringProcessor {

    private static class PatternAndReplacement{
        public final Pattern pattern;
        public final String replacement;

        public PatternAndReplacement(String pattern, String replacement) {
            this.pattern = Pattern.compile(pattern);
            this.replacement = replacement;
        }

        public String process(String s) {
            return pattern.matcher(s).replaceAll(replacement);
        }

        @Override
        public String toString() {
            return pattern.pattern() + "->" + replacement;
        }
    }

    private static final PatternAndReplacement newlineRemove = new PatternAndReplacement("\n", "");
    private static final PatternAndReplacement newlineConvert = new PatternAndReplacement("\n", "<br />");
    private static final PatternAndReplacement[] htmlPairs = {
        new PatternAndReplacement("&", "&amp;"),
        new PatternAndReplacement("\"", "&quot;"),
        new PatternAndReplacement("'", "&apos;"),
        new PatternAndReplacement("<", "&lt;"),
        new PatternAndReplacement(">", "&gt;")
    };
    private static final PatternAndReplacement[] messageFormatTags= {
        new PatternAndReplacement("\\[b\\](.*)\\[/b\\]", "<span class=\"bold\">$1</span>"),
        new PatternAndReplacement("\\[u\\](.*)\\[/u\\]", "<span class=\"underline\">$1</span>"),
        new PatternAndReplacement("\\[q\\](.*)\\[/q\\](<br />)?", "<span class=\"quote\">$1</span>")
    };
    private static final Pattern quoteLinkPattern = Pattern.compile("\\[q=(\\d+)\\](.*)\\[/q\\](<br />)?");

    private StringProcessor(){
        throw new AssertionError("Cannot instantiate this class");
    }

    /** Converts a message to HTML-safe format. Replaces characters which hava special meaning in html by their
     * HTML entities.
     * @param s String to be converted.
     * @return A HTML-safe version of the provided string.
     */
    public static String makeHtmlSafe(String s) {
        for (PatternAndReplacement pair : htmlPairs) {
            s = pair.process(s);
        }
        return s;
    }

    /** Converts linebreaks to html-safe versions or removes them.
     * @param s A string to be converted.
     * @param doNotConvert Removes line breaks instead of converting them.
     * @return A version of the string with line breaks processed.
     */
    public static String processLinebreaks(String s, boolean doNotConvert) {
        if (doNotConvert)
            return newlineRemove.process(s);
        else
            return newlineConvert.process(s);
    }

    /** Processed formatting tags for this message. This replaces special tags such as [b] with their
     * HTML-viewable variants. This method must be called after
     * {@link simplforum.utils.StringProcessor#makeHtmlSafe(java.lang.String)} or otherwise the formatting
     * tags will become mangled.
     * @param s A string to be converted.
     * @param redirectAddress HTTP address which is used for message redirection.
     * @return A version of the string with HTML-ready formatting tags.
     */
    public static String processFormattingTags(String s, String redirectAddress) {
        s = quoteLinkPattern.matcher(s)
                .replaceAll("<span class=\"quote\">"
                        + "<a class=\"origin\" href=\""+ redirectAddress +"$1\">$1</a>:<br />$2</span>");
        System.out.println(s);
        for (PatternAndReplacement pair : messageFormatTags) {
            s = pair.process(s);
        }
        return s;
    }
}
