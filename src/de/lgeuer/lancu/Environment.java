/*
 * File: Environment.java
 * Author: Lars Geuer
 * Date: 23.4.2007
 */

package de.lgeuer.lancu;

import de.lgeuer.lancu.core.entity.Languages;
import de.lgeuer.lancu.message.Messages;
import de.lgeuer.lancu.message.Names;

public abstract class Environment {


    protected static Environment environment = new DefaultEnvironment();


    public static Environment getEnvironment() {

	return environment;
    }

    public abstract Messages getMessages();

    public abstract Names getNames();

    public abstract Languages getLanguages();

    public abstract void setLanguages(Languages languages);
}