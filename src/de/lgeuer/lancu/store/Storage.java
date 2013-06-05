/*
 * File: Storage.java
 * Author: Lars Geuer
 * Date: 30.4.2007
 */

package de.lgeuer.lancu.store;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.lgeuer.lancu.core.entity.Languages;


public interface Storage {

    public void save(Languages languages) throws FileNotFoundException,IOException; 

    public void saveAs(Languages languages) throws FileNotFoundException,
						   IOException,
						   UnsupportedOperationException; 

    public boolean implementsSaveAs(); 

    public Languages restore() throws FileNotFoundException,
				      IOException,
				      ClassNotFoundException; 
}

