package gfx;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import cont.GameController;

public class OKPopup extends JFrame {
	private JFrame me;
	private static int numberOpen = 0;
	
	private OKPopup(String str) {
		super("OK");
		
		me = this;
		numberOpen++;

		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setDefaultLookAndFeelDecorated(true);
		
		// Center Frame
		setLocationRelativeTo(null);
		
		str = formatLabel(str);
		JLabel text = new JLabel(str, SwingConstants.CENTER);
			getContentPane().add(text, BorderLayout.NORTH);
		
		JButton closeButton = new JButton("OK");
			closeButton.setSize(20,30);
			add(closeButton);
			closeButton.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
			    	numberOpen--;
			    	me.dispose();
			    }
			});
			getContentPane().add(closeButton, BorderLayout.SOUTH);
		
		// Autosize
		pack();
		
		// Display!
		setVisible(true);
	}
	
	private String formatLabel(String str) {
		str = str.replace("\n", "<br>");
		
		str = "<html><body>" + str;
		str = str + "</body></html>)";
		
		return str;
	}

	public static void open(String text) {
		if(!isOpen())
			new OKPopup(text);
	}

	public static boolean isOpen() {
		return numberOpen > 0;
	}
}
