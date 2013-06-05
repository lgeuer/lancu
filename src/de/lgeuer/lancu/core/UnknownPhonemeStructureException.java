/**
 * File: UnknownPhonemeSequenceException.java
 * Author: Lars Geuer
 * Date: 4.4.2007
 */

package de.lgeuer.lancu.core;

import de.lgeuer.lancu.core.entity.PhonemeStructure;

public class UnknownPhonemeStructureException extends LanguageViolationException {

    private static final long serialVersionUID = -3718168636504075295L;
    private PhonemeStructure structure;
    private String structureName;
    
    public UnknownPhonemeStructureException(PhonemeStructure aStructure) {
	
	super("Unknown structure: " + aStructure.getName());

	structure = aStructure;
	structureName = structure.getName();
    }

    public UnknownPhonemeStructureException(String name) {
	
	super("Unknown structure: " + name);
	structureName = name;
    }

    public String getStructureName() {
	
	return structureName;
    }
}