package sapere.controller.stub;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import sapere.controller.InternalAgent;
import sapere.model.LogicContentFilter;
import sapere.model.Lsa;
import sapere.model.LsaFactory;
import sapere.model.communication.Message;

public class SapereService extends InternalAgent implements ActionListener{
	
	private JTextField injectT,injectObT,injectObFil,removeT,updateTcont,updateTid,observeTId,observeFil;
	private JButton inject,observe,remove,injectOb,update;
	private JTextArea text;
	private int port;
	
	private LsaFactory factory = LsaFactory.getInstance();
	
	public SapereService(int port){
		super("SapereAgentGUI");
		this.port=port;
		JFrame frame = new JFrame("Sapere Agent GUI "+port);
		
		
		inject = new JButton("INJECT");
		observe = new JButton("OBSERVE");
		remove = new JButton("REMOVE");
		update = new JButton("UPDATE");
		injectOb = new JButton("INJECT-OBSERVE");
		
		injectT = new JTextField();
		injectT.setBorder(BorderFactory.createTitledBorder("LSA"));
		observeTId = new JTextField();
		observeTId.setBorder(BorderFactory.createTitledBorder("ID"));
		observeFil = new JTextField();
		observeFil.setBorder(BorderFactory.createTitledBorder("FILTER"));
		removeT = new JTextField();
		removeT.setBorder(BorderFactory.createTitledBorder("ID"));
		updateTid = new JTextField();
		updateTid.setBorder(BorderFactory.createTitledBorder("ID"));
		updateTcont = new JTextField();
		updateTcont.setBorder(BorderFactory.createTitledBorder("CONTENT"));
		JPanel upPanel = new JPanel(new GridLayout(1, 2));
		upPanel.add(updateTid);
		upPanel.add(updateTcont);
		JPanel obPanel = new JPanel(new GridLayout(1, 2));
		obPanel.add(observeTId);
		obPanel.add(observeFil);
		injectObFil = new JTextField();
		injectObFil.setBorder(BorderFactory.createTitledBorder("FILTER"));
		injectObT = new JTextField();
		injectObT.setBorder(BorderFactory.createTitledBorder("LSA"));
		JPanel inobPanel = new JPanel(new GridLayout(1, 2));
		inobPanel.add(injectObT);
		inobPanel.add(injectObFil);
		
		inject.addActionListener(this);
		injectOb.addActionListener(this);
		observe.addActionListener(this);
		update.addActionListener(this);
		remove.addActionListener(this);
		
		JPanel main = new JPanel(new GridLayout(5, 2));
		JPanel log = new JPanel(new GridLayout(1, 1));
		
		text = new JTextArea();
		text.setEditable(false);
		JScrollPane scroll = new JScrollPane(text);
		log.add(scroll);
		
		main.add(injectT);
		main.add(inject);
		main.add(obPanel);
		main.add(observe);
		main.add(removeT);
		main.add(remove);
		main.add(upPanel);
		main.add(update);
		main.add(inobPanel);
		main.add(injectOb);
		
		frame.setLayout(new GridLayout(2,1));
		frame.getContentPane().add(main);
		frame.getContentPane().add(log);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(50, 50, 600, 420);
		frame.setResizable(false);
		frame.setVisible(true);
		
		new Thread(){
			public void run(){
				while(true){
					try {
						final Message msg = input.take();
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								log(msg.toString(),false);							
							}
						});
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		System.out.println("Service activated.");
	}
	
	public static void main(String args[]){
		new SapereService(Integer.parseInt(args[0]));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(inject)){
			try{
				Lsa lsa = factory.createLsaFromProlog(injectT.getText());
				SapereOperation op = new SapereOperation(SapereOperationType.Inject, null, lsa, null, null);
				log("request = "+op+"",false);
				SapereMiddleware.executeOperation(op, this, port);
			}catch(Exception ex){
				log(ex.getMessage(),true);
			}
		}
		else if(e.getSource().equals(observe)){
			try{
				LogicContentFilter lcf = null;
				if (!(observeFil.getText() == null) && !observeFil.getText().equals(""))
					lcf = new LogicContentFilter(observeFil.getText());
				if (!(observeTId.getText() == null) && !observeTId.getText().equals("")){
					SapereOperation op = new SapereOperation(SapereOperationType.Observe, observeTId.getText(), null, null, lcf);
					log("request = "+op+"",false);
					SapereMiddleware.executeOperation(op, this,port);
				}
				else{
					log("please fill all fields!",true);
				}
			}catch(Exception ex){
				log(ex.getMessage(),true);
			}
		}
		else if(e.getSource().equals(remove)){
			if (!removeT.getText().equals("")){
				SapereOperation op = new SapereOperation(SapereOperationType.Remove, removeT.getText(), null, null, null);
				log("request = "+op+"",false);
				SapereMiddleware.executeOperation(op, this,port);
			}
			else{
				log("please fill all fields!",true);
			}
		}
		else if(e.getSource().equals(update)){
			try{
				SapereOperation op = new SapereOperation(SapereOperationType.Update, updateTid.getText(), null, factory.createContentFromProlog(updateTcont.getText()), null);
				log("request = "+op+"",false);
				SapereMiddleware.executeOperation(op, this,port);
			}catch(Exception ex){
				log(ex.getMessage(),true);
			}
		}
		else if(e.getSource().equals(injectOb)){
			try{
				Lsa lsa = factory.createLsaFromProlog(injectObT.getText());
				LogicContentFilter lcf = null;
				if (!injectObFil.getText().equals(""))
					lcf = new LogicContentFilter(injectObFil.getText());
				SapereOperation op = new SapereOperation(SapereOperationType.InjectObserve, null, lsa, null, lcf);
				log("request = "+op+"",false);
				SapereMiddleware.executeOperation(op, this,port);
			}catch(Exception ex){
				log(ex.getMessage(),true);
			}
		}
	}
	
	private void log(String msg,boolean error){
		if (error){
			text.insert("ERROR ---- "+msg+"\n",text.getCaretPosition());
		}
		else{
			text.insert(msg+"\n", text.getCaretPosition());
		}
	}
}
