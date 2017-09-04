import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Stack;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

enum REASON {
	LANG_CHARS, DB_WORDS
};

public class FileChooser extends JFrame {

	private static final long serialVersionUID = 1L;
	private Stack<String> Added_langs;

	public JTextField filepath;
	private JButton choose, ok, AddLangBtn;
	private JPanel Langs;

	private JPanel AddLangugesPanel;
	private JPanel MainPanel_1;

	public FileChooser(REASON R, String title) {
		setBounds(100, 100, (int) Utils.width / 4, (int) Utils.height / 4);
		setLayout(new BorderLayout());
		setTitle(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		switch (R) {
		case LANG_CHARS:
			initialize_MainPanel();
			initialize_langPanel();
			break;
		case DB_WORDS:
			initialize_MainPanel();
			break;
		}
		pack();
		setVisible(true);
	}

	private void initialize_MainPanel() {
		String filenameJTextFieldInfo = "file contains language charcters";
		filepath = new JTextField();
		choose = new JButton("Choose");
		ok = new JButton("OK");
		filepath.setText(filenameJTextFieldInfo);
		choose.addActionListener(new ChooseL());
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (filepath.getText().equals(filenameJTextFieldInfo)) {
					JOptionPane.showMessageDialog(new JFrame(), "Sorry! You didn't choose any file!", "Warning",
							JOptionPane.WARNING_MESSAGE);
				} else {
					try {
						JOptionPane.showMessageDialog(new JFrame(), "Thanks!", "Info", JOptionPane.INFORMATION_MESSAGE);
						String BackupFile = new File(filepath.getText()).getName();
						Utils.createfile(BackupFile);
						Utils.copyFileUsingFileStreams(new File(filepath.getText()), new File(BackupFile));
						Utils.writeStringToFile(Utils.CHARFILEKEY+filepath.getText(), Utils.SHAREDPREF);
						Utils.UpdateStateInConfigFile(State.CHARSLOADED);
						SeShatEditorMain.LangCharsChoosingFile_Pressed(filepath.getText());
					} catch (IOException e1) {
						System.out.println("E:" + e1.toString());
					}
					
				}
				close();
			}
		});

		filepath.setEditable(false);

		MainPanel_1 = new JPanel();
		Border border = MainPanel_1.getBorder();
		Border margin = new EmptyBorder(20, 20, 20, 20);
		Border borderline = new BevelBorder(BevelBorder.RAISED);
		MainPanel_1.setBorder(new CompoundBorder( margin,borderline));

		border = ok.getBorder();
		margin = new EmptyBorder(10, 10, 10, 10);
		ok.setBorder(new CompoundBorder(border, margin));
		ok.setPreferredSize(new Dimension(20, 20));

		border = choose.getBorder();
		margin = new EmptyBorder(10, 10, 10, 10);
		choose.setBorder(new CompoundBorder(border, margin));
		choose.setPreferredSize(new Dimension(20, 20));

		MainPanel_1.setName("file paths");

		MainPanel_1.setLayout(new BorderLayout());
		MainPanel_1.add(filepath, BorderLayout.NORTH);
		MainPanel_1.add(ok, BorderLayout.SOUTH);
		MainPanel_1.add(choose, BorderLayout.EAST);

		add(MainPanel_1, BorderLayout.NORTH);
	}

	public void initialize_langPanel() {

		this.Added_langs = Utils.readfileintoStack("Config", 1);
		AddLangugesPanel = new JPanel();
		AddLangugesPanel.setName("Add Language");
		AddLangugesPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		AddLangugesPanel.setLayout(new GridLayout(1, 1));

		AddLangBtn = new JButton("Add Language");
		AddLangBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new AddLang(getInctanse());
			}
		});
		Langs = new JPanel();
		ButtonGroup group = new ButtonGroup();
		for (String added_lang : Added_langs) {
			JRadioButtonMenuItem newMenuItem = new JRadioButtonMenuItem(new Locale(added_lang).getDisplayName());
			newMenuItem.setSelected(true);
			group.add(newMenuItem);
			Langs.add(newMenuItem);
		}
		AddLangugesPanel.add(AddLangBtn);
		add(Langs, BorderLayout.CENTER);
		Border border = Langs.getBorder();
		Border margin = new EmptyBorder(100, 100, 100, 100);
		Langs.setBorder(new CompoundBorder(border, margin));

		add(AddLangugesPanel, BorderLayout.SOUTH);
	}

	private void close() {
		this.dispose();
	}

	class ChooseL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser c = new JFileChooser();
			int rVal = c.showOpenDialog(FileChooser.this);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				filepath.setText(c.getSelectedFile().getAbsolutePath());
			}
		}
	}

	public Stack<String> getAdded_langs() {
		return Added_langs;
	}

	public void setAdded_langs(Stack<String> added_langs) {
		Added_langs = added_langs;
	}

	public FileChooser getInctanse() {
		return this;
	}
}
