package de.lgeuer.lancu.ui.module;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.ui.UIController;

class SyllableForm extends JPanel implements ActionListener,Observer {

    private static final long serialVersionUID = -7858014931265070935L;
    private UIController controller;
    private Language language;

    private JLabel syllableLabel = new JLabel("Syllable:  ");
    private JTextField syllableTextField = new JTextField(40);	
    private JButton setButton = new JButton("Set");


    public SyllableForm(UIController aController, Language aLanguage) {

	controller = aController;
	language = aLanguage;

	setButton.addActionListener(this);

	this.setLayout(new FlowLayout(FlowLayout.LEFT));
	this.add(syllableLabel);
	this.add(syllableTextField);
	this.add(setButton);

	this.setBorder(BorderFactory.createTitledBorder("Syllable"));
    }


    public void setUp() {

	language.getSyllable().addObserver(this);
	this.update(language,null);
    }


    public void shutdown() {

	language.getSyllable().deleteObserver(this);
    }


    @Override
    public synchronized void actionPerformed(ActionEvent e) {

	try {

	    language.setSyllable(syllableTextField.getText());
	}
	catch(SyntaxException ex) {

	    controller.fireError("Invalid syllable",null);
	}
    }


    @Override
    public void update(Observable observable,Object o) {

	syllableTextField.setText(language.getSyllable().getStructure());
	controller.repaint();
    }
}