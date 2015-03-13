package sapere.controller.reactionmanager;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import sapere.controller.Node;
import sapere.model.Lsa;
import sapere.model.reaction.Reaction;

public class SpaceMonitor {
	
	JTextArea lsastext,reactstext;
	
	public SpaceMonitor(){
		
		JFrame frame = new JFrame("Node at "+Node.getServicesPort());
		
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		
		lsastext= new JTextArea();
		lsastext.setEditable(false);
		reactstext= new JTextArea();
		reactstext.setEditable(false);
		JLabel lsaslabel = new JLabel("LSA Space content");
		JLabel reactslabel = new JLabel("Current reactions");
		
		JScrollPane lsaspane = new JScrollPane(lsastext);
		JScrollPane reactspane = new JScrollPane(reactstext);
		
		main.add(lsaslabel);
		
		main.add(lsaspane);
		//main.add(reactslabel);
		
		//main.add(reactspane);
		
		frame.setLayout(new GridLayout(1,1));
		frame.getContentPane().add(main);
		frame.pack();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(510, 250);
		frame.setResizable(true);
		frame.setVisible(true);
	}
	
	public void update(final Lsa[] list){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				String tmp="";
				for(Lsa e:list){
					tmp+=e.toString()+"\n";
				}
				lsastext.setText(tmp);
			}
			
		});
	}
	
	public void update(final Reaction[] list){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				String tmp="";
				for(Reaction r:list){
					tmp+=r.toString()+"\n";
				}
				reactstext.setText(tmp);
			}
			
		});
	}

}
