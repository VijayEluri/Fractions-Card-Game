package basic;

import java.awt.Component;

import javax.swing.JOptionPane;

import extras.SwingWorker;


public class FYIMessage {
	private SwingWorker worker;
	private String message;
	private Component parentContainer;
	
	public FYIMessage(Component parent, String msg) {
		message = msg;
		parentContainer = parent;
		worker = new SwingWorker() {
			@Override
			public Object construct() {
				JOptionPane.showMessageDialog(parentContainer, message);
				return "done";
			}
		};
		worker.start();
	}
	
	public void killMessage() {
		worker.interrupt();
	}
	
}
