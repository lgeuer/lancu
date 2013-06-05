
/*
 * File: Storage.java
 * Author: Lars Geuer
 * Date: 30.4.2007
 */

package de.lgeuer.lancu.store;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;

import de.lgeuer.lancu.core.entity.Languages;



public class JDOFileStorage implements Storage {
    
    private JFileChooser fc = new JFileChooser();
    private Component parent;
    private File file; //may be null

    public JDOFileStorage(Component aParent) {

	parent = aParent;
    }


    @Override
    public void save(Languages languages) 
	throws FileNotFoundException,IOException {

	if (file != null) {

	    saveToFile(languages,file);
	}
	else {

	    saveAs(languages);
	}

    }

    @Override
    public void saveAs(Languages languages) 
	throws FileNotFoundException,IOException {

	int result = fc.showSaveDialog(parent);

	if (result == JFileChooser.APPROVE_OPTION) {

	    File aFile = fc.getSelectedFile();
	    saveToFile(languages,aFile);
	    file = aFile;
	}
    }


    private void saveToFile(Languages languages,File aFile) 
	throws FileNotFoundException,IOException {
	
	ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(aFile));
	os.writeObject(languages);
	os.flush();
	os.close();
    }

    @Override
    public boolean implementsSaveAs() {

	return true;
    }

    @Override
    public Languages restore()
	throws FileNotFoundException,IOException,ClassNotFoundException {

	int result = fc.showOpenDialog(parent);

	if (result == JFileChooser.APPROVE_OPTION) {

	    File aFile = fc.getSelectedFile();

	    ObjectInputStream is = new ObjectInputStream(new FileInputStream(aFile));
	    Languages languages = (Languages)is.readObject();
	    is.close();

	    file = aFile;
	    return languages;
	}

	throw new IllegalStateException("No file chosen");
    }
}

