package de.lgeuer.lancu.core.syntax.lancuregex;

import java.util.regex.Pattern;

public interface DefaultRuleSyntaxNew {

    public static final String TAG_OPEN = "<";

    public static final String TAG_CLOSE = ">";

    public static final String CHANGE_TAG_IDENTIFIER = "change:";

    public static final String ADD_TAG_IDENTIFIER = "add:";

    public static final String DELETE_TAG_IDENTIFIER = "delete";

    public static final String COMPARE_TAG_IDENTIFIER = "compare:";

    public static final String NAME_TAG_IDENTIFIER = "name:";

    public static final String NAMED_GROUP_DELIMITER = ";";

    public static final String ASSIGMENT_DELIMITER = ",";

    public static final String ASSIGNER = "=";

    public static final String CHANGE_TO_ASSIGNER = ":";

    public static final String DEPENDENT_CHANGE_TAG_OPEN = "(";

    public static final String DEPENDENT_CHANGE_TAG_CLOSE = ")";

    public static final String CONSTANT_IDENTIFIER = "#";

    public static final String CONSTANT_BRACKET_OPEN = "{";

    public static final String CONSTANT_BRACKET_CLOSE = "}";

    public final static String RULE_PART_IDENTIFIER = "$";

    public final static String RULE_PART_BRACKET_OPEN = "{";

    public final static String RULE_PART_BRACKET_CLOSE = "}";

    public final static String PHONEME_STRUCTURE_IDENTIFIER = "$";

    public final static String PHONEME_STRUCTURE_BRACKET_OPEN = "{";

    public final static String PHONEME_STRUCTURE_BRACKET_CLOSE = "}";

    public static final String OPERATOR_MATCHES = "==";

    public static final String OPERATOR_DOESNT_MATCH = "!=";

    public static final String ANY_SYMBOL = "*";

    public static final String VAR_SYMBOL = "[A-Z0-9]";

    public static final String INTERNAL_VAR_SYMBOL = "?";

    public static final String PHONEME = "[^<>;:$#.=!(){}\\[\\]\\|\\&\\\\^?*+]";

    public static final String CONSTANT = Pattern.quote(CONSTANT_IDENTIFIER) + "("
    + VAR_SYMBOL + "+|" + Pattern.quote(CONSTANT_BRACKET_OPEN)
    + VAR_SYMBOL + "+" + Pattern.quote(CONSTANT_BRACKET_CLOSE) + ")";

    public static final String PHONEME_STRUCTURE = Pattern
    .quote(PHONEME_STRUCTURE_IDENTIFIER)
    + "(?:"
    + "(?<structurename1>" + VAR_SYMBOL + "+)|"
    + Pattern.quote(PHONEME_STRUCTURE_BRACKET_OPEN)
    + "(?<structurename2>" + VAR_SYMBOL + "+)"
    + Pattern.quote(PHONEME_STRUCTURE_BRACKET_CLOSE) 
    + ")";


    public static final String INTERNAL_RULE_PART = Pattern
    .quote(PHONEME_STRUCTURE_IDENTIFIER)
    + Pattern.quote(PHONEME_STRUCTURE_BRACKET_OPEN + INTERNAL_VAR_SYMBOL)
    + VAR_SYMBOL + "+"
    + Pattern.quote(PHONEME_STRUCTURE_BRACKET_CLOSE);


    public static final String PHONEME_CHAR = "[^()" + TAG_OPEN + TAG_CLOSE + PHONEME_STRUCTURE_IDENTIFIER  + RULE_PART_IDENTIFIER + PHONEME_STRUCTURE_BRACKET_OPEN + PHONEME_STRUCTURE_BRACKET_CLOSE + "]";

    public static final String TAG = TAG_OPEN + "[^" + TAG_OPEN + "]*" + TAG_CLOSE ;
    
    public static final String FIRST_GROUP = "FIRST";

    public static final String LAST_GROUP = "LAST";
    
    public static final String INTERNAL_SILLABLE_DELIMITER = "-"; //TODO: change to "\0" after testing
}
