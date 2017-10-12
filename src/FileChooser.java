import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Stack;

import javax.swing.AbstractButton;
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
	private Stack<String> Added_langs= null;

	public JTextField filepath=null;
	private JButton choose, ok, AddLangBtn;
	public JPanel Langs;
	
	public ButtonGroup LangRadioBtnGroup;
	String filenameJTextFieldInfo;
	private JPanel AddLangugesPanel;
	private JPanel MainPanel_1;

	private REASON reason;

	public FileChooser(REASON R, String title) {
		this.reason = R;
		setBounds(100, 100, (int) SeShatEditorMain.utils.width / 4, (int) SeShatEditorMain.utils.height / 4);
		setLayout(new BorderLayout());
		setTitle(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		switch (R) {
		case LANG_CHARS:
			filenameJTextFieldInfo = Messages.getString("FileChooser.characters_file"); //$NON-NLS-1$

			initialize_MainPanel();
			initialize_langPanel();
			break;
		case DB_WORDS:
			filenameJTextFieldInfo = Messages.getString("FileChooser.words_file"); //$NON-NLS-1$
			initialize_MainPanel();
			break;
		}
		pack();
		setVisible(true);
	}

	private void initialize_MainPanel() {
		filepath = new JTextField();
		choose = new JButton(Messages.getString("FileChooser.btn_choose")); //$NON-NLS-1$
		ok = new JButton(Messages.getString("FileChooser.btn_ok")); //$NON-NLS-1$
		filepath.setText(filenameJTextFieldInfo);
		choose.addActionListener(new ChooseL());
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (filepath.getText().equals(filenameJTextFieldInfo)) {
					JOptionPane.showMessageDialog(new JFrame(), Messages.getString("FileChooser.nochoice"), Messages.getString("FileChooser.warning"), //$NON-NLS-1$ //$NON-NLS-2$
							JOptionPane.WARNING_MESSAGE);
				} else {
					try {
						JOptionPane.showMessageDialog(new JFrame(), Messages.getString("FileChooser.thanks"), Messages.getString("FileChooser.info"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
						String BackupFile = new File(new File("").getAbsolutePath()+SeShatEditorMain.utils.SlashIndicator+new File(filepath.getText()).getName()).getPath();
						SeShatEditorMain.utils.createfile(BackupFile);
						SeShatEditorMain.utils.copyFileUsingFileStreams(new File(filepath.getText()), new File(BackupFile));
						SeShatEditorMain.utils.cleanwordsfile(BackupFile);
						if (reason == REASON.LANG_CHARS) {
							SeShatEditorMain.utils.Lang = getSelectedButtonText();
							SeShatEditorMain.utils.writeStringToFile(SeShatEditorMain.utils.LANGFILEKEY+ SeShatEditorMain.utils.Lang ,SeShatEditorMain.utils.SHAREDPREF);
							SeShatEditorMain.utils.writeStringToFile(SeShatEditorMain.utils.CHARFILEKEY + BackupFile, SeShatEditorMain.utils.SHAREDPREF);
							SeShatEditorMain.utils.UpdateStateInConfigFile(State.CHARSLOADED);
							SeShatEditorMain.LangCharsChoosingFile_Pressed();
						}else {
							SeShatEditorMain.utils.writeStringToFile(SeShatEditorMain.utils.DBWORDSFILEKEY + BackupFile, SeShatEditorMain.utils.SHAREDPREF);
							SeShatEditorMain.utils.UpdateStateInConfigFile(State.DBWORDSLOADED);
							SeShatEditorMain.LangWordsChoosingFile_Pressed();
						}
					} catch (IOException e1) {
						System.out.println(Messages.getString( e1.toString())); //$NON-NLS-1$
					}
					close();
				}
			}
		});

		filepath.setEditable(false);

		MainPanel_1 = new JPanel();
		Border border = MainPanel_1.getBorder();
		Border margin = new EmptyBorder(20, 20, 20, 20);
		Border borderline = new BevelBorder(BevelBorder.RAISED);
		MainPanel_1.setBorder(new CompoundBorder(margin, borderline));

		border = ok.getBorder();
		margin = new EmptyBorder(10, 10, 10, 10);
		ok.setBorder(new CompoundBorder(border, margin));
		ok.setPreferredSize(new Dimension(20, 20));

		border = choose.getBorder();
		margin = new EmptyBorder(10, 10, 10, 10);
		choose.setBorder(new CompoundBorder(border, margin));
		choose.setPreferredSize(new Dimension(20, 20));

		MainPanel_1.setName(Messages.getString("FileChooser.filepaths")); //$NON-NLS-1$

		MainPanel_1.setLayout(new BorderLayout());
		MainPanel_1.add(filepath, BorderLayout.NORTH);
		MainPanel_1.add(ok, BorderLayout.SOUTH);
		MainPanel_1.add(choose, BorderLayout.EAST);

		add(MainPanel_1, BorderLayout.NORTH);
	}

	public void initialize_langPanel() {

		this.Added_langs = SeShatEditorMain.utils.readfileintoStack(SeShatEditorMain.utils.CONFIG, 1); //$NON-NLS-1$
		AddLangugesPanel = new JPanel();
		AddLangugesPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		AddLangugesPanel.setLayout(new GridLayout(1, 1));

		AddLangBtn = new JButton(Messages.getString("FileChooser.btn_addlang")); //$NON-NLS-1$
		AddLangBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new AddLang(getInctanse());
			}
		});
		Langs = new JPanel();
		LangRadioBtnGroup = new ButtonGroup();
		for (String added_lang : Added_langs) {
			JRadioButtonMenuItem newMenuItem = new JRadioButtonMenuItem(new Locale(added_lang).getDisplayName());
			newMenuItem.setSelected(true);
			LangRadioBtnGroup.add(newMenuItem);
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
	
    private String getSelectedButtonText() {
    	int i=0;
        for (Enumeration<AbstractButton> buttons = this.LangRadioBtnGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return SeShatEditorMain.utils.readfileintoStack(SeShatEditorMain.utils.CONFIG).elementAt(i++).toUpperCase(); // adding 1 for state line
            }
        }
        return null;
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
