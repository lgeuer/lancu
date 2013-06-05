/*
 * File: DefaultUIController.java
 * Author: Lars Geuer
 * Date: 24.4.2007
 */

package de.lgeuer.lancu.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import de.lgeuer.lancu.Environment;
import de.lgeuer.lancu.core.ViolationException;
import de.lgeuer.lancu.core.ViolationExceptionItem;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.store.JDOFileStorage;
import de.lgeuer.lancu.store.Storage;
import de.lgeuer.lancu.ui.module.LanguageModuleInitializer;
import de.lgeuer.lancu.ui.module.LanguageModulesWrapperInitializer;
import de.lgeuer.lancu.ui.module.LanguagesModuleInitializer;
import de.lgeuer.lancu.ui.module.ManageLanguageModule;
import de.lgeuer.lancu.ui.module.ModuleIntializer;
import de.lgeuer.lancu.ui.module.PhonemeModuleInitializer;
import de.lgeuer.lancu.ui.module.UIModule;
import de.lgeuer.lancu.ui.module.VocabularyModuleInitializer;
import de.lgeuer.lancu.ui.module.WordClassModuleInitializer;


public class DefaultUIController implements UIController, Observer {

    private LancuFrame frame;

    private Storage storage;

    private JMenuBar menuBar = new JMenuBar();

    private JMenu fileMenu = new JMenu("File");

    private JMenu languageMenu = new JMenu("Languages");

    private JMenu moduleMenu = new JMenu("Modules");

    private JMenu transactionMenu = new JMenu("Transaction");

    private JMenuItem openItem = new JMenuItem("Open ...");

    private JMenuItem saveItem = new JMenuItem("Save");

    private JMenuItem saveAsItem = new JMenuItem("Save As ...");

    private JMenuItem exitItem = new JMenuItem("Exit");

    private JMenuItem languageDummyItem = new JMenuItem("<No Entry>");

    private JMenuItem transactionDummyItem = new JMenuItem("<No Entry>");

    private List<UIModule> openModules = new ArrayList<UIModule>();

    private List<LanguageModuleInitializer> languageModuleInitializers = new ArrayList<LanguageModuleInitializer>();

    class UIModuleMenuItemActionListener implements ActionListener {

	private UIModule module;

	private UIController controller;

	public UIModuleMenuItemActionListener(UIModule aModule,
		UIController aController) {

	    module = aModule;
	    controller = aController;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	    controller.openUIModule(module);
	}
    }

    class CommitActionListener implements ActionListener {

	private Language language;

	public CommitActionListener(Language aLanguage) {

	    language = aLanguage;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	    try {

		language.commit();
		fireConfirmation("The language has been committed.", null);
	    } catch (ViolationException ex) {

		fireError("Error while committing language.", null);
		System.out.println("There were errors while committing '"
			+ language.getName() + "'");
		System.out.println("=====================");

		for (ViolationExceptionItem item : ex.getItems()) {

		    System.out.println(item.getMessage());
		    System.out.println("---------------------");
		}

		JFrame commitErrorFrame = new CommitViolationFrame(language.getName(), ex.getItems());
		commitErrorFrame.setVisible(true);
	    } catch (Exception ex) {
		throw new RuntimeException(ex);
	    }
	}
    }

    class RollbackActionListener implements ActionListener {

	private Language language;

	public RollbackActionListener(Language aLanguage) {

	    language = aLanguage;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	    language.rollback();
	    fireConfirmation("The language has been rollend back.", null);
	}
    }

    public DefaultUIController(LancuFrame aFrame) {

	frame = aFrame;
	storage = new JDOFileStorage(frame);

	// for empty menus
	languageDummyItem.setEnabled(false);
	transactionDummyItem.setEnabled(false);

	// set up file menu
	openItem.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {

		try {

		    Environment.getEnvironment()
		    .setLanguages(storage.restore());
		    fireConfirmation("Language list has been loaded.", null);
		} catch (IllegalStateException ex) {

		    // action canceled
		} catch (Exception ex) {

		    fireError("Error while loading language list", null);
		    ex.printStackTrace(); // TODO: replace with logger
		}
	    }
	});

	saveItem.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {

		try {

		    storage.save(Environment.getEnvironment().getLanguages());
		    fireConfirmation("Language list has been saved.", null);
		} catch (Exception ex) {

		    fireError("Error while saving language list", null);
		    ex.printStackTrace(); // TODO: replace with logger
		}
	    }
	});

	saveAsItem.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {

		try {

		    storage.saveAs(Environment.getEnvironment().getLanguages());
		    fireConfirmation("Language list has been saved.", null);
		} catch (Exception ex) {

		    fireError("Error while saving language list", null);
		    ex.printStackTrace(); // TODO: replace with logger
		}
	    }
	});

	exitItem.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {

		frame.setVisible(false);
		frame.dispose();
	    }
	});

	fileMenu.add(openItem);
	fileMenu.add(saveItem);
	fileMenu.add(saveAsItem);
	fileMenu.add(exitItem);

	if (!storage.implementsSaveAs()) {

	    saveAsItem.setEnabled(false);
	}

	menuBar.add(fileMenu);

	// fill modules menu

	// init modules (modules could be read from a file later)
	ModuleIntializer languages = new LanguagesModuleInitializer();
	languages.initialize(this);

	LanguageModuleInitializer moduleInitializer = new LanguageModulesWrapperInitializer();
	moduleInitializer.initialize(this);
	addLanguageMenuItem(moduleInitializer);

	LanguageModuleInitializer phonemes = new PhonemeModuleInitializer();
	phonemes.initialize(this);
	addLanguageMenuItem(phonemes);

	LanguageModuleInitializer wordClasses = new WordClassModuleInitializer();
	wordClasses.initialize(this);
	addLanguageMenuItem(wordClasses);

	LanguageModuleInitializer vocabularies = new VocabularyModuleInitializer();
	vocabularies.initialize(this);
	addLanguageMenuItem(vocabularies);

	moduleMenu.add(languageMenu);
	menuBar.add(moduleMenu);

	menuBar.add(transactionMenu);

	frame.setJMenuBar(menuBar);

	Environment.getEnvironment().getLanguages().addObserver(this);

	openUIModule(new ManageLanguageModule(this));
    }

    @Override
    public void display() {

	frame.setVisible(true);
    }

    @Override
    public void openUIModule(UIModule module) {

	if (!openModules.contains(module)) {

	    openModules.add(module);
	    frame.add(module);
	    module.setUp();
	}
    }

    @Override
    public void closeUIModule(UIModule module) {

	if (openModules.contains(module)) {

	    module.shutDown();
	    openModules.remove(module);
	    frame.remove(module);
	}
    }

    @Override
    public void addMenu(JMenu menu) {

	menuBar.add(menu);
	frame.repaint();
    }

    @Override
    public void addModuleMenuItem(UIModule module) {

	JMenuItem item = new JMenuItem(module.getShortName());
	item.addActionListener(new UIModuleMenuItemActionListener(module, this));
	moduleMenu.add(item);

	updateMenu();

    }

    @Override
    public void addLanguageMenuItem(LanguageModuleInitializer initializer) {

	languageModuleInitializers.add(initializer);
	updateMenu();
    }

    // not used
    public void removeLanguageMenuItem(LanguageModuleInitializer initializer) {

	languageModuleInitializers.remove(initializer);
	updateMenu();
    }

    private void updateMenu() {

	Map<String, Language> languages = Environment.getEnvironment().getLanguages().getMap();

	languageMenu.removeAll();
	transactionMenu.removeAll();

	for (Language language : languages.values()) {

	    JMenu optionMenu = new JMenu(language.getName());

	    // update modules->languages
	    for (LanguageModuleInitializer initializer : languageModuleInitializers) {

		for(UIModule module : initializer.getLanguageModules(language)) {

		    JMenuItem item = new JMenuItem(module.getShortName());

		    item.addActionListener(new UIModuleMenuItemActionListener(module, this));
		    optionMenu.add(item);
		}

	    }

	    languageMenu.add(optionMenu);

	    // update transaction
	    JMenu transactionLanguageMenu = new JMenu(language.getName());
	    JMenuItem rollback = new JMenuItem("Rollback");
	    JMenuItem commit = new JMenuItem("Commit");

	    rollback.addActionListener(new RollbackActionListener(language));
	    commit.addActionListener(new CommitActionListener(language));

	    transactionLanguageMenu.add(rollback);
	    transactionLanguageMenu.add(commit);

	    transactionMenu.add(transactionLanguageMenu);
	}

	if (languages.size() == 0) {

	    languageMenu.add(languageDummyItem);
	    transactionMenu.add(transactionDummyItem);
	}

	languageMenu.repaint();
	transactionMenu.repaint();
    }

    @Override
    public void update(Observable observable, Object o) {

	// close modules of not any more existing languages
	Map<String, Language> languages = Environment.getEnvironment()
		.getLanguages().getMap();
	for (UIModule module : openModules) {

	    try {

		if (!languages.containsValue(module.getLanguage())) {

		    closeUIModule(module);
		    continue;
		}
	    } catch (UnsupportedOperationException ex) {

		// not language depended
	    }
	}

	updateMenu();

	frame.repaint();
    }

    @Override
    public void fireError(String message, UIModule owner) {

	frame.fireMessage(message, Color.WHITE, Color.RED);
    }

    @Override
    public void fireWarning(String message, UIModule owner) {

	frame.fireMessage(message, Color.BLACK, Color.YELLOW);
    }

    @Override
    public void fireConfirmation(String message, UIModule owner) {

	frame.fireMessage(message, Color.BLACK, Color.GREEN);
    }

    @Override
    public boolean confirm(String message, UIModule owner) {

	int result = JOptionPane.showConfirmDialog(frame, message,
		"Confirmation", JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE);

	return result == JOptionPane.OK_OPTION;
    }

    @Override
    public void repaint(UIModule module) {

	this.repaint();
    }

    @Override
    public void repaint() {

	frame.repaint();
    }
}