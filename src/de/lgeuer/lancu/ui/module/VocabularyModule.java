/*
 * File: VocabularyModule.java
 * Author: Lars Geuer
 * Date: 21.5.2007
 */

package de.lgeuer.lancu.ui.module;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import de.lgeuer.lancu.core.InvalidPhonemeSequenceException;
import de.lgeuer.lancu.core.ItemAlreadyExistsException;
import de.lgeuer.lancu.core.UnknownMorphemeReferenceException;
import de.lgeuer.lancu.core.entity.Inflection;
import de.lgeuer.lancu.core.entity.InflectionState;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.core.entity.Morpheme;
import de.lgeuer.lancu.core.entity.Regularity;
import de.lgeuer.lancu.core.entity.Word;
import de.lgeuer.lancu.core.entity.WordClass;
import de.lgeuer.lancu.core.entity.WordState;
import de.lgeuer.lancu.core.syntax.lancuregex.WordSyntaxException;
import de.lgeuer.lancu.ui.UIController;
import de.lgeuer.lancu.util.id.UnknownIdException;


public class VocabularyModule extends UIModule {

	private static final long serialVersionUID = -4140070697627057561L;

	private static String moduleName = "Vocabulary";

	private UIController controller;
	private Language language;

	private MorphemeList morphemeList;
	private WordList wordList;
	private JPanel mainPanel = new JPanel(new BorderLayout());

	public  VocabularyModule(UIController aController,Language aLanguage) {

		super(aController);

		controller = aController;
		language = aLanguage;

		morphemeList = new MorphemeList(controller,language);
		wordList = new WordList(controller,language);

		mainPanel.add(morphemeList,BorderLayout.WEST);
		mainPanel.add(wordList,BorderLayout.CENTER);
		this.add(mainPanel);	
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

		morphemeList.setUp();
		wordList.setUp();

	}

	@Override
	public void shutDown() {

		morphemeList.shutDown();
		wordList.shutDown();
	}

}


class MorphemeList extends JPanel implements ActionListener {

	private static final long serialVersionUID = 2638579239685013997L;

	class MorphemeTableModel extends AbstractTableModel implements Observer {

		private static final long serialVersionUID = -3753127369211057690L;


		@Override
		public synchronized int getRowCount() {

			return language.getMorphemes().size();
		}


		@Override
		public int getColumnCount() {

			return 2;
		}


		@Override
		public synchronized Object getValueAt(int row,int column) {

			TreeSet<Morpheme> morphemes = new TreeSet<Morpheme>(language.getMorphemes());
			Iterator<Morpheme> it = morphemes.iterator();

			for (int i = 0; i < row;i++) {

				it.next();
			}

			if (it.hasNext()) {

				Morpheme morpheme = it.next();

				if(column == 0) {

					return morpheme.getId();
				}
				
				return morpheme.getMorpheme();
			}

			return "";
		}


		@Override
		public synchronized void update(Observable observable,Object o) {

			int row = table.getSelectedRow();
			int column = table.getSelectedColumn();

			this.fireTableStructureChanged();

			if (row > -1 && row < tableModel.getRowCount()) {

				if (column == -1) {

					column = 0;
				}

				table.addRowSelectionInterval(row,0);
			}

			controller.repaint();
		}


		public void setActive(boolean active) {

			if (active) {

				language.addObserver(this);
			}
			else {

				language.deleteObserver(this);
			}
		}
	}


	private UIController controller;
	private Language language;

	private MorphemeTableModel tableModel = new MorphemeTableModel();
	private JTable table = new JTable(tableModel);
	private JScrollPane scrollPane;

	private JLabel morphemeLabel    = new JLabel("Morpheme:  ");
	private JLabel descriptionLabel = new JLabel("Description:  ");
	private JTextField morphemeTextField = new JTextField(10);
	private JTextArea descriptionTextArea = new JTextArea(5,10);
	private JButton addButton = new JButton("Add");
	private JButton editButton = new JButton("Edit");
	private JButton deleteButton = new JButton("Delete");

	private JPanel tablePanel            = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel actionPanel           = new JPanel(new BorderLayout());
	private JPanel buttonPanel           = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel morphemeInputPanel    = new JPanel(new FlowLayout(FlowLayout.LEFT));    
	private JPanel descriptionLabelPanel = new JPanel(new GridLayout(5,1));
	private JPanel descriptionInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel inputPanel            = new JPanel(new BorderLayout());


	public MorphemeList(UIController aController, Language aLanguage) {

		controller = aController;
		language = aLanguage;

		scrollPane = new JScrollPane(table);

		addButton.addActionListener(this);
		editButton.addActionListener(this);
		deleteButton.addActionListener(this);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);

		table.setTableHeader(null);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				int row = table.getSelectedRow();

				if (row < 0) {

					editButton.setEnabled(false);
					return;
				}

				int morphemeId = (Integer)tableModel.getValueAt(row,0);
				Morpheme selectedMorpheme = null;

				for (Morpheme morpheme:language.getMorphemes()) {

					if (morpheme.getId() == morphemeId) {

						selectedMorpheme = morpheme;
						break;
					}
				}

				editButton.setEnabled(true);
				morphemeTextField.setText(selectedMorpheme.getMorpheme());
				descriptionTextArea.setText(selectedMorpheme.getDescription());
			}
		});

		tablePanel.add(scrollPane);

		buttonPanel.add(editButton);
		buttonPanel.add(addButton);
		buttonPanel.add(deleteButton);

		//set label size
		Dimension morphemeSize = morphemeLabel.getPreferredSize();
		Dimension descriptionSize = descriptionLabel.getPreferredSize();
		Dimension labelSize = new Dimension();
		double width = 0;
		double height = 0;

		if (morphemeSize.getWidth() > descriptionSize.getWidth()) {

			width = morphemeSize.getWidth();
		}
		else {

			width = descriptionSize.getWidth();
		}

		if (morphemeSize.getHeight() > descriptionSize.getHeight()) {

			height = morphemeSize.getHeight();
		}
		else {

			height = descriptionSize.getHeight();
		}
		labelSize.setSize(width,height);

		morphemeLabel.setPreferredSize(labelSize);
		descriptionLabel.setPreferredSize(labelSize);
		descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
		descriptionLabelPanel.add(descriptionLabel);

		morphemeInputPanel.add(morphemeLabel);
		morphemeInputPanel.add(morphemeTextField);
		descriptionInputPanel.add(descriptionLabelPanel);
		descriptionInputPanel.add(new JScrollPane(descriptionTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

		inputPanel.add(morphemeInputPanel,BorderLayout.NORTH);
		inputPanel.add(descriptionInputPanel,BorderLayout.CENTER);

		actionPanel.add(inputPanel,BorderLayout.NORTH);
		actionPanel.add(buttonPanel,BorderLayout.CENTER);

		this.setLayout(new BorderLayout());
		this.add(tablePanel,BorderLayout.NORTH);
		this.add(actionPanel,BorderLayout.CENTER);

		Dimension size = scrollPane.getPreferredSize();
		size.setSize(200,400);
		scrollPane.setPreferredSize(size);

		this.setBorder(BorderFactory.createTitledBorder("Morphemes"));
	}


	public void setUp() {

		tableModel.setActive(true);

		if (table.getRowCount() > 0) {

			table.addRowSelectionInterval(0,0);
		}
	}


	public void shutDown() {

		tableModel.setActive(false);
	}


	@Override
	public synchronized void actionPerformed(ActionEvent e) {

		//add morpheme
		if (e.getSource() == addButton) {

			String morpheme    = morphemeTextField.getText();
			String description = descriptionTextArea.getText();

			if (morpheme.equals("")) {

				controller.fireError("Morpheme is empty",null);
				return;
			}

			if (description.equals("")) {

				controller.fireError("Description is empty",null);
				return;
			}

			try {

				for (Morpheme m:language.getMorphemes()) {

					if(m.getMorpheme().equals(morpheme) && 
							m.getDescription().equals(description)) {

						controller.fireError("Morpheme '" + morpheme 
								+ "' already exists with this description",
								null);
						return;
					}
				}

				for (Morpheme m:language.getMorphemes()) {

					if(m.getMorpheme().equals(morpheme)) {

						controller.fireWarning("Morpheme '" + morpheme 
								+ "' already exists with an other description",
								null);
						break;
					}

					if(m.getDescription().equals(description)) {

						controller.fireWarning("Another morpheme with "
								+ "this description already exists",
								null);
						break;
					}
				}

				if (!morpheme.equals("")) {

					language.addMorpheme(morpheme,description);
					morphemeTextField.setText("");
					descriptionTextArea.setText("");
				}
			}
			catch(ItemAlreadyExistsException ex) {

				controller.fireError("Morpheme '" + morpheme + "' already exist",null);
			}
			catch(InvalidPhonemeSequenceException ex) {

				controller.fireError("Illegal morpheme '" + morpheme + "'",null);
			}
			catch(UnknownMorphemeReferenceException ex) {

				controller.fireError("Unkown morpheme reference  " + ex.getReference() + "",null);
			}

			return;
		}


		//edit morpheme
		if (e.getSource() == editButton) {


			int row    = table.getSelectedRow();
			int column = table.getSelectedColumn();

			if (row < 0 || column < 0) {

				return;
			}

			Integer id  = (Integer)table.getValueAt(row,0);
			String newMorpheme = morphemeTextField.getText();
			String newDescription = descriptionTextArea.getText();

			if (newMorpheme.equals("")) {

				controller.fireError("Morpheme is empty",null);
				return;
			}

			if (newDescription.equals("")) {

				controller.fireError("Description is empty",null);
				return;
			}

			try {

				Morpheme morpheme = language.getMorpheme(id);

				for (Morpheme m:language.getMorphemes()) {

					if(m.getMorpheme().equals(newMorpheme) && 
							m.getDescription().equals(newDescription)) {

						controller.fireError("Morpheme '" + newMorpheme 
								+ "' already exists with this description",
								null);
						return;
					}
				}

				for (Morpheme m:language.getMorphemes()) {

					if(m.getMorpheme().equals(newMorpheme)) {

						controller.fireWarning("Morpheme '" + newMorpheme 
								+ "' already exists with an other description",
								null);
						break;
					}

					if(m.getDescription().equals(newDescription)) {

						controller.fireWarning("Another morpheme with "
								+ "this description already exists",
								null);
						break;
					}
				}

				morpheme.setMorpheme(newMorpheme);
				morpheme.setDescription(newDescription);
			}
			catch(InvalidPhonemeSequenceException ex) {

				controller.fireError("Illegal morpheme '" 
						+ newMorpheme + "'",null);
			}
			catch(UnknownIdException ex) {

			}
		}


		//delete morpheme
		if (e.getSource() == deleteButton) {

			int row    = table.getSelectedRow();

			if (row < 0) {

				return;
			}

			Integer morphemeId = (Integer)table.getValueAt(row,0);

			Collection<Morpheme> morphemes = language.getMorphemes();
			Iterator<Morpheme> it = morphemes.iterator();

			while (it.hasNext()) {

				Morpheme morpheme = it.next();

				if (morphemeId.equals(morpheme.getId())) {

					language.removeMorpheme(morpheme);
					break;
				}
			}
		}
	}
}


class WordList extends JPanel implements ActionListener,Observer {

	private static final long serialVersionUID = 2171522915622298480L;


	class WordTableModel extends AbstractTableModel implements Observer {

		private static final long serialVersionUID = 1430519823179688342L;


		@Override
		public synchronized int getRowCount() {

			return language.getWords().size();
		}


		@Override
		public int getColumnCount() {

			return 2;
		}


		@Override
		public synchronized Object getValueAt(int row,int column) {

			Collection<Word> words = language.getWords();
			Iterator<Word> it = words.iterator();

			for (int i = 0; i < row;i++) {

				it.next();
			}

			if (it.hasNext()) {

				Word word = it.next();

				if(column == 0) {

					return word;
				}

				return word.getTranslation();
			}

			return "";
		}


		@Override
		public synchronized void update(Observable observable,Object o) {

			int row = table.getSelectedRow();

			this.fireTableStructureChanged();

			if (row > -1 && row < tableModel.getRowCount()) {

				table.addRowSelectionInterval(row,0);
			}

			controller.repaint();
		}


		public void setActive(boolean active) {

			if (active) {

				language.addObserver(this);
			}
			else {

				language.deleteObserver(this);
			}
		}
	}


	class StateTableModel extends AbstractTableModel implements Observer {

		private static final long serialVersionUID = -434432049533345357L;
		private Word word;
		private StateCellRenderer renderer = new StateCellRenderer();


		@Override
		public synchronized int getRowCount() {


			try {

				return word.getWordStates().size();
			}
			catch(Exception ex) {

				return 0;
			}
		}


		@Override
		public int getColumnCount() {

			try {

				return word.getWordClass().getInflections().size() + 2;
			}
			catch(Exception ex) {

				return 0;
			}
		}


		@Override
		public synchronized Object getValueAt(int row,int column) {

			try {

				WordState state = word.getWordStates().get(row);


				if (column == 0) {
					JCheckBox box =  new JCheckBox();
					return box;

				}

				if (column == word.getWordClass().getInflections().size() + 1) {

					JTextField tf = new JTextField(state.getParsedWord(),10);
					JPanel panel = new JPanel();
					panel.add(tf);

					int h = (int)panel.getPreferredSize().getHeight();
					if (h > stateTable.getRowHeight(h)) {

						stateTable.setRowHeight(h);
					}

					return panel;

				}


				Regularity regularity = state.getRegularities().get(column - 1);
				InflectionState inflectionState = regularity.getInflectionState();

				JLabel inflectionStateLabel = 
					new JLabel(inflectionState.getName());
				JComboBox regularityBox = 
					new JComboBox(new Vector<Regularity>(inflectionState.getRegularities()));
				regularityBox.setSelectedItem(regularity);

				JPanel panel = new JPanel(new BorderLayout());
				panel.add(inflectionStateLabel,BorderLayout.NORTH);
				panel.add(regularityBox,BorderLayout.CENTER);

				int h = (int)panel.getPreferredSize().getHeight();
				if (h > stateTable.getRowHeight(h)) {

					stateTable.setRowHeight(h);
				}

				return panel;
			}
			catch(Exception ex) {

				return "";
			}
		}


		public void setWord(Word aWord) {

			if (word != null) {

				word.deleteObserver(this);
			}

			word = aWord;

			if (word != null) {

				word.addObserver(this);
			}

			update(word,null);
		}

		@Override
		public synchronized void update(Observable observable,Object o) {

			this.fireTableStructureChanged();
			int columnCount = stateTable.getColumnCount();

			for (int i = 0;i < columnCount;i++) {

				stateTable.getColumnModel().getColumn(i).setCellRenderer(renderer);

				if (i == 0) {

					stateTable.getColumnModel().getColumn(i).setHeaderValue("Irregular");
				}
				else {

					if (i+1 == columnCount) {

						stateTable.getColumnModel().getColumn(i).setHeaderValue("Form");
					}
					else {

						Inflection inflection = word.getWordClass().getInflections().get(i - 1);
						stateTable.getColumnModel().getColumn(i).setHeaderValue("Inflection:" 
								+ inflection.getName());
					}
				}
			}

			stateTable.doLayout();
			controller.repaint();
		}


		public void setActive(boolean active) {

			if (active) {

				stateTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				language.addObserver(this);
			}
			else {

				language.deleteObserver(this);
			}
		}
	}



	class StateCellRenderer implements TableCellRenderer {

		private Object currentValue;

		public Object getCellRendererValue() {
			return currentValue;
		}

		@Override
		public Component getTableCellRendererComponent(JTable aTable,
				Object value,
				boolean isSelected,
				boolean hasFocus,
				int row,
				int column) {

			currentValue = value;

			if (value == null) {

				return new JPanel();
			}

			if(value instanceof Component) {

				JPanel panel = new JPanel(new GridLayout(1,1));
				panel.add((Component)value);
				return panel;
			}

			return new JPanel();
		}
	}

	private UIController controller;
	private Language language;

	private WordTableModel tableModel = new WordTableModel();
	private StateTableModel stateTableModel = new StateTableModel();

	private JTable table = new JTable(tableModel);
	private JTable stateTable = new JTable(stateTableModel);
	private JScrollPane scrollPane = new JScrollPane(table);
	private JScrollPane wordStatesPane = new JScrollPane(stateTable);;

	private JLabel wordLabel        = new JLabel("Root:  ");
	private JLabel translationLabel = new JLabel("Translation:  ");
	private JLabel descriptionLabel = new JLabel("Description:  ");
	private JLabel wordClassLabel = new JLabel("Word Class:  ");
	private JTextField wordTextField = new JTextField(10);
	private JTextField translationTextField = new JTextField(10);
	private JTextArea descriptionTextArea = new JTextArea(5,10);
	private JComboBox wordClassComboBox = new JComboBox();

	private JButton addButton = new JButton("Add");
	private JButton editButton = new JButton("Edit");
	private JButton deleteButton = new JButton("Delete");

	private JPanel tablePanel            = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel actionPanel           = new JPanel(new BorderLayout());
	private JPanel buttonPanel           = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel wordInputPanel        = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel translationInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel descriptionLabelPanel = new JPanel(new GridLayout(5,1));
	private JPanel descriptionInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel wordClassInputPanel   = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel inputPanel1           = new JPanel(new GridLayout(3,1));
	private JPanel inputPanel2           = new JPanel(new BorderLayout());


	public WordList(UIController aController, Language aLanguage) {

		controller = aController;
		language = aLanguage;

		addButton.addActionListener(this);
		editButton.addActionListener(this);
		deleteButton.addActionListener(this);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);

		table.setTableHeader(null);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				int row = table.getSelectedRow();

				if (row < 0) {

					editButton.setEnabled(false);
					return;
				}

				Word selectedWord = (Word)tableModel.getValueAt(row,0);

				editButton.setEnabled(true);
				wordTextField.setText(selectedWord.getRoot());
				translationTextField.setText(selectedWord.getTranslation());
				descriptionTextArea.setText(selectedWord.getDescription());
				wordClassComboBox.setSelectedItem(selectedWord.getWordClass());


				stateTableModel.setWord(selectedWord);
			}
		});


		wordClassComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				//WordClass wc = (WordClass)wordClassComboBox.getSelectedItem();
				//TODO: continue coding
			}
		});

		tablePanel.add(scrollPane);

		buttonPanel.add(editButton);
		buttonPanel.add(addButton);
		buttonPanel.add(deleteButton);

		//set label size
		Dimension wordSize = wordLabel.getPreferredSize();
		Dimension descriptionSize = descriptionLabel.getPreferredSize();
		Dimension labelSize = new Dimension();
		double width = 0;
		double height = 0;

		if (wordSize.getWidth() > descriptionSize.getWidth()) {

			width = wordSize.getWidth();
		}
		else {

			width = descriptionSize.getWidth();
		}

		if (wordSize.getHeight() > descriptionSize.getHeight()) {

			height = wordSize.getHeight();
		}
		else {

			height = descriptionSize.getHeight();
		}

		labelSize.setSize(width,height);

		wordLabel.setPreferredSize(labelSize);
		translationLabel.setPreferredSize(labelSize);
		descriptionLabel.setPreferredSize(labelSize);
		wordClassLabel.setPreferredSize(labelSize);

		descriptionLabelPanel.add(descriptionLabel);

		wordInputPanel.add(wordLabel);
		wordInputPanel.add(wordTextField);
		translationInputPanel.add(translationLabel);
		translationInputPanel.add(translationTextField);
		descriptionInputPanel.add(descriptionLabelPanel);
		descriptionInputPanel.add(new JScrollPane(descriptionTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		wordClassInputPanel.add(wordClassLabel);
		wordClassInputPanel.add(wordClassComboBox);

		inputPanel1.add(wordInputPanel);
		inputPanel1.add(translationInputPanel);
		inputPanel1.add(wordClassInputPanel);

		inputPanel2.add(inputPanel1,BorderLayout.NORTH);
		inputPanel2.add(descriptionInputPanel,BorderLayout.CENTER);

		actionPanel.add(inputPanel2,BorderLayout.NORTH);
		actionPanel.add(wordStatesPane,BorderLayout.CENTER);
		actionPanel.add(buttonPanel,BorderLayout.SOUTH);

		this.setBorder(BorderFactory.createTitledBorder("Words"));
		this.setLayout(new BorderLayout());
		this.add(tablePanel,BorderLayout.WEST);
		this.add(actionPanel,BorderLayout.CENTER);

		Dimension size = scrollPane.getPreferredSize();
		size.setSize(250,600);
		scrollPane.setPreferredSize(size);

		wordStatesPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		wordStatesPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}


	public void setUp() {

		language.addObserver(this);
		update(language,null);

		tableModel.setActive(true);
		stateTableModel.setActive(true);

		if (table.getRowCount() > 0) {

			table.addRowSelectionInterval(0,0);
		}
	}


	public void shutDown() {

		language.deleteObserver(this);
		tableModel.setActive(false);
		stateTableModel.setActive(true);
	}


	@Override
	public void update(Observable observable,Object o) {

		ArrayList<WordClass> wordClasses = 
			new ArrayList<WordClass>(new TreeSet<WordClass>(language.getWordClasses()));

		wordClassComboBox.removeAllItems();

		for (WordClass wc:wordClasses) {

			wordClassComboBox.addItem(wc);
		}

		if (wordClasses.size() > 0) {

			wordClassComboBox.setSelectedIndex(0);
		}

		wordClassComboBox.setPreferredSize(wordTextField.getPreferredSize());
	}


	@Override
	public synchronized void actionPerformed(ActionEvent e) {

		//add word
		if (e.getSource() == addButton) {

			String word        = wordTextField.getText();
			String translation = translationTextField.getText();
			String description = descriptionTextArea.getText();

			if (word.equals("")) {

				controller.fireError("Word root is empty",null);
				return;
			}

			if (translation.equals("")) {

				controller.fireError("Translation is empty",null);
				return;
			}

			try {

				for (Word m:language.getWords()) {

					if(m.getRoot().equals(word) && 
							m.getTranslation().equals(translation)) {

						controller.fireError("Word '" + word 
								+ "' already exists with this translation",
								null);
						return;
					}
				}

				for (Word m:language.getWords()) {

					if(m.getRoot().equals(word)) {

						controller.fireWarning("Word '" + word 
								+ "' already exists with an other description",
								null);
						break;
					}

					if(m.getTranslation().equals(translation)) {

						controller.fireWarning("Another word with "
								+ "this translation already exists",
								null);
						break;
					}
				}

				if (!word.equals("")) {

					language.addWord(word,
							translation,
							description,
							(WordClass)wordClassComboBox.getSelectedItem());
					wordTextField.setText("");
					translationTextField.setText("");
					descriptionTextArea.setText("");
				}
			}
			catch(ItemAlreadyExistsException ex) {

				controller.fireError("Word '" + word + "' already exist",null);
			}
			catch(WordSyntaxException ex) {

				controller.fireError("Illegal syntax in word '" + word + "'",null);
			}

			return;
		}


		//edit word
		if (e.getSource() == editButton) {

			int row    = table.getSelectedRow();
			int column = table.getSelectedColumn();

			if (row < 0 || column < 0) {

				return;
			}

			String newWord = wordTextField.getText();
			String newTranslation = translationTextField.getText();
			String newDescription = descriptionTextArea.getText();
			WordClass newWordClass = (WordClass)wordClassComboBox.getSelectedItem();

			if (newWord.equals("")) {

				controller.fireError("Word root is empty",null);
				return;
			}

			if (newTranslation.equals("")) {

				controller.fireError("Translation is empty",null);
				return;
			}

			try {

				Word word = (Word)table.getValueAt(row,0);

				for (Word m:language.getWords()) {

					if(m != word &&
							m.getRoot().equals(newWord) && 
							m.getTranslation().equals(newTranslation)) {

						controller.fireError("Word '" + newWord 
								+ "' already exists with this description",
								null);
						return;
					}
				}

				for (Word m:language.getWords()) {

					if(m != word &&
							m.getRoot().equals(newWord)) {

						controller.fireWarning("Word '" + newWord 
								+ "' already exists with an other description",
								null);
						break;
					}

					if(m != word &&
							m.getDescription().equals(newDescription)) {

						controller.fireWarning("Another word with "
								+ "this description already exists",
								null);
						break;
					}
				}

				word.setRoot(newWord);
				word.setTranslation(newTranslation);
				word.setDescription(newDescription);
				word.setWordClass(newWordClass);
			}
			catch(WordSyntaxException ex) {

				controller.fireError("Illegal syntax in word '" + newWord + "'",null);
			}
			catch(NullPointerException ex) {

				//thrown when newWordClass == null 
			}
		}


		//delete word
		if (e.getSource() == deleteButton) {

			int row    = table.getSelectedRow();

			if (row < 0) {

				return;
			}

			language.removeWord((Word)table.getValueAt(row,0));
		}
	}
}
