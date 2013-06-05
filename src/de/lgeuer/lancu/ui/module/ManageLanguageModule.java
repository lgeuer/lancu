/*
 * File: ManageLanguageModule.java
 * Author: Lars Geuer
 * Date: 29.4.2007
 */

package de.lgeuer.lancu.ui.module;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import de.lgeuer.lancu.Environment;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.ui.UIController;


public class ManageLanguageModule extends UIModule implements ActionListener {

    private static final long serialVersionUID = -8456590157185116948L;

    private static String moduleName = "Languages";

    class LanguageTableModel extends AbstractTableModel implements Observer {

	private static final long serialVersionUID = -1166615377604315635L;

	@Override
	public synchronized int getRowCount() {

	    return Environment.getEnvironment().getLanguages().size() + 1;
	}

	@Override
	public int getColumnCount() {

	    return 1;
	}

	@Override
	public synchronized Object getValueAt(int row, int column) {

	    Map<String, Language> languages = Environment.getEnvironment().getLanguages().getMap();
	    Iterator<Language> it = languages.values().iterator();

	    for (int i = 0; i < row; i++) {

		it.next();
	    }

	    if (it.hasNext()) {

		return it.next().getName();
	    }

	    return "";
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
	    Map<String, Language> languages = Environment.getEnvironment().getLanguages().getMap();
	    Iterator<Language> it = languages.values().iterator();

	    for (int i = 0; i < row;i++) {

		it.next();
	    }

	    if (it.hasNext()) {

		it.next().setName(value.toString());
	    } else {
		try {
		    Environment.getEnvironment().getLanguages().addLanguage(value.toString());
		} catch (IllegalArgumentException e) {
		    System.out.println("Language already exists: " + value);
		}
	    }
	}

	@Override
	public boolean isCellEditable(int row, int col) {
	    return true;
	}

	@Override
	public Class<String> getColumnClass(int col) {
	    return String.class;
	}

	@Override
	public synchronized void update(Observable observable, Object o) {

	    this.fireTableStructureChanged();
	}

	public void setActive(boolean active) {

	    if (active) {

		Environment.getEnvironment().getLanguages().addObserver(this);
	    } else {

		Environment.getEnvironment().getLanguages()
			.deleteObserver(this);
	    }
	}
    }

    private UIController controller;

    private LanguageTableModel tableModel = new LanguageTableModel();

    private JTable table = new JTable(tableModel);

    private JScrollPane scrollPane = new JScrollPane(table);

    private JPanel tablePanel = new JPanel();

    private JLabel languageLabel = new JLabel("Name: ");

    private JTextField languageTextField = new JTextField(15);

    private JButton createButton = new JButton("Add");

    private JButton deleteButton = new JButton("Delete");

    private JButton editButton = new JButton("Rename");

    private JPanel mainPanel = new JPanel(new BorderLayout());

    private JPanel actionPanel = new JPanel(new BorderLayout());

    private JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    public ManageLanguageModule(UIController aController) {

	super(aController);

	controller = aController;

	this.setLayout(new FlowLayout(FlowLayout.CENTER));

	table.setTableHeader(null);
	table.getSelectionModel().addListSelectionListener(
		new ListSelectionListener() {

		    @Override
		    public void valueChanged(ListSelectionEvent e) {

			int row = table.getSelectedRow();
			int column = table.getSelectedColumn();

			if (row != -1 && column != -1) {

			    languageTextField.setText((String) tableModel
				    .getValueAt(row, column));
			}
		    }
		});

	tablePanel.add(scrollPane);

	textFieldPanel.add(languageLabel);
	textFieldPanel.add(languageTextField);

	createButton.addActionListener(this);
	deleteButton.addActionListener(this);
	editButton.addActionListener(this);

	buttonPanel.add(createButton);
	buttonPanel.add(editButton);
	buttonPanel.add(deleteButton);

	actionPanel.add(textFieldPanel, BorderLayout.CENTER);
	actionPanel.add(buttonPanel, BorderLayout.SOUTH);

	mainPanel.add(tablePanel, BorderLayout.CENTER);
	mainPanel.add(actionPanel, BorderLayout.SOUTH);
	this.add(mainPanel);

	mainPanel.setBorder(BorderFactory.createTitledBorder("Languages"));

	Dimension size = scrollPane.getPreferredSize();
	size.setSize(250, 400);
	scrollPane.setPreferredSize(size);
    }

    @Override
    public String getName() {

	return moduleName;
    }

    @Override
    public String getShortName() {

	return moduleName;
    }

    @Override
    public Language getLanguage() {

	throw new UnsupportedOperationException();
    }

    @Override
    public void setUp() {

	tableModel.setActive(true);
    }

    @Override
    public void shutDown() {

	tableModel.setActive(false);
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {

	// create language
	if (e.getSource() == createButton) {

	    String name = languageTextField.getText();

	    try {

		if (!name.equals("")) {

		    Environment.getEnvironment().getLanguages().addLanguage(
			    name);
		    languageTextField.setText("");
		}
	    } catch (IllegalArgumentException ex) {

		controller.fireError("Language '" + name + "' already exist",
			this);
	    }

	    return;
	}

	// delete language
	if (e.getSource() == deleteButton) {

	    Map<String, Language> languages = Environment.getEnvironment()
		    .getLanguages().getMap();
	    Language language = languages.get(languageTextField.getText());

	    if (language != null) {

		if (controller.confirm("Do you really want to delete "
			+ language.getName() + "?", this)) {

		    Environment.getEnvironment().getLanguages().removeLanguage(
			    language);
		}
	    } else {

		controller.fireError("No language selected", this);
	    }
	}

	// edit language name
	if (e.getSource() == editButton) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();
	    Map<String, Language> languages = Environment.getEnvironment()
		    .getLanguages().getMap();
	    Language language = null;

	    if (row != -1 && column != -1) {

		language = languages.get(tableModel.getValueAt(row, column));
	    }

	    if (language != null) {
		language.setName(languageTextField.getText());
	    } else {

		controller.fireError("No language selected", this);
	    }
	}
    }
}