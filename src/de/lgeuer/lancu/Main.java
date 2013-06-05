/*
 * File: Main.java
 * Author: Lars Geuer
 * Date: 24.4.2007
 */

package de.lgeuer.lancu;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.lgeuer.lancu.ui.DefaultUIController;
import de.lgeuer.lancu.ui.LancuFrame;
import de.lgeuer.lancu.ui.UIController;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		LancuFrame frame = new LancuFrame("lancu");
		UIController controller = new DefaultUIController(frame);
		controller.display();
	    }
	});

    }
}