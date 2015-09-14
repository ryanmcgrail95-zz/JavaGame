package gfx;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import cont.GameController;

public class ErrorPopup extends JFrame {
	private JFrame me;
	private static int numberOpen = 0;
	
	private ErrorPopup(String str, boolean forceAbort) {
		super("Error");
		
		me = this;
		numberOpen++;

		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setDefaultLookAndFeelDecorated(true);
		
		// Center Frame
		setLocationRelativeTo(null);
		
		str = formatLabel(str);
		JLabel text = new JLabel(str);
			getContentPane().add(text, BorderLayout.NORTH);
		
		if(!forceAbort) {
			JButton closeButton = new JButton("Ignore");
				closeButton.setSize(100,50);
				add(closeButton);
				closeButton.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent e) {
				    	numberOpen--;
				    	me.dispose();
				    }
				});
				getContentPane().add(closeButton, BorderLayout.WEST);
		}
		
		JButton endButton = new JButton("Abort");
			endButton.setSize(100,50);
			add(endButton);
			endButton.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
			    	GameController.end();
			    }
			});
			getContentPane().add(endButton, BorderLayout.EAST);

		// Autosize
		pack();
		
		// Display!
		setVisible(true);
		
		//			System.exit(error);

	}
	
	private String formatLabel(String str) {
		str = str.replace("\n", "<br>");
		
		str = "<html><body>" + str;
		str = str + "</body></html>)";
		
		return str;
	}

	public static void open(String text, boolean forceAbort) {
		if(!isOpen())
			new ErrorPopup(text, forceAbort);
	}

	public static boolean isOpen() {
		return numberOpen > 0;
	}
}
