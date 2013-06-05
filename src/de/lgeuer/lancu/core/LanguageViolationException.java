package de.lgeuer.lancu.core;

public class LanguageViolationException extends Exception {

    private static final long serialVersionUID = -1199629821895343184L;

    public LanguageViolationException(String message) {
	super(message);
    }

    public LanguageViolationException(String message, Throwable cause) {
	super(message, cause);
    }

    public LanguageViolationException(Throwable cause) {
	super(cause);
    }
}
