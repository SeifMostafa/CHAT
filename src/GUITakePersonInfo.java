
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import model.Person;

public class GUITakePersonInfo {

	private JFrame frame;
	private JTextField nameText;
	private JLabel genderLabel;
	private JLabel ageLabel;
	private JTextField ageText;
	private JLabel addressLabel;
	private JTextField addressText;
	private JLabel birthAddressLabel;
	private JTextField birthAddressText;
	private JLabel envLabel;
	private JComboBox<String> envCombo;
	private JLabel orgHomeLabel;
	private JComboBox<String> orgCombo;
	private JLabel workLabel;
	private JComboBox<String> jobCombo;
	private JLabel favNamesLabel;
	private JTextPane favNamePane;
	private JLabel whyLabel;
	private JButton btnGenerate;
	private ArrayList<String> favNames = new ArrayList<>();
	private ArrayList<String> whyMsgs = new ArrayList<>();
	private JList whyList;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JRadioButton maleRadio;
	private JRadioButton femaleRadio;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem language;
	private JMenuItem exit;

	private static void writeToXml(Person p)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		String filepath = Messages.getString("LastPersonFile");
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = docBuilder.parse(filepath);

		// // Get the root element
		// Node persons = doc.getFirstChild();

		// Get the element by tag name directly
		Node person = doc.getElementsByTagName(Messages.getString("PersonElement")).item(0);

		// // update staff attribute
		// NamedNodeMap attr = person.getAttributes();
		// loop the staff child node
		NodeList list = person.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {

			Node node = list.item(i);

			// get the element, and update the value
			if (Messages.getString("NameTag").equals(node.getNodeName())) {
				node.setTextContent(p.getName());
			} else if (Messages.getString("AgeTag").equals(node.getNodeName())) {
				node.setTextContent(String.valueOf(p.getAge()));
			} else if (Messages.getString("AddressTag").equals(node.getNodeName())) {
				node.setTextContent(p.getAddress());
			} else if (Messages.getString("JopTag").equals(node.getNodeName())) {
				node.setTextContent(p.getJob());
			} else if (Messages.getString("FavNamesTag").equals(node.getNodeName())) {
				node.setTextContent(p.getFavNames().toString());
			} else if (Messages.getString("WhyLearnTag").equals(node.getNodeName())) {
				node.setTextContent(p.getWhyLearn().toString());
			} else if (Messages.getString("GenderTag").equals(node.getNodeName())) {
				node.setTextContent(p.getGender());
			} else if (Messages.getString("BirthAddressTag").equals(node.getNodeName())) {
				node.setTextContent(p.getBirthAddress());
			} else if (Messages.getString("EnvLocationTag").equals(node.getNodeName())) {
				node.setTextContent(p.getEnvLocation());
			} else if (Messages.getString("orgHomeTag").equals(node.getNodeName())) {
				node.setTextContent(p.getOrgHome());
			}

		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(filepath));
		transformer.transform(source, result);

		System.out.println(Messages.getString("SaveMessage"));
	}

	public void xmlappend(Person person)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(Messages.getString("PersonsFile"));
		Element root = document.getDocumentElement();

		Collection<Person> persons = new ArrayList<Person>();
		persons.add(person);

		for (Person p : persons) {
			// server elements
			Element newPerson = document.createElement(Messages.getString("PersonElement"));

			Element name = document.createElement(Messages.getString("NameTag"));
			name.appendChild(document.createTextNode(p.getName()));
			newPerson.appendChild(name);

			Element age = document.createElement(Messages.getString("AgeTag"));
			age.appendChild(document.createTextNode(Integer.toString(p.getAge())));
			newPerson.appendChild(age);

			Element address = document.createElement(Messages.getString("AddressTag"));
			address.appendChild(document.createTextNode(p.getAddress()));
			newPerson.appendChild(address);

			Element jop = document.createElement(Messages.getString("JopTag"));
			jop.appendChild(document.createTextNode(p.getJob()));
			newPerson.appendChild(jop);

			Element favNames = document.createElement(Messages.getString("FavNamesTag"));
			favNames.appendChild(document.createTextNode(p.getFavNames().toString()));
			newPerson.appendChild(favNames);

			Element whyLearn = document.createElement(Messages.getString("WhyLearnTag"));
			whyLearn.appendChild(document.createTextNode(p.getWhyLearn().toString()));
			newPerson.appendChild(whyLearn);

			Element gender = document.createElement(Messages.getString("GenderTag"));
			gender.appendChild(document.createTextNode(p.getGender()));
			newPerson.appendChild(gender);

			Element birthAddress = document.createElement("birthAddress");
			birthAddress.appendChild(document.createTextNode(p.getBirthAddress()));
			newPerson.appendChild(birthAddress);

			Element envLocation = document.createElement(Messages.getString("EnvLocationTag"));
			envLocation.appendChild(document.createTextNode(p.getEnvLocation()));
			newPerson.appendChild(envLocation);

			Element orgName = document.createElement("orgHome");
			orgName.appendChild(document.createTextNode(p.getOrgHome()));
			newPerson.appendChild(orgName);

			root.appendChild(newPerson);
		}

		DOMSource source = new DOMSource(document);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StreamResult result = new StreamResult(Messages.getString("PersonsFile"));
		transformer.transform(source, result);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					GUITakePersonInfo window = new GUITakePersonInfo();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUITakePersonInfo() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// MainFrame
		frame = new JFrame();
		frame.getContentPane().setEnabled(false);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 104, 83, 86, 0, 46, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
				Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);
		// Name Texteditor
		nameText = new JTextField();
		GridBagConstraints gbc_nameText = new GridBagConstraints();
		gbc_nameText.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameText.insets = new Insets(0, 0, 5, 5);
		gbc_nameText.gridx = 2;
		gbc_nameText.gridy = 0;
		frame.getContentPane().add(nameText, gbc_nameText);
		nameText.setColumns(10);
		// Name Label
		JLabel nameLabel = new JLabel(Messages.getString("NameLabel"));
		nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		nameLabel.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.anchor = GridBagConstraints.WEST;
		gbc_nameLabel.gridwidth = 2;
		gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameLabel.gridx = 3;
		gbc_nameLabel.gridy = 0;
		frame.getContentPane().add(nameLabel, gbc_nameLabel);

		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.EAST;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 1;
		frame.getContentPane().add(panel, gbc_panel);

		femaleRadio = new JRadioButton(Messages.getString("GUITakePersonInfo.radioButton_1.text"));
		femaleRadio.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(femaleRadio);

		maleRadio = new JRadioButton(Messages.getString("GUITakePersonInfo.radioButton.text"));
		maleRadio.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(maleRadio);

		genderLabel = new JLabel(Messages.getString("GenderLabel"));
		GridBagConstraints gbc_genderLabel = new GridBagConstraints();
		gbc_genderLabel.anchor = GridBagConstraints.WEST;
		gbc_genderLabel.gridwidth = 2;
		gbc_genderLabel.insets = new Insets(0, 0, 5, 5);
		gbc_genderLabel.gridx = 3;
		gbc_genderLabel.gridy = 1;
		frame.getContentPane().add(genderLabel, gbc_genderLabel);

		ageText = new JTextField();
		GridBagConstraints gbc_ageText = new GridBagConstraints();
		gbc_ageText.fill = GridBagConstraints.HORIZONTAL;
		gbc_ageText.insets = new Insets(0, 0, 5, 5);
		gbc_ageText.gridx = 2;
		gbc_ageText.gridy = 2;
		frame.getContentPane().add(ageText, gbc_ageText);
		ageText.setColumns(10);

		ageLabel = new JLabel(Messages.getString("AgeLabel"));
		ageLabel.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_ageLabel = new GridBagConstraints();
		gbc_ageLabel.anchor = GridBagConstraints.WEST;
		gbc_ageLabel.gridwidth = 2;
		gbc_ageLabel.insets = new Insets(0, 0, 5, 5);
		gbc_ageLabel.gridx = 3;
		gbc_ageLabel.gridy = 2;
		frame.getContentPane().add(ageLabel, gbc_ageLabel);

		addressText = new JTextField();
		GridBagConstraints gbc_addressText = new GridBagConstraints();
		gbc_addressText.fill = GridBagConstraints.HORIZONTAL;
		gbc_addressText.insets = new Insets(0, 0, 5, 5);
		gbc_addressText.gridx = 2;
		gbc_addressText.gridy = 3;
		frame.getContentPane().add(addressText, gbc_addressText);
		addressText.setColumns(10);

		addressLabel = new JLabel(Messages.getString("AddressLabel"));
		GridBagConstraints gbc_addressLabel = new GridBagConstraints();
		gbc_addressLabel.anchor = GridBagConstraints.WEST;
		gbc_addressLabel.gridwidth = 2;
		gbc_addressLabel.insets = new Insets(0, 0, 5, 5);
		gbc_addressLabel.gridx = 3;
		gbc_addressLabel.gridy = 3;
		frame.getContentPane().add(addressLabel, gbc_addressLabel);

		birthAddressText = new JTextField();
		GridBagConstraints gbc_birthAdressText = new GridBagConstraints();
		gbc_birthAdressText.fill = GridBagConstraints.HORIZONTAL;
		gbc_birthAdressText.insets = new Insets(0, 0, 5, 5);
		gbc_birthAdressText.gridx = 2;
		gbc_birthAdressText.gridy = 4;
		frame.getContentPane().add(birthAddressText, gbc_birthAdressText);
		birthAddressText.setColumns(10);

		birthAddressLabel = new JLabel(Messages.getString("BirthAddressLabel"));
		GridBagConstraints gbc_birthAddressLabel = new GridBagConstraints();
		gbc_birthAddressLabel.anchor = GridBagConstraints.WEST;
		gbc_birthAddressLabel.gridwidth = 2;
		gbc_birthAddressLabel.insets = new Insets(0, 0, 5, 5);
		gbc_birthAddressLabel.gridx = 3;
		gbc_birthAddressLabel.gridy = 4;
		frame.getContentPane().add(birthAddressLabel, gbc_birthAddressLabel);

		envCombo = new JComboBox<String>();
		GridBagConstraints gbc_envCombo = new GridBagConstraints();
		gbc_envCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_envCombo.insets = new Insets(0, 0, 5, 5);
		gbc_envCombo.gridx = 2;
		gbc_envCombo.gridy = 5;
		frame.setBounds(100, 100, 671, 453);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String environments[] = { "Ù‚Ø±ÙˆÙŠ", "Ø­Ø¶Ø±ÙŠ", "Ø¨Ø¯ÙˆÙŠ", "Ø³Ø§Ø­Ù„ÙŠ", "Ù†ÙˆØ¨ÙŠ" };
		for (int i = 0; i < environments.length; i++) {
			envCombo.addItem(environments[i]);
		}

		frame.getContentPane().add(envCombo, gbc_envCombo);

		envLabel = new JLabel(Messages.getString("EnvLabel"));
		GridBagConstraints gbc_envLabel = new GridBagConstraints();
		gbc_envLabel.anchor = GridBagConstraints.WEST;
		gbc_envLabel.gridwidth = 2;
		gbc_envLabel.insets = new Insets(0, 0, 5, 5);
		gbc_envLabel.gridx = 3;
		gbc_envLabel.gridy = 5;
		frame.getContentPane().add(envLabel, gbc_envLabel);

		orgCombo = new JComboBox<String>();
		GridBagConstraints gbc_orgCombo = new GridBagConstraints();
		gbc_orgCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_orgCombo.insets = new Insets(0, 0, 5, 5);
		gbc_orgCombo.gridx = 2;
		gbc_orgCombo.gridy = 6;

		String homes[] = { "Ù…Ù‡Ø§Ø¬Ø±", "Ø­Ø¶Ø±ÙŠ", "ØµØ¹ÙŠØ¯", "Ø³Ø§Ø­Ù„ÙŠ" };
		for (int i = 0; i < homes.length; i++) {
			orgCombo.addItem(homes[i]);
		}

		frame.getContentPane().add(orgCombo, gbc_orgCombo);

		orgHomeLabel = new JLabel(Messages.getString("OrgHomeLabel"));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 3;
		gbc_lblNewLabel.gridy = 6;
		frame.getContentPane().add(orgHomeLabel, gbc_lblNewLabel);

		jobCombo = new JComboBox<String>();
		GridBagConstraints gbc_jobCombo = new GridBagConstraints();
		gbc_jobCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_jobCombo.insets = new Insets(0, 0, 5, 5);
		gbc_jobCombo.gridx = 2;
		gbc_jobCombo.gridy = 7;
		String jobs[] = { "Ø¹Ø§Ù…Ù„ ØµÙ†Ø§Ø¹ÙŠ", "Ø¹Ø§Ù…Ù„ Ø²Ø±Ø§Ø¹ÙŠ", "Ù…ÙƒØªØ¨ÙŠ", "Ù…Ù†Ø²Ù„ÙŠ", "Ø­Ø±Ù�ÙŠ",
				"Ø¹Ù…Ù„ Ø­Ø±", "Ù…Ø¹Ø§Ø´", "Ø·Ù�Ù„", "Ù„Ø§ ÙŠØ¹Ù…Ù„" };
		for (int i = 0; i < jobs.length; i++) {
			jobCombo.addItem(jobs[i]);
		}
		frame.getContentPane().add(jobCombo, gbc_jobCombo);

		workLabel = new JLabel(Messages.getString("WorkLabel"));
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.WEST;
		gbc_label.gridwidth = 2;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 3;
		gbc_label.gridy = 7;
		frame.getContentPane().add(workLabel, gbc_label);

		favNamePane = new JTextPane();
		GridBagConstraints gbc_favNames = new GridBagConstraints();
		gbc_favNames.insets = new Insets(0, 0, 5, 5);
		gbc_favNames.fill = GridBagConstraints.BOTH;
		gbc_favNames.gridx = 2;
		gbc_favNames.gridy = 8;
		frame.getContentPane().add(favNamePane, gbc_favNames);

		favNamesLabel = new JLabel(Messages.getString("FavNamesLabel"));
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.WEST;
		gbc_label_1.gridwidth = 2;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 3;
		gbc_label_1.gridy = 8;
		frame.getContentPane().add(favNamesLabel, gbc_label_1);

		whyLabel = new JLabel(Messages.getString("WhyLabel"));
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.anchor = GridBagConstraints.WEST;
		gbc_label_2.gridwidth = 2;
		gbc_label_2.insets = new Insets(0, 0, 5, 5);
		gbc_label_2.gridx = 3;
		gbc_label_2.gridy = 9;

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 2;
		gbc_scrollPane.gridy = 9;

		frame.getContentPane().add(scrollPane, gbc_scrollPane);
		String why[] = { "Ø³Ø®Ø±ÙŠÙ‡ Ùˆ Ø®Ø¯Ø§Ø¹", "Ø§Ù„Ø¹Ù…Ù„", "Ø§Ù„ØªÙ‚Ø¯Ù… Ù�ÙŠ Ø´Ø¦ Ù…Ø§",
				"ØªÙƒÙ…Ù„Ø© Ø§Ù„ØªØ¹Ù„ÙŠÙ…", "Ø§Ù„ØªÙƒÙ†ÙˆÙ„ÙˆØ¬ÙŠØ§", "Ù…Ø³Ø§Ø¹Ø¯Ø©  Ø£ÙˆÙ„Ø§Ø¯Ù‡",
				"Ø§Ù„ØªØ¹Ø§Ù…Ù„ Ù…Ø¹ Ø§Ù„Ø¨ÙŠØ¦Ù‡ Ø§Ù„Ù…Ø­ÙŠØ·Ù‡", "Ø§Ù„Ø³Ù�Ø±",
				"Ø¹Ù„Ù… Ø§Ù„Ø£Ø¯ÙŠØ§Ù† Ø§Ù„Ø³Ù…Ø§ÙˆÙŠÙ‡" };

		whyList = new JList(why);

		scrollPane.setViewportView(whyList);

		whyList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					whyMsgs = (ArrayList<String>) whyList.getSelectedValuesList();
				}
			}
		});
		frame.getContentPane().add(whyLabel, gbc_label_2);

		btnGenerate = new JButton(Messages.getString("GenerateBtn"));
		btnGenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Person p = new Person();

				if (orgCombo.getSelectedItem().toString().equals("") || jobCombo.getSelectedItem().toString().equals("") //$NON-NLS-2$
						|| favNamePane.getText().equals("") || whyList.isSelectionEmpty()
						|| nameText.getText().equals("") || addressText.getText().equals("") //$NON-NLS-2$
						|| ageText.getText().equals("") || birthAddressText.getText().equals("") //$NON-NLS-2$
						|| envCombo.getSelectedItem().toString().equals("")) {
					JOptionPane.showMessageDialog(null, Messages.getString("NullException"));
				} else {
					try {
						p.setAge(Integer.parseInt(ageText.getText()));
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(null, Messages.getString("AgeException"));
						ageText.setText("");
						return;
					}
					favNames.add(favNamePane.getText() + " ");
					p.setName(nameText.getText());
					p.setAddress(addressText.getText());
					p.setBirthAddress(birthAddressText.getText());
					p.setEnvLocation(envCombo.getSelectedItem().toString());
					p.setFavNames(favNames);
					if (maleRadio.isSelected()) {
						femaleRadio.setSelected(false);
						p.setGender(maleRadio.getText());
					} else if (femaleRadio.isSelected()) {
						maleRadio.setSelected(false);
						p.setGender(femaleRadio.getText());
					}
					p.setJob(jobCombo.getSelectedItem().toString());
					p.setWhyLearn(whyMsgs);
					p.setOrgHome(orgCombo.getSelectedItem().toString());
					try {
						xmlappend(p);
					} catch (ParserConfigurationException e1) {
						e1.printStackTrace();
					} catch (SAXException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (TransformerException e1) {
						e1.printStackTrace();
					}
					try {
						writeToXml(p);
					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (TransformerException e) {
						e.printStackTrace();
					}
					clear();
					SeShatEditorMain.GenerateSyllabus_Pressed(p);
				}

			}
		});
		GridBagConstraints gbc_btnGenerate = new GridBagConstraints();
		gbc_btnGenerate.insets = new Insets(0, 0, 0, 5);
		gbc_btnGenerate.gridx = 2;
		gbc_btnGenerate.gridy = 10;
		frame.getContentPane().add(btnGenerate, gbc_btnGenerate);

		menuBar = new JMenuBar();

		mnFile = new JMenu(Messages.getString("GUITakePersonInfo.mnFile.text")); //$NON-NLS-1$
		mnFile.setMnemonic(KeyEvent.VK_F);

		exit = new JMenuItem(Messages.getString("GUITakePersonInfo.mntmExit.text")); //$NON-NLS-1$
		exit.setMnemonic(KeyEvent.VK_E);
		exit.setToolTipText("Exit application");
		exit.addActionListener((ActionEvent event) -> {
			System.exit(0);
		});

		// menuBar.setVisible(true);

		menuBar.add(mnFile);

		language = new JMenuItem("Add Language"); //$NON-NLS-1$
		language.setMnemonic(KeyEvent.VK_E);
		language.setToolTipText("language change");

		language.addActionListener((ActionEvent event) -> {
			AddLang addlang = new AddLang();
			
		});

		mnFile.add(language);

		mnFile.addSeparator();
		mnFile.add(exit);
		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);

		frame.setVisible(true);

	}

	public void clear() {
		nameText.setText("");
		maleRadio.setSelected(false);
		femaleRadio.setSelected(false);
		ageText.setText("");
		favNamePane.setText("");
		addressText.setText("");
		birthAddressText.setText("");
		whyList.setSelectionMode(0);
		JOptionPane.showMessageDialog(null, Messages.getString("SuccessMessage"));
	}
}