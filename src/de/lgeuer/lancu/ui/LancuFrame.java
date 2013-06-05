/*
 * File: LancuFrame.java
 * Author: Lars Geuer
 * Date: 24.4.2007
 */

package de.lgeuer.lancu.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.lgeuer.lancu.ui.module.UIModule;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;


public class LancuFrame extends JFrame {

    private static final long serialVersionUID = 2191525849596962556L;

    private JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JLabel messageLabel = new JLabel("Lancu has been started...");

    private RootWindow rootWindow = null;
    private LancuFrame lancuFrame;

    public LancuFrame(String name) {

	super(name);
	
	lancuFrame = this;

	DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().
		getDefaultScreenDevice().getDisplayMode();

	messagePanel.add(messageLabel); //just to have something in the panel


	this.setLayout(new BorderLayout());
	//add(tabbedPane,BorderLayout.CENTER);
	add(messagePanel,BorderLayout.SOUTH);

	setSize(mode.getWidth(),mode.getHeight());
	setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }


    public void add(UIModule panel) {

	System.out.println("Adding Module: " + panel.getName());
	if (rootWindow == null) {
	    ViewMap viewMap = new ViewMap();
	    rootWindow = DockingUtil.createRootWindow(viewMap, true);
	    this.getContentPane().add(rootWindow, BorderLayout.CENTER);
	    RootWindowProperties properties = new RootWindowProperties();
	    properties.addSuperObject(new ShapedGradientDockingTheme().getRootWindowProperties());
	    rootWindow.getRootWindowProperties().addSuperObject(properties);
	    rootWindow.getRootWindowProperties().getDockingWindowProperties().setTitleProvider(new LanuDockingWindowTitleProvider());
	    rootWindow.addListener(new DockingWindowAdapter() {
		@Override
		public void windowClosed(DockingWindow window) {
		    System.out.println("Window closing");
		    if(window instanceof View) {
			Component component = ((View)window).getComponent();
			if (component instanceof UIModule) {
			    ((UIModule)component).remove();
			}
		    }
		}
	    });

	}
	DockingUtil.addWindow(panel.getDockingWindow(), rootWindow);


	//		int index;
	//		tabbedPane.add(panel);
	//		index = tabbedPane.indexOfComponent(panel);
	//		tabbedPane.setSelectedIndex(index);

	//		tabbedPane.repaint();
	rootWindow.repaint();
    }


    public void remove(UIModule panel) {

	System.out.println("Removing Module: " + panel.getName());

	//		tabbedPane.remove(panel);
	//		tabbedPane.repaint();
    }


    public synchronized void fireMessage(String message,Color fgColor,Color bgColor) {

	MessageThread thread = new MessageThread(messageLabel,messagePanel,message,fgColor,bgColor);
	thread.start();
    }
}



class MessageThread extends  Thread {

    private String message;
    private Color fgColor;
    private Color bgColor;
    private JLabel messageLabel;    
    private JPanel messagePanel;    


    public MessageThread(JLabel messageLabel,JPanel messagePanel,String message,Color fgColor,Color bgColor) {

	this.message = message;
	this.fgColor = fgColor;
	this.bgColor = bgColor;
	this.messageLabel = messageLabel;
	this.messagePanel = messagePanel;
    }


    @Override
    public void run() {

	synchronized(messagePanel) {

	    //JLabel label = new JLabel(message);

	    Color labelFg = messageLabel.getForeground();
	    Color panelBg = messagePanel.getBackground();

	    messageLabel.setText(message);
	    messageLabel.setForeground(fgColor);
	    messagePanel.setBackground(bgColor);

	    try {

		Thread.sleep(3000);
	    }
	    catch(InterruptedException ex) {

		//do nothing
	    }

	    messageLabel.setForeground(labelFg);
	    messagePanel.setBackground(panelBg);

	    try {

		Thread.sleep(100);
	    }
	    catch(InterruptedException ex) {

		//do nothing
	    }
	}
    }
}

