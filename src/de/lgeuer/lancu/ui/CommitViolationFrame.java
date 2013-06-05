package de.lgeuer.lancu.ui;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import de.lgeuer.lancu.core.ViolationExceptionItem;

public class CommitViolationFrame extends JFrame {

    class ViolationTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1251353709103796569L;

	@Override
	public synchronized int getRowCount() {

	    return violationItems.size();
	}

	@Override
	public int getColumnCount() {

	    return 1;
	}

	@Override
	public synchronized Object getValueAt(int row,int column) {

	    if (violationItems.size() >= row) {
		return violationItems.get(row).getMessage();
	    }
	    return "";
	}
    }


    private static final long serialVersionUID = -5638056991379350271L;

    private List<ViolationExceptionItem> violationItems;

    private ViolationTableModel tableModel = new ViolationTableModel();
    private JTable table = new JTable(tableModel);
    private JPanel mainPanel = new JPanel();

    public CommitViolationFrame(String languageName, List<ViolationExceptionItem> violationItems) {

	super("Error while commiting " + languageName);

	this.violationItems = violationItems;
	
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	table.setSize(table.getPreferredSize());
		
	mainPanel.add(table);
	mainPanel.setSize(mainPanel.getPreferredSize());
	this.add(mainPanel);
	this.setSize(this.getPreferredSize());
	
	//this.setBounds(50,50,500,150);	
    }

}
