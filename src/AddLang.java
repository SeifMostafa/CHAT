import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class AddLang extends JFrame {
	Stack<String> Added_langs;
	FileChooser chooser;
	private static final long serialVersionUID = 1L;
	public AddLang(FileChooser fileChooser){
		chooser = fileChooser;
		this.Added_langs=chooser.getAdded_langs();
		JPanel panel = new JPanel();
		String locales[] = Locale.getISOLanguages();
		JComboBox<String> dropdownlist = new JComboBox<String>();
		for (int i = 0; i < locales.length; i++) {
			dropdownlist.addItem(locales[i] + "," + new Locale(locales[i]).getDisplayName());
		}
		setLayout(new BorderLayout());
		Border border =	dropdownlist.getBorder();
		Border margin = new EmptyBorder(10,10,10,10);
		dropdownlist.setBorder(new CompoundBorder(border, margin));
		
		add(dropdownlist,BorderLayout.CENTER);
		JButton OK_btn = new JButton("OK");
		JButton Cancel_btn = new JButton("Cancel");
		
		 border =	panel.getBorder();
		 margin = new EmptyBorder(100,100,100,100);
		panel.setBorder(new CompoundBorder(border, margin));
		
		panel.setLayout(new GridLayout(1, 2));
		panel.add(OK_btn);
		panel.add(Cancel_btn);
		panel.setName("Pick Language");
		OK_btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkifalreadyadded(locales[dropdownlist.getSelectedIndex()])){
					Utils.writeStringToFile(locales[dropdownlist.getSelectedIndex()], "Config");
					final Thread updater = new Thread() {
					    /* (non-Javadoc)
					     * @see java.lang.Thread#run()
					     */
					    @Override
					    public void run() {
					        for (int i = 0; i < 10; i++) {
					        	Added_langs.push(locales[dropdownlist.getSelectedIndex()]);
								chooser.setAdded_langs(Added_langs);
								chooser.validate();
								chooser.repaint();					            try {
					                Thread.sleep(1000);
					            } catch (InterruptedException e) {
					                throw new RuntimeException(e);
					            }
					        }
					    }
					};
					updater.start();
				}
				removeAll();
				dispose();
			}
		});
		Cancel_btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAll();
				dispose();
			}
		});
		setBounds((int)Utils.width/2,(int)Utils.height/2,500,500);
		add(panel,BorderLayout.SOUTH);
		pack();
		setTitle("Pick language");
		setVisible(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	private boolean checkifalreadyadded(String string) {
		if(Added_langs.contains(string)) return true;
		else return false;
	}
}
