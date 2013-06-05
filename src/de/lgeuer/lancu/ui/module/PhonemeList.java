package de.lgeuer.lancu.ui.module;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import de.lgeuer.lancu.core.ItemAlreadyExistsException;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.core.entity.Phoneme;
import de.lgeuer.lancu.ui.UIController;

class PhonemeList extends JPanel implements ActionListener {

    private static final long serialVersionUID = -4259564860060536921L;


    class PhonemeTableModel extends AbstractTableModel implements Observer {

	private static final long serialVersionUID = 1251353709103796569L;

	@Override
	public synchronized int getRowCount() {

	    return language.getPhonemes().size() + 1;
	}


	@Override
	public int getColumnCount() {

	    return 1;
	}


	@Override
	public synchronized Object getValueAt(int row,int column) {

	    TreeSet<Phoneme> phonemes = new TreeSet<Phoneme>(language.getPhonemes());
	    Iterator<Phoneme> it = phonemes.iterator();

	    for (int i = 0; i < row;i++) {

		it.next();
	    }

	    if (it.hasNext()) {

		return it.next().getPhonemeAsString();
	    }

	    return "";
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
	    TreeSet<Phoneme> phonemes = new TreeSet<Phoneme>(language.getPhonemes());
	    Iterator<Phoneme> it = phonemes.iterator();

	    for (int i = 0; i < row;i++) {

		it.next();
	    }

	    if (it.hasNext()) {

		it.next().setPhoneme(value.toString());
	    } else {
		try {
		    language.addPhoneme(value.toString());
		} catch (ItemAlreadyExistsException e) {
		    System.out.println("Phoneme alread exists: " + value);
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
	public synchronized void update(Observable observable,Object o) {

	    this.fireTableStructureChanged();
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


    public class PhonemeCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = -3232476150774248415L;

	public PhonemeCellEditor(){
	    super(new JFormattedTextField());
	}

	@Override
	public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column) {
	    JFormattedTextField editor = (JFormattedTextField) super.getTableCellEditorComponent(jTable, value, isSelected, row, column);

	    if (value instanceof String){

		editor.setHorizontalAlignment(SwingConstants.CENTER);
		editor.setValue(value);
	    }
	    return editor;
	}

	@Override
	public boolean stopCellEditing() {
	    try {
		// try to get the value
		this.getCellEditorValue();
		return super.stopCellEditing();
	    } catch (Exception ex) {
		return false;
	    }
	}

	@Override
	public Object getCellEditorValue() {
	    // get content of textField
	    String str = (String) super.getCellEditorValue();
	    if(str.length() > 1) {
		throw new RuntimeException("Phoneme to long");
	    }

	    return str;
	}
    }

    private UIController controller;
    private Language language;

    private PhonemeTableModel tableModel = new PhonemeTableModel();
    private JTable table = new JTable(tableModel);
    private JScrollPane scrollPane = new JScrollPane(table);

    private JLabel phonemeLabel = new JLabel("Phoneme:  ");
    private JTextField phonemeTextField = new JTextField(1);
    private JButton addButton = new JButton("Add");
    private JButton deleteButton = new JButton("Delete");

    private JPanel tablePanel  = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JPanel actionPanel = new JPanel(new BorderLayout());
    private JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JPanel inputPanel  = new JPanel(new FlowLayout(FlowLayout.LEFT));


    public PhonemeList(UIController aController, Language aLanguage) {

	controller = aController;
	language = aLanguage;

	addButton.addActionListener(this);
	deleteButton.addActionListener(this);

	tablePanel.add(scrollPane);

	buttonPanel.add(addButton);
	buttonPanel.add(deleteButton);

	inputPanel.add(phonemeLabel);
	inputPanel.add(phonemeTextField);

	actionPanel.add(inputPanel,BorderLayout.NORTH);
	actionPanel.add(buttonPanel,BorderLayout.CENTER);

	Dimension size = scrollPane.getPreferredSize();
	size.setSize(100,400);
	scrollPane.setPreferredSize(size);

	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	table.setTableHeader(null);

	this.setLayout(new BorderLayout());
	this.add(tablePanel,BorderLayout.NORTH);
	this.add(actionPanel,BorderLayout.CENTER);

	this.setBorder(BorderFactory.createTitledBorder("Phonemes"));
    }


    public void setUp() {

	tableModel.setActive(true);
    }


    public void shutdown() {

	tableModel.setActive(false);
    }


    @Override
    public synchronized void actionPerformed(ActionEvent e) {

	//add phoneme
	if (e.getSource() == addButton) {

	    String phoneme = phonemeTextField.getText();

	    try {

		if (!phoneme.equals("")) {

		    language.addPhoneme(phoneme);
		    phonemeTextField.setText("");
		}
	    }
	    catch(ItemAlreadyExistsException ex) {

		controller.fireError("Phoneme '" + phoneme + "' already exists",null);
	    }

	    catch(IllegalArgumentException ex) {

		controller.fireError("Illegal Phoneme '" + phoneme + "'",null);
	    }

	    return;
	}

	//delete phoneme
	if (e.getSource() == deleteButton) {

	    int row    = table.getSelectedRow();
	    int column = table.getSelectedColumn();

	    if (row < 0 || column < 0) {

		return;
	    }

	    String phonemeString = (String)table.getValueAt(row,column);

	    Collection<Phoneme> phonemes = language.getPhonemes();
	    Iterator<Phoneme> it = phonemes.iterator();

	    while (it.hasNext()) {

		Phoneme phoneme = it.next();

		if (phoneme.getPhonemeAsString().equals(phonemeString)) {

		    language.removePhoneme(phoneme);
		    break;
		}
	    }
	}
    }
}