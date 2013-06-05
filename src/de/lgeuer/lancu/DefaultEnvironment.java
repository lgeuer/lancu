/*
 * File: DefaultEnviroment.java
 * Author: Lars Geuer
 * Date: 23.4.2007
 */

package de.lgeuer.lancu;

import de.lgeuer.lancu.core.entity.Languages;
import de.lgeuer.lancu.message.English;
import de.lgeuer.lancu.message.Messages;
import de.lgeuer.lancu.message.Names;


public class DefaultEnvironment extends Environment {

    private English messages = new English(); //also implements Name
    private Languages languages = new Languages();
    

    protected DefaultEnvironment() {
    }


    @Override
    public Messages getMessages() {

	return messages;
    }


    @Override
    public Names getNames() {
 
	return messages; //also implements Name
    }

    @Override
    public Languages getLanguages() {

	return languages;
    }

    @Override
    public void setLanguages(Languages newLanguages) {

	languages.init(newLanguages);
    }

}
