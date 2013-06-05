/*
 * File: WordClassModule.java
 * Author: Lars Geuer
 * Date: 21.5.2007
 */

package de.lgeuer.lancu.ui.module;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import de.lgeuer.lancu.core.ItemAlreadyExistsException;
import de.lgeuer.lancu.core.LanguageViolationException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.entity.AbstractRule;
import de.lgeuer.lancu.core.entity.Inflection;
import de.lgeuer.lancu.core.entity.InflectionState;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.core.entity.Regularity;
import de.lgeuer.lancu.core.entity.WordClass;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.ui.UIController;


public class WordClassModule extends UIModule {

    private static final long serialVersionUID = 4502068622552152262L;

    private static String moduleName = "Word Classes";

    private UIController controller;

    private Language language;

    private WordClassList wordClassList;

    private InflectionList inflectionList;

    private InflectionStateList inflectionStateList;

    private RegularityList regularityList;

    private RuleList ruleList;

    private JPanel wordClassPanel = new JPanel();

    private JPanel inflectionPanel = new JPanel();

    private JPanel inflectionStatePanel = new JPanel();

    private JPanel regularityPanel = new JPanel();

    private JPanel rulePanel = new JPanel();

    public WordClassModule(UIController aController, Language aLanguage) {

	super(aController);

	controller = aController;
	language = aLanguage;

	TreeSet<WordClass> wordClasses = new TreeSet<WordClass>(language
		.getWordClasses());
	WordClass wordClass = null;
	Inflection inflection = null;
	InflectionState inflectionState = null;
	Regularity regularity = null;

	if (wordClasses.size() > 0) {

	    wordClass = wordClasses.iterator().next();

	    TreeSet<Inflection> inflections = new TreeSet<Inflection>(wordClass
		    .getInflections());

	    if (inflections.size() > 0) {

		inflection = inflections.iterator().next();

		TreeSet<InflectionState> inflectionStates = new TreeSet<InflectionState>(
			inflection.getInflectionStates());
		if (inflectionStates.size() > 0) {

		    inflectionState = inflectionStates.iterator().next();

		    TreeSet<Regularity> regularities = new TreeSet<Regularity>(
			    inflectionState.getRegularities());
		    if (regularities.size() > 0) {

			regularity = regularities.iterator().next();
		    }
		}
	    }
	}

	ruleList = new RuleList(controller, regularity);
	regularityList = new RegularityList(controller, inflectionState,
		ruleList);
	inflectionStateList = new InflectionStateList(controller, inflection,
		regularityList);
	inflectionList = new InflectionList(controller, wordClass,
		inflectionStateList);
	wordClassList = new WordClassList(controller, language, inflectionList);

	this.setLayout(new GridLayout(1, 4));
	wordClassPanel.add(wordClassList);
	inflectionPanel.add(inflectionList);
	inflectionStatePanel.add(inflectionStateList);
	regularityPanel.add(regularityList);
	rulePanel.add(ruleList);

	this.add(wordClassPanel);
	this.add(inflectionPanel);
	this.add(inflectionStatePanel);
	this.add(regularityPanel);
	this.add(rulePanel);
    }

    @Override
    public String getName() {

	return moduleName + " (" + language.getName() + ")";
    }

    @Override
    public String getShortName() {

	return moduleName;
    }

    @Override
    public Language getLanguage() {

	return language;
    }

    @Override
    public void setUp() {

	wordClassList.setUp();
	inflectionList.setUp();
	inflectionStateList.setUp();
	regularityList.setUp();
    }

    @Override
    public void shutDown() {

	wordClassList.shutdown();
	inflectionList.shutdown();
	regularityList.shutdown();
    }

}

class WordClassList extends JPanel implements ActionListener {

    private static final long serialVersionUID = 6204060621070149990L;

    class WordClassTableModel extends AbstractTableModel implements Observer {

	private static final long serialVersionUID = 626362587121935384L;

	@Override
	public synchronized int getRowCount() {

	    return language.getWordClasses().size();
	}

	@Override
	public int getColumnCount() {

	    return 1;
	}

	@Override
	public synchronized Object getValueAt(int row, int column) {

	    TreeSet<WordClass> wordClasses = new TreeSet<WordClass>(language
		    .getWordClasses());
	    Iterator<WordClass> it = wordClasses.iterator();

	    for (int i = 0; i < row; i++) {

		it.next();
	    }

	    if (it.hasNext()) {

		return it.next().getName();
	    }
	    
	    return "";
	}

	@Override
	public synchronized void update(Observable observable, Object o) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    this.fireTableStructureChanged();

	    if (row > -1 && row < tableModel.getRowCount()) {

		if (column == -1) {

		    column = 0;
		}

		table.addRowSelectionInterval(row, column);
	    }

	    controller.repaint();
	}

	public void setActive(boolean active) {

	    if (active) {

		language.addObserver(this);
	    } else {

		language.deleteObserver(this);
	    }
	}
    }

    private UIController controller;

    private Language language;

    private InflectionList inflectionList;

    private WordClassTableModel tableModel = new WordClassTableModel();

    private JTable table = new JTable(tableModel);

    private JScrollPane scrollPane = new JScrollPane(table);

    private JLabel wordClassLabel = new JLabel("Word Class:  ");

    private JTextField wordClassTextField = new JTextField(10);

    private JButton addButton = new JButton("Add");

    private JButton renameButton = new JButton("Rename");

    private JButton deleteButton = new JButton("Delete");

    private JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private JPanel actionPanel = new JPanel(new BorderLayout());

    private JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    public WordClassList(UIController aController, Language aLanguage,
	    InflectionList anInflectionList) {

	controller = aController;
	language = aLanguage;
	inflectionList = anInflectionList;

	addButton.addActionListener(this);
	renameButton.addActionListener(this);
	deleteButton.addActionListener(this);

	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	table.setTableHeader(null);
	table.getSelectionModel().addListSelectionListener(
		new ListSelectionListener() {

		    @Override
		    public void valueChanged(ListSelectionEvent e) {

			int row = table.getSelectedRow();
			int column = table.getSelectedColumn();

			if (row < 0) {

			    inflectionList.setWordClass(null);
			    return;
			}

			String wordClassName = (String) tableModel.getValueAt(
				row, column);
			WordClass selectedWordClass = null;

			wordClassTextField.setText(wordClassName);

			for (WordClass wordClass : language.getWordClasses()) {

			    if (wordClass.getName().equals(wordClassName)) {

				selectedWordClass = wordClass;
				break;
			    }
			}

			inflectionList.setWordClass(selectedWordClass);
		    }
		});

	tablePanel.add(scrollPane);

	buttonPanel.add(renameButton);
	buttonPanel.add(addButton);
	buttonPanel.add(deleteButton);

	inputPanel.add(wordClassLabel);
	inputPanel.add(wordClassTextField);

	actionPanel.add(inputPanel, BorderLayout.NORTH);
	actionPanel.add(buttonPanel, BorderLayout.CENTER);

	this.setLayout(new BorderLayout());
	this.add(tablePanel, BorderLayout.NORTH);
	this.add(actionPanel, BorderLayout.CENTER);

	Dimension size = scrollPane.getPreferredSize();
	size.setSize(200, 400);
	scrollPane.setPreferredSize(size);

	this.setBorder(BorderFactory.createTitledBorder("Word Classes"));
    }

    public void setUp() {

	tableModel.setActive(true);

	if (table.getRowCount() > 0) {

	    table.addRowSelectionInterval(0, 0);
	}
    }

    public void shutdown() {

	tableModel.setActive(false);
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {

	// add wordClass
	if (e.getSource() == addButton) {

	    String wordClass = wordClassTextField.getText();

	    try {

		if (!wordClass.equals("")) {

		    language.addWordClass(wordClass);
		    wordClassTextField.setText("");
		}
	    } catch (ItemAlreadyExistsException ex) {

		controller.fireError("Word Class '" + wordClass
			+ "' already exist", null);
	    }

	    catch (IllegalArgumentException ex) {

		controller.fireError("Illegal Word Class '" + wordClass + "'",
			null);
	    }

	    return;
	}

	// rename word class
	if (e.getSource() == renameButton) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    if (row < 0 || column < 0) {

		return;
	    }

	    String oldName = (String) table.getValueAt(row, column);
	    String newName = wordClassTextField.getText();

	    if (oldName.equals(newName)) {

		return;
	    }

	    Collection<WordClass> wordClasses = language.getWordClasses();

	    for (WordClass wordClass : wordClasses) {

		if (wordClass.getName().equals(oldName)) {

		    for (WordClass wc : wordClasses) {

			if (wc.getName().equals(newName)) {

			    controller.fireError("Word class '" + newName
				    + "' already exists", null);
			    return;
			}
		    }

		    wordClass.setName(newName);
		    break;
		}
	    }
	}

	// delete word class
	if (e.getSource() == deleteButton) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    if (row < 0 || column < 0) {

		return;
	    }

	    String wordClassString = (String) table.getValueAt(row, column);

	    Collection<WordClass> wordClasses = language.getWordClasses();
	    Iterator<WordClass> it = wordClasses.iterator();

	    while (it.hasNext()) {

		WordClass wordClass = it.next();

		if (wordClass.getName().equals(wordClassString)) {

		    language.removeWordClass(wordClass);
		    break;
		}
	    }
	}
    }
}

class InflectionList extends JPanel implements ActionListener {

    private static final long serialVersionUID = -5336481123110842212L;

    class InflectionTableModel extends AbstractTableModel implements Observer {

	private static final long serialVersionUID = 3138043485694513031L;

	@Override
	public synchronized int getRowCount() {

	    if (wordClass != null) {

		return wordClass.getInflections().size();
	    }

	    return 0;
	}

	@Override
	public int getColumnCount() {

	    return 1;
	}

	@Override
	public synchronized Object getValueAt(int row, int column) {

	    if (wordClass != null) {

		TreeSet<Inflection> inflections = new TreeSet<Inflection>(
			wordClass.getInflections());
		Iterator<Inflection> it = inflections.iterator();

		for (int i = 0; i < row; i++) {

		    it.next();
		}

		if (it.hasNext()) {

		    return it.next().getName();
		}
		
		return "";
	    }

	    return "";
	}

	@Override
	public synchronized void update(Observable observable, Object o) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    this.fireTableStructureChanged();

	    if (row > -1 && row < tableModel.getRowCount()) {

		if (column == -1) {

		    column = 0;
		}

		table.addRowSelectionInterval(row, column);
	    }

	    controller.repaint();
	}

	public void setActive(boolean active) {

	    if (wordClass != null) {

		if (active) {

		    wordClass.addObserver(this);
		} else {

		    wordClass.deleteObserver(this);
		}
	    }
	}
    }

    private UIController controller;

    private WordClass wordClass;

    private InflectionStateList inflectionStateList;

    private InflectionTableModel tableModel = new InflectionTableModel();

    private JTable table = new JTable(tableModel);

    private JScrollPane scrollPane = new JScrollPane(table);

    private JLabel inflectionLabel = new JLabel("Inflexion:  ");

    private JTextField inflectionTextField = new JTextField(10);

    private JButton addButton = new JButton("Add");

    private JButton renameButton = new JButton("Rename");

    private JButton deleteButton = new JButton("Delete");

    private JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private JPanel actionPanel = new JPanel(new BorderLayout());

    private JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    public InflectionList(UIController aController, WordClass aWordClass,
	    InflectionStateList anInflectionStateList) {

	controller = aController;
	wordClass = aWordClass;
	inflectionStateList = anInflectionStateList;

	addButton.setEnabled(wordClass != null);
	renameButton.setEnabled(wordClass != null);
	deleteButton.setEnabled(wordClass != null);
	inflectionTextField.setEnabled(wordClass != null);

	addButton.addActionListener(this);
	renameButton.addActionListener(this);
	deleteButton.addActionListener(this);

	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	table.setTableHeader(null);
	table.getSelectionModel().addListSelectionListener(
		new ListSelectionListener() {

		    @Override
		    public void valueChanged(ListSelectionEvent e) {

			int row = table.getSelectedRow();
			int column = table.getSelectedColumn();

			if (row < 0) {

			    inflectionTextField.setText("");
			    inflectionStateList.setInflection(null);
			    return;
			}

			String inflectionName = (String) tableModel.getValueAt(
				row, column);
			Inflection selectedInflection = null;

			inflectionTextField.setText(inflectionName);

			for (Inflection inflection : wordClass.getInflections()) {

			    if (inflection.getName().equals(inflectionName)) {

				selectedInflection = inflection;
				break;
			    }
			}

			renameButton.setEnabled(wordClass != null
				&& table.getSelectedRow() >= 0);
			inflectionStateList.setInflection(selectedInflection);
		    }
		});

	tablePanel.add(scrollPane);

	buttonPanel.add(renameButton);
	buttonPanel.add(addButton);
	buttonPanel.add(deleteButton);

	inputPanel.add(inflectionLabel);
	inputPanel.add(inflectionTextField);

	actionPanel.add(inputPanel, BorderLayout.NORTH);
	actionPanel.add(buttonPanel, BorderLayout.CENTER);

	this.setLayout(new BorderLayout());
	this.add(tablePanel, BorderLayout.NORTH);
	this.add(actionPanel, BorderLayout.CENTER);

	Dimension size = scrollPane.getPreferredSize();
	size.setSize(200, 400);
	scrollPane.setPreferredSize(size);

	this.setBorder(BorderFactory.createTitledBorder("Inflexions"));
    }

    public void setUp() {

	tableModel.setActive(true);

	if (table.getRowCount() > 0) {

	    table.addRowSelectionInterval(0, 0);
	}
    }

    public void shutdown() {

	tableModel.setActive(false);
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {

	// add inflection
	if (e.getSource() == addButton && wordClass != null) {

	    String inflection = inflectionTextField.getText();

	    try {

		if (!inflection.equals("")) {

		    wordClass.addInflection(inflection);
		    inflectionTextField.setText("");
		}
	    } catch (ItemAlreadyExistsException ex) {

		controller.fireError("Inflexion '" + inflection
			+ "' already exists", null);
	    }

	    catch (IllegalArgumentException ex) {

		controller.fireError("Illegal Inflexion '" + inflection + "'",
			null);
		ex.printStackTrace();
	    }

	    return;
	}

	// rename inflection
	if (e.getSource() == renameButton && wordClass != null) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    if (row < 0 || column < 0) {

		return;
	    }

	    String oldName = (String) table.getValueAt(row, column);
	    String newName = inflectionTextField.getText();

	    if (oldName.equals(newName)) {

		return;
	    }

	    Collection<Inflection> inflections = wordClass.getInflections();

	    for (Inflection inflection : inflections) {

		for (Inflection inf : inflections) {

		    if (inf.getName().equals(newName)) {

			controller.fireError("Inflexion '" + newName
				+ "' already exists", null);
			return;
		    }
		}

		if (inflection.getName().equals(oldName)) {

		    inflection.setName(newName);
		    break;
		}
	    }
	}

	// delete inflection
	if (e.getSource() == deleteButton && wordClass != null) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    if (row < 0 || column < 0) {

		return;
	    }

	    String inflectionString = (String) table.getValueAt(row, column);

	    Collection<Inflection> inflections = wordClass.getInflections();
	    Iterator<Inflection> it = inflections.iterator();

	    while (it.hasNext()) {

		Inflection inflection = it.next();

		if (inflection.getName().equals(inflectionString)) {

		    wordClass.removeInflection(inflection);
		    break;
		}
	    }
	}
    }

    public synchronized void setWordClass(WordClass aWordClass) {

	if (wordClass != aWordClass) {

	    tableModel.setActive(false);
	    wordClass = aWordClass;
	    tableModel.setActive(true);

	    tableModel.fireTableDataChanged();

	    if (wordClass != null) {

		Collection<Inflection> inflections = wordClass.getInflections();
		if (inflections.size() > 0) {

		    inflectionStateList.setInflection(inflections.iterator()
			    .next());

		    table.clearSelection();
		    table.addRowSelectionInterval(0, 0);
		}
	    }
	}

	addButton.setEnabled(wordClass != null);
	renameButton.setEnabled(wordClass != null
		&& table.getSelectedRow() >= 0);
	deleteButton.setEnabled(wordClass != null);
	inflectionTextField.setEnabled(wordClass != null);
    }
}

class InflectionStateList extends JPanel implements ActionListener {

    private static final long serialVersionUID = 920273502819819752L;

    class InflectionStateTableModel extends AbstractTableModel implements
    Observer {

	private static final long serialVersionUID = 3984885608672228017L;

	@Override
	public synchronized int getRowCount() {

	    if (inflection == null) {

		return 0;
	    }

	    return inflection.getInflectionStates().size();
	}

	@Override
	public int getColumnCount() {

	    return 1;
	}

	@Override
	public synchronized Object getValueAt(int row, int column) {

	    if (inflection == null) {

		return "";
	    }

	    TreeSet<InflectionState> inflectionStates = new TreeSet<InflectionState>(
		    inflection.getInflectionStates());
	    Iterator<InflectionState> it = inflectionStates.iterator();

	    for (int i = 0; i < row; i++) {

		it.next();
	    }

	    if (it.hasNext()) {

		return it.next().getName();
	    }

	    return "";
	}

	@Override
	public synchronized void update(Observable observable, Object o) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    this.fireTableStructureChanged();

	    if (row > -1 && row < tableModel.getRowCount()) {

		if (column == -1) {

		    column = 0;
		}

		table.addRowSelectionInterval(row, column);
	    }

	    controller.repaint();
	}

	public void setActive(boolean active) {

	    if (inflection != null) {

		if (active) {

		    inflection.addObserver(this);
		} else {

		    inflection.deleteObserver(this);
		}
	    }
	}
    }

    private UIController controller;

    private Inflection inflection;

    private RegularityList regularityList;

    private InflectionStateTableModel tableModel = new InflectionStateTableModel();

    private JTable table = new JTable(tableModel);

    private JScrollPane scrollPane = new JScrollPane(table);

    private JLabel inflectionStateLabel = new JLabel("Inflexion State:  ");

    private JTextField inflectionStateTextField = new JTextField(10);

    private JButton addButton = new JButton("Add");

    private JButton renameButton = new JButton("Rename");

    private JButton deleteButton = new JButton("Delete");

    private JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private JPanel actionPanel = new JPanel(new BorderLayout());

    private JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    public InflectionStateList(UIController aController,
	    Inflection aInflection, RegularityList aRegularityList) {

	controller = aController;
	inflection = aInflection;
	regularityList = aRegularityList;

	addButton.addActionListener(this);
	renameButton.addActionListener(this);
	deleteButton.addActionListener(this);

	addButton.setEnabled(inflection != null);
	renameButton.setEnabled(inflection != null
		&& table.getSelectedRow() >= 0);
	deleteButton.setEnabled(inflection != null);
	inflectionStateTextField.setEnabled(inflection != null);

	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	table.setTableHeader(null);
	table.getSelectionModel().addListSelectionListener(
		new ListSelectionListener() {

		    @Override
		    public void valueChanged(ListSelectionEvent e) {

			int row = table.getSelectedRow();
			int column = table.getSelectedColumn();

			if (row < 0) {

			    inflectionStateTextField.setText("");
			    regularityList.setInflectionState(null);
			    return;
			}

			String inflectionStateName = (String) tableModel
			.getValueAt(row, column);
			InflectionState selectedInflectionState = null;

			inflectionStateTextField.setText(inflectionStateName);

			for (InflectionState inflectionState : inflection
				.getInflectionStates()) {

			    if (inflectionState.getName().equals(
				    inflectionStateName)) {

				selectedInflectionState = inflectionState;
				break;
			    }
			}

			renameButton.setEnabled(inflection != null
				&& table.getSelectedRow() >= 0);
			regularityList
			.setInflectionState(selectedInflectionState);
		    }
		});

	tablePanel.add(scrollPane);

	buttonPanel.add(renameButton);
	buttonPanel.add(addButton);
	buttonPanel.add(deleteButton);

	inputPanel.add(inflectionStateLabel);
	inputPanel.add(inflectionStateTextField);

	actionPanel.add(inputPanel, BorderLayout.NORTH);
	actionPanel.add(buttonPanel, BorderLayout.CENTER);

	this.setLayout(new BorderLayout());
	this.add(tablePanel, BorderLayout.NORTH);
	this.add(actionPanel, BorderLayout.CENTER);

	Dimension size = scrollPane.getPreferredSize();
	size.setSize(200, 400);
	scrollPane.setPreferredSize(size);

	this.setBorder(BorderFactory.createTitledBorder("Inflexion States"));
    }

    public void setUp() {

	tableModel.setActive(true);

	if (table.getRowCount() > 0) {

	    table.addRowSelectionInterval(0, 0);
	}
    }

    public void shutdown() {

	tableModel.setActive(false);
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {

	// add inflectionState
	if (e.getSource() == addButton) {

	    String inflectionState = inflectionStateTextField.getText();

	    try {

		if (!inflectionState.equals("")) {

		    inflection.addInflectionState(inflectionState);
		    inflectionStateTextField.setText("");
		}
	    } catch (ItemAlreadyExistsException ex) {

		controller.fireError("Inflexion state '" + inflectionState
			+ "' already exist", null);
	    }

	    catch (IllegalArgumentException ex) {

		controller.fireError("Illegal inflexion state '"
			+ inflectionState + "'", null);
	    }

	    return;
	}

	// rename inflection state
	if (e.getSource() == renameButton) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    if (row < 0 || column < 0) {

		return;
	    }

	    String oldName = (String) table.getValueAt(row, column);
	    String newName = inflectionStateTextField.getText();

	    if (oldName.equals(newName)) {

		return;
	    }

	    Collection<InflectionState> inflectionStates = inflection
	    .getInflectionStates();

	    for (InflectionState inflectionState : inflectionStates) {

		if (inflectionState.getName().equals(oldName)) {

		    for (InflectionState wc : inflectionStates) {

			if (wc.getName().equals(newName)) {

			    controller.fireError("Inflexion state '" + newName
				    + "' already exists", null);
			    return;
			}
		    }

		    inflectionState.setName(newName);
		    break;
		}
	    }
	}

	// delete inflection state
	if (e.getSource() == deleteButton) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    if (row < 0 || column < 0) {

		return;
	    }

	    String inflectionStateString = (String) table.getValueAt(row,
		    column);

	    Collection<InflectionState> inflectionStates = inflection
	    .getInflectionStates();
	    Iterator<InflectionState> it = inflectionStates.iterator();

	    while (it.hasNext()) {

		InflectionState inflectionState = it.next();

		if (inflectionState.getName().equals(inflectionStateString)) {

		    inflection.removeInflectionState(inflectionState);
		    break;
		}
	    }
	}
    }

    public synchronized void setInflection(Inflection aInflection) {

	if (inflection != aInflection) {

	    tableModel.setActive(false);
	    inflection = aInflection;
	    tableModel.setActive(true);

	    tableModel.fireTableDataChanged();

	    if (inflection != null) {

		Collection<InflectionState> inflectionStates = inflection
		.getInflectionStates();

		if (inflectionStates.size() > 0) {

		    regularityList.setInflectionState(inflectionStates
			    .iterator().next());

		    table.clearSelection();
		    table.addRowSelectionInterval(0, 0);
		}
	    }
	}

	addButton.setEnabled(inflection != null);
	renameButton.setEnabled(inflection != null
		&& table.getSelectedRow() >= 0);
	deleteButton.setEnabled(inflection != null);
	inflectionStateTextField.setEnabled(inflection != null);
    }
}

class RegularityList extends JPanel implements ActionListener {

    private static final long serialVersionUID = 5419726520068844545L;

    class RegularityTableModel extends AbstractTableModel implements Observer {

	private static final long serialVersionUID = -3072340457497238939L;

	@Override
	public synchronized int getRowCount() {

	    if (inflectionState == null) {

		return 0;
	    }

	    return inflectionState.getRegularities().size();
	}

	@Override
	public int getColumnCount() {

	    return 1;
	}

	@Override
	public synchronized Object getValueAt(int row, int column) {

	    if (inflectionState == null) {

		return "";
	    }

	    TreeSet<Regularity> reqularities = new TreeSet<Regularity>(
		    inflectionState.getRegularities());
	    Iterator<Regularity> it = reqularities.iterator();

	    for (int i = 0; i < row; i++) {

		it.next();
	    }

	    if (it.hasNext()) {

		return it.next().getName();
	    }

	    return "";
	}

	@Override
	public synchronized void update(Observable observable, Object o) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    this.fireTableStructureChanged();

	    if (row > -1 && row < tableModel.getRowCount()) {

		if (column == -1) {

		    column = 0;
		}

		table.addRowSelectionInterval(row, column);
	    }

	    controller.repaint();
	}

	public void setActive(boolean active) {

	    if (inflectionState != null) {

		if (active) {

		    inflectionState.addObserver(this);
		} else {

		    inflectionState.deleteObserver(this);
		}
	    }
	}
    }

    private UIController controller;

    private InflectionState inflectionState;

    private RuleList ruleList;

    private RegularityTableModel tableModel = new RegularityTableModel();

    private JTable table = new JTable(tableModel);

    private JScrollPane scrollPane = new JScrollPane(table);

    private JLabel regularityLabel = new JLabel("Regularity:  ");

    private JTextField regularityTextField = new JTextField(10);

    private JButton addButton = new JButton("Add");

    private JButton renameButton = new JButton("Rename");

    private JButton deleteButton = new JButton("Delete");

    private JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private JPanel actionPanel = new JPanel(new BorderLayout());

    private JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    public RegularityList(UIController aController,
	    InflectionState aInflectionState, RuleList aRuleList) {

	controller = aController;
	inflectionState = aInflectionState;
	ruleList = aRuleList;

	addButton.addActionListener(this);
	renameButton.addActionListener(this);
	deleteButton.addActionListener(this);

	addButton.setEnabled(inflectionState != null);
	renameButton.setEnabled(inflectionState != null
		&& table.getSelectedRow() >= 0);
	deleteButton.setEnabled(inflectionState != null);
	regularityTextField.setEnabled(inflectionState != null);

	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	table.setTableHeader(null);
	table.getSelectionModel().addListSelectionListener(
		new ListSelectionListener() {

		    @Override
		    public void valueChanged(ListSelectionEvent e) {

			int row = table.getSelectedRow();
			int column = table.getSelectedColumn();

			if (row < 0) {

			    regularityTextField.setText("");
			    ruleList.setRegularity(null);
			    return;
			}

			String regularityName = (String) tableModel.getValueAt(
				row, column);
			Regularity selectedRegularity = null;

			regularityTextField.setText(regularityName);

			for (Regularity regularity : inflectionState
				.getRegularities()) {

			    if (regularity.getName().equals(regularityName)) {

				selectedRegularity = regularity;
				break;
			    }
			}

			renameButton.setEnabled(inflectionState != null
				&& table.getSelectedRow() >= 0);
			ruleList.setRegularity(selectedRegularity);
		    }
		});

	tablePanel.add(scrollPane);

	buttonPanel.add(renameButton);
	buttonPanel.add(addButton);
	buttonPanel.add(deleteButton);

	inputPanel.add(regularityLabel);
	inputPanel.add(regularityTextField);

	actionPanel.add(inputPanel, BorderLayout.NORTH);
	actionPanel.add(buttonPanel, BorderLayout.CENTER);

	this.setLayout(new BorderLayout());
	this.add(tablePanel, BorderLayout.NORTH);
	this.add(actionPanel, BorderLayout.CENTER);

	Dimension size = scrollPane.getPreferredSize();
	size.setSize(200, 400);
	scrollPane.setPreferredSize(size);

	this.setBorder(BorderFactory.createTitledBorder("Regularity"));
    }

    public void setUp() {

	tableModel.setActive(true);

	if (table.getRowCount() > 0) {

	    table.addRowSelectionInterval(0, 0);
	}
    }

    public void shutdown() {

	tableModel.setActive(false);
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {

	// add regularity
	if (e.getSource() == addButton) {

	    String regularity = regularityTextField.getText();

	    try {

		if (!regularity.equals("")) {

		    inflectionState.addRegularity(regularity);
		    regularityTextField.setText("");
		}
	    } catch (ItemAlreadyExistsException ex) {

		controller.fireError("Regularity '" + regularity
			+ "' already exist", null);
	    }

	    catch (IllegalArgumentException ex) {

		controller.fireError("Illegal regularity '" + regularity + "'",
			null);
	    }

	    return;
	}

	// rename regularity
	if (e.getSource() == renameButton) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    if (row < 0 || column < 0) {

		return;
	    }

	    String oldName = (String) table.getValueAt(row, column);
	    String newName = regularityTextField.getText();

	    if (oldName.equals(newName)) {

		return;
	    }

	    Collection<Regularity> reqularities = inflectionState
	    .getRegularities();

	    for (Regularity regularity : reqularities) {

		if (regularity.getName().equals(oldName)) {

		    for (Regularity wc : reqularities) {

			if (wc.getName().equals(newName)) {

			    controller.fireError("Regularity '" + newName
				    + "' already exists", null);
			    return;
			}
		    }

		    regularity.setName(newName);
		    break;
		}
	    }
	}

	// delete regularity
	if (e.getSource() == deleteButton) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    if (row < 0 || column < 0) {

		return;
	    }

	    String regularityString = (String) table.getValueAt(row, column);

	    Collection<Regularity> reqularities = inflectionState
	    .getRegularities();
	    Iterator<Regularity> it = reqularities.iterator();

	    while (it.hasNext()) {

		Regularity regularity = it.next();

		if (regularity.getName().equals(regularityString)) {

		    inflectionState.removeRegularity(regularity);
		    break;
		}
	    }
	}
    }

    public synchronized void setInflectionState(InflectionState aInflectionState) {

	if (inflectionState != aInflectionState) {

	    tableModel.setActive(false);
	    inflectionState = aInflectionState;
	    tableModel.setActive(true);

	    tableModel.fireTableDataChanged();

	    if (inflectionState != null) {

		Collection<Regularity> regularities = inflectionState
		.getRegularities();
		if (regularities.size() > 0) {

		    ruleList.setRegularity(regularities.iterator().next());

		    table.clearSelection();
		    table.addRowSelectionInterval(0, 0);
		}
	    }
	}

	addButton.setEnabled(inflectionState != null);
	renameButton.setEnabled(inflectionState != null
		&& table.getSelectedRow() >= 0);
	deleteButton.setEnabled(inflectionState != null);
	regularityTextField.setEnabled(inflectionState != null);
    }
}

class RuleList extends JPanel implements ActionListener {

    private static final long serialVersionUID = -1237761855655950836L;

    class RuleTableModel extends AbstractTableModel implements Observer {

	private static final long serialVersionUID = 257112761809287830L;

	@Override
	public synchronized int getRowCount() {

	    if (regularity == null) {

		return 0;
	    }

	    return regularity.getRules().size();
	}

	@Override
	public int getColumnCount() {

	    return 1;
	}

	@Override
	public synchronized Object getValueAt(int row, int column) {

	    if (regularity == null) {

		return "";
	    }

	    Collection<AbstractRule> reqularities = regularity
	    .getRules();
	    Iterator<AbstractRule> it = reqularities.iterator();

	    for (int i = 0; i < row; i++) {

		it.next();
	    }

	    if (it.hasNext()) {

		return it.next().getRule();
	    }
	    
	    return "";
	}

	@Override
	public synchronized void update(Observable observable, Object o) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    this.fireTableStructureChanged();

	    if (row > -1 && row < tableModel.getRowCount()) {

		if (column == -1) {

		    column = 0;
		}

		table.addRowSelectionInterval(row, column);
	    }

	    controller.repaint();
	}

	public void setActive(boolean active) {

	    if (regularity != null) {

		if (active) {

		    regularity.addObserver(this);
		} else {

		    regularity.deleteObserver(this);
		}
	    }
	}
    }

    private UIController controller;

    private Regularity regularity;

    private RuleTableModel tableModel = new RuleTableModel();

    private JTable table = new JTable(tableModel);

    private JScrollPane scrollPane = new JScrollPane(table);

    private JLabel ruleLabel = new JLabel("Rule:  ");

    private JTextField ruleTextField = new JTextField(10);

    private JButton addButton = new JButton("Add");

    private JButton editButton = new JButton("Edit");

    private JButton deleteButton = new JButton("Delete");

    private JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private JPanel actionPanel = new JPanel(new BorderLayout());

    private JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    public RuleList(UIController aController, Regularity aRegularity) {

	controller = aController;
	regularity = aRegularity;

	addButton.addActionListener(this);
	editButton.addActionListener(this);
	deleteButton.addActionListener(this);

	addButton.setEnabled(regularity != null);
	editButton
	.setEnabled(regularity != null && table.getSelectedRow() >= 0);
	deleteButton.setEnabled(regularity != null);
	ruleTextField.setEnabled(regularity != null);

	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	table.setTableHeader(null);
	table.getSelectionModel().addListSelectionListener(
		new ListSelectionListener() {

		    @Override
		    public void valueChanged(ListSelectionEvent e) {

			int row = table.getSelectedRow();
			int column = table.getSelectedColumn();

			if (row < 0) {

			    ruleTextField.setText("");
			    return;
			}

			String ruleString = (String) tableModel.getValueAt(row,
				column);

			ruleTextField.setText(ruleString);
			editButton.setEnabled(regularity != null
				&& table.getSelectedRow() >= 0);
		    }
		});

	tablePanel.add(scrollPane);

	buttonPanel.add(editButton);
	buttonPanel.add(addButton);
	buttonPanel.add(deleteButton);

	inputPanel.add(ruleLabel);
	inputPanel.add(ruleTextField);

	actionPanel.add(inputPanel, BorderLayout.NORTH);
	actionPanel.add(buttonPanel, BorderLayout.CENTER);

	this.setLayout(new BorderLayout());
	this.add(tablePanel, BorderLayout.NORTH);
	this.add(actionPanel, BorderLayout.CENTER);

	Dimension size = scrollPane.getPreferredSize();
	size.setSize(200, 400);
	scrollPane.setPreferredSize(size);

	this.setBorder(BorderFactory.createTitledBorder("Rules"));
    }

    public void setUp() {

	tableModel.setActive(true);

	if (table.getRowCount() > 0) {

	    table.addRowSelectionInterval(0, 0);
	}
    }

    public void shutdown() {

	tableModel.setActive(false);
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {

	// add rule
	if (e.getSource() == addButton) {

	    String rule = ruleTextField.getText();

	    try {

		if (!rule.equals("")) {

		    regularity.addRule(rule);
		    ruleTextField.setText("");
		}
	    } catch (ItemAlreadyExistsException ex) {

		controller.fireError("Rule '" + rule + "' already exist", null);
	    }

	    catch (SyntaxException ex) {

		controller.fireError("Illegal rule syntax", null);
	    } catch (UnknownPhonemeStructureException ex) {
		
		controller.fireError("Unknown Structure: " + ex.getStructureName(), null);
	    }

	    return;
	}

	// edit rule
	if (e.getSource() == editButton) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    if (row < 0 || column < 0) {

		return;
	    }

	    String oldRuleString = (String) table.getValueAt(row, column);
	    String newRuleString = ruleTextField.getText();

	    if (oldRuleString.equals(newRuleString)) {

		return;
	    }

	    Collection<AbstractRule> reqularities = regularity
	    .getRules();

	    for (AbstractRule rule : reqularities) {

		if (rule.getRule().equals(oldRuleString)) {

		    for (AbstractRule wc : reqularities) {

			if (wc.getRule().equals(newRuleString)) {

			    controller.fireError("Rule '" + newRuleString
				    + "' already exists", null);
			    return;
			}
		    }

		    try {

			rule.setRule(newRuleString);
			break;
		    } catch (LanguageViolationException ex) {

			controller.fireError("Invalid rule syntax", null);
			break;
		    }
		}
	    }
	}

	// delete rule
	if (e.getSource() == deleteButton) {

	    int row = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    if (row < 0 || column < 0) {

		return;
	    }

	    String ruleString = (String) table.getValueAt(row, column);

	    Collection<AbstractRule> reqularities = regularity
	    .getRules();
	    Iterator<AbstractRule> it = reqularities.iterator();

	    while (it.hasNext()) {

		AbstractRule rule = it.next();

		if (rule.getRule().equals(ruleString)) {

		    regularity.removeRule(rule);
		    break;
		}
	    }
	}
    }

    public synchronized void setRegularity(Regularity aRegularity) {

	if (regularity != aRegularity) {

	    tableModel.setActive(false);
	    regularity = aRegularity;
	    tableModel.setActive(true);

	    tableModel.fireTableDataChanged();

	    if (regularity != null) {

		Collection<AbstractRule> rules = regularity.getRules();
		if (rules.size() > 0) {

		    table.clearSelection();
		    table.addRowSelectionInterval(0, 0);
		}
	    }
	}

	addButton.setEnabled(regularity != null);
	editButton
	.setEnabled(regularity != null && table.getSelectedRow() >= 0);
	deleteButton.setEnabled(regularity != null);
	ruleTextField.setEnabled(regularity != null);
    }
}
