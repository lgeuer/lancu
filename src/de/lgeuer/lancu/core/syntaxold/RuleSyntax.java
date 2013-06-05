/*
 * File: RuleSyntax.java
 * Author: Lars Geuer
 * Date: 14.3.2007
 */


package de.lgeuer.lancu.core.syntaxold;

import java.io.Serializable;
import java.util.List;

import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import de.lgeuer.lancu.core.RuleViolationException;
import de.lgeuer.lancu.core.UnknownConstantException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.entity.PhonemeStructure;


/**
 * Used by <code>PhonemeStructure</code> and <code>Rule </code>
 */
public interface RuleSyntax extends Serializable {

    /**
     * @deprecated Use checkPhonemeStructureSyntax(String) instead.
     */
    @Deprecated public boolean checkSyntax(String structure);

    public boolean checkPhonemeStructureSyntax(String structure);
    
    public boolean checkRuleSyntax(String rule);

    public boolean checkRule(String rule);

    public boolean containsRuleSymbol(String string);

    public List<String> getGroupNames(String structure);

    public String stripStructure(String structure);

    public String stripRule(String rule);

    public List<String[]> getSequenceParts(String phonemeSequence,String rule) 
	throws UnknownPhonemeStructureException,
	       UnknownConstantException;

    public boolean isTagSequence(String string); 

    public boolean isPhonemeStructureName(String string); 

    public String stripVarName(String string);

    public String parseTag(String phonemeSequence,PhonemeStructure structure,String tag) 
	throws RuleDoesNotApplyException;

    public String[] splitPhonemeStructureByGroup(String structure,String namedGroup);

    public String getNamedGroup(String namedGroup,String phonemeSequence,PhonemeStructure structure);

    public String replaceConstants(String string) throws UnknownConstantException;

    public String replacePhonemeStructures(String string) throws UnknownPhonemeStructureException;

    public String trimRule(String rule,String sequence) throws UnknownPhonemeStructureException,
							       RuleViolationException,
							       UnknownConstantException;

}
