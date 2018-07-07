package java.util.regex;

import def.js.JSON;
import def.js.RegExp;
import def.js.SyntaxError;
import jsweet.util.Lang;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static def.js.Globals.undefined;
import static jsweet.util.Lang.*;

public class Pattern implements Serializable {
    public static final int CASE_INSENSITIVE = 2;
    public static final int MULTILINE = 8;
    public static final int UNICODE_CASE = 64;
    public static final int UNICODE_CHARACTER_CLASS = 256;

    final RegExp regexp;
    final int _flags;
    final Map<String, Integer> namedGroupsNames;

    private Pattern(RegExp regexp, int _flags, Map<String, Integer> namedGroupsNames) {
        this.regexp = regexp;
        this._flags = _flags;
        this.namedGroupsNames = namedGroupsNames;
    }

    private static class GroupNameRemover implements Function<def.js.String[], def.js.String> {
        private final Map<String, Integer> namedGroupsNames;
        private int count = 0;
        private boolean inBrackets = false;

        private GroupNameRemover(Map<String, Integer> namedGroupsNames) {
            this.namedGroupsNames = namedGroupsNames;
        }


        @Override
        public def.js.String apply(def.js.String[] args) {
            // argument list:
            // 0 == whole recognized text
            // 1 == open bracket escapes
            // 2 == close bracket escapes
            // 3 == open parenthesis escapes
            // 4 == non capturing block
            // 5 == named group name

            if (inBrackets || args[2] != undefined) { // only close bracket saves out from this state
                inBrackets = args[2] == undefined || args[2].length == 1; // keep in brackets if not close or escaped
                return args[0];
            }
            if (args[1] != undefined) {
                inBrackets = args[1].length == 0; // open bracket without escape
                return args[0];
            }
            if (args[3] != undefined && args[3].length != 0) { // escaped open parenthesis, not a block
                return args[0];
            }

            if (args[4] == undefined) { // not a non capturing block (aka capturing block), count it
                count += 1;
            }

            if (args[5] != undefined) { // capturing block with name
                namedGroupsNames.put(string(args[5]), count);
                args[0] = args[0].replace(new RegExp("\\?<[^>]+>"), "");
            }
            return args[0];
        }
    }

    static Pattern compile(String regexp) {
        return compile(regexp, 0);
    }

    static Pattern compile(String regexpString, int flags) {
        String jsFlags = "g";
        if ((flags & MULTILINE) > 0) {
            jsFlags += "m";
        }
        if ((flags & CASE_INSENSITIVE) > 0) {
            jsFlags += "i";
        }
        if ((flags & UNICODE_CHARACTER_CLASS) > 0) {
            jsFlags += "u";
        }
        if ((flags & UNICODE_CASE) > 0) {
            jsFlags += "ui";
        }

        // formatting regexp to acquire some information (name and indices)
        // this should not be to change anything about the original regex
        Map<String, Integer> namedGroupsNames = new HashMap<>();
        GroupNameRemover groupNameRemover = new GroupNameRemover(namedGroupsNames);

        regexpString = string(string(regexpString)
                .replace(new RegExp("" +
                                "(?:\\\\\\\\)*(\\\\?)\\[\\^?\\]?|" +
                                "(?:\\\\\\\\)*(\\\\?)\\]|" +
                                "(?:\\\\\\\\)*(\\\\?)\\((?:" +
                                    "(\\?\\:)|" +
                                    "\\?<([^>]+)>)?", "g"),
                        Lang.<Supplier<def.js.String>> any((Function<def.js.String[], def.js.String>)
                                ((def.js.String ... args) -> groupNameRemover.apply(args))))
                // add non-capturing groups to all string
                .replace(new RegExp("" +
                        "(" + // previous group suffix ($1)
                            "\\?\\:|" + // open bracket modifier (?:
                            "(?:" + // close bracket modifiers
                                "[*+?]|" + // * + ?
                                "\\{[^\\}]+\\}" + // {3, 4}
                            ")*" +
                        ")" +
                        "((?:" + // main grouped characters ($2)
                            "[^()\\\\|\\[\\]]|" + // any not special character
                            "\\\\\\\\|" + // escaped characters
                            "\\\\\\(|" +
                            "\\\\\\)|" +
                            "\\\\\\||" +
                            "\\\\\\[|" +
                            "\\\\\\]|" +
                            "\\[\\^?\\]\\]|" + // []] and [^]] special brackets
                            "\\[\\^?(?:" + // brackets
                                "[^\\\\\\]]|" + // not special closing character
                                "\\\\\\\\|" + // escaped escape
                                "\\\\\\]" + // escaped close bracket
                            ")+\\]" +
                        ")*)", "g"), Lang.<Supplier<def.js.String>> any((Function<def.js.String[], def.js.String>)
                        ((def.js.String ... args) -> {
                            if (args[2] == undefined || args[2].length == 0) {
                                return args[1];
                            }

                            def.js.String regexp = args[args.length - 1];
                            int startIndexOfMatched = Integer.parseInt(string(args[args.length - 2]));
                            int endIndexOfMatched = startIndexOfMatched + args[0].length;
                            boolean hasOpenBracket = (startIndexOfMatched > 0 && "(".equals(string(regexp.charAt(startIndexOfMatched-1)))) ||
                                    (startIndexOfMatched > 2 && "(?:".equals(string(regexp.substr(startIndexOfMatched - 3, 3))));
                            boolean hasCloseBracket = regexp.length > endIndexOfMatched && ")".equals(string(regexp.charAt(endIndexOfMatched)));

                            if (hasOpenBracket && hasCloseBracket) {
                                return string(string(args[1]) + args[2]);
                            }

                            return string(args[1] + "(?:" + args[2] + ")");
                        })))
        );

        try {
            return new Pattern(new RegExp(regexpString, jsFlags), flags, namedGroupsNames);
        } catch (SyntaxError e) {
            throw new PatternSyntaxException(e, regexpString);
        }
    }

    public int flags() {
        return _flags;
    }

    public Matcher matcher(CharSequence sequence) {
        return new Matcher(this, sequence.toString());
    }

    public static boolean matches(String regex, CharSequence input) {
        return compile(regex).matcher(input).matches();
    }

    public String pattern() {
        return regexp.source;
    }

    public static String quote(String s) {
        return JSON.stringify(s);
    }

    public String[] split(CharSequence input, int limit) {
        return any(string(input.toString()).split(regexp, limit).toArray());
    }

    public String[] split(CharSequence input) {
        return split(input, 0);
    }

    public Stream<String> splitAsStream(CharSequence input) {
        return Arrays.stream(split(input));
    }

    @Override
    public String toString() {
        return pattern();
    }
}
