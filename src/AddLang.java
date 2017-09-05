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
import javax.swing.JRadioButtonMenuItem;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class AddLang extends JFrame {
	Stack<String> Added_langs;
	FileChooser chooser;
	private static final long serialVersionUID = 1L;

	public AddLang(FileChooser fileChooser) {
		chooser = fileChooser;
		this.Added_langs = chooser.getAdded_langs();
		JPanel panel = new JPanel();
		String locales[] = Locale.getISOLanguages();
		JComboBox<String> dropdownlist = new JComboBox<String>();
		for (int i = 0; i < locales.length; i++) {
			dropdownlist.addItem(locales[i] + "," + new Locale(locales[i]).getDisplayName());
		}
		setLayout(new BorderLayout());
		Border border = dropdownlist.getBorder();
		Border margin = new EmptyBorder(10, 10, 10, 10);
		dropdownlist.setBorder(new CompoundBorder(border, margin));

		add(dropdownlist, BorderLayout.CENTER);
		JButton OK_btn = new JButton("OK");
		JButton Cancel_btn = new JButton("Cancel");

		border = panel.getBorder();
		margin = new EmptyBorder(100, 100, 100, 100);
		panel.setBorder(new CompoundBorder(border, margin));

		panel.setLayout(new GridLayout(1, 2));
		panel.add(OK_btn);
		panel.add(Cancel_btn);
		OK_btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!checkifalreadyadded(locales[dropdownlist.getSelectedIndex()])) {
					Utils.writeStringToFile(locales[dropdownlist.getSelectedIndex()], Utils.CONFIG);
					Added_langs.push(locales[dropdownlist.getSelectedIndex()]);
					chooser.setAdded_langs(Added_langs);
	
						JRadioButtonMenuItem newMenuItem = new JRadioButtonMenuItem(new Locale(locales[dropdownlist.getSelectedIndex()]).getDisplayName());
						newMenuItem.setSelected(true);
						chooser.LangRadioBtnGroup.add(newMenuItem);
						
						chooser.Langs.add(newMenuItem);
						chooser.setVisible(false);
						chooser.revalidate();
						chooser.repaint();
						try{
							Thread.sleep(100);
						}catch(Exception e2){
							System.out.println(e2.toString());
						}
						chooser.setVisible(true);
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
		setBounds((int) Utils.width / 2, (int) Utils.height / 2, 500, 500);
		add(panel, BorderLayout.SOUTH);
		pack();
		setTitle("Pick language");
		setVisible(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private boolean checkifalreadyadded(String string) {
		if (Added_langs.contains(string))
			return true;
		else
			return false;
	}
}
