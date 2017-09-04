import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import model.Person;

public class GUITakePersonInfo {

	private JFrame frame;
	private JTextField nameText;
	private JLabel genderLabel;
	private JRadioButton male;
	private JRadioButton female;
	private JLabel ageLabel;
	private JTextField ageText;
	private JLabel addressLabel;
	private JTextField addressText;
	private JLabel birthAddressLabel;
	private JTextField birthAddressText;
	private JLabel envLabel;
	private JComboBox<String> envCombo;
	private JLabel lblNewLabel;
	private JComboBox<String> orgCombo;
	private JLabel label;
	private JComboBox<String> jobCombo;
	private JLabel favNamesLabel;
	private JTextPane favNamePane;
	private JLabel whyLabel;
	private JButton btnGenerate;
	private ArrayList<String> favNames = new ArrayList<>();
	private ArrayList<String> whyMsgs = new ArrayList<>();
	private JList whyList;
	private JScrollPane scrollPane;



	private static void writeToXml(Person p)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		String filepath = "data.xml";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = docBuilder.parse(filepath);

		// // Get the root element
		// Node persons = doc.getFirstChild();

		// Get the element by tag name directly
		Node person = doc.getElementsByTagName("person").item(0);

		// // update staff attribute
		// NamedNodeMap attr = person.getAttributes();
		// loop the staff child node
		NodeList list = person.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {

			Node node = list.item(i);

			// get the element, and update the value
			if ("name".equals(node.getNodeName())) {
				node.setTextContent(p.getName());
			} else if ("age".equals(node.getNodeName())) {
				node.setTextContent(String.valueOf(p.getAge()));
			} else if ("address".equals(node.getNodeName())) {
				node.setTextContent(p.getAddress());
			} else if ("job".equals(node.getNodeName())) {
				node.setTextContent(p.getJob());
			} else if ("favNames".equals(node.getNodeName())) {
				node.setTextContent(p.getFavNames().toString());
			} else if ("whyLearn".equals(node.getNodeName())) {
				node.setTextContent(p.getWhyLearn().toString());
			} else if ("gender".equals(node.getNodeName())) {
				node.setTextContent(p.getGender());
			} else if ("birthAddress".equals(node.getNodeName())) {
				node.setTextContent(p.getBirthAddress());
			} else if ("envLocation".equals(node.getNodeName())) {
				node.setTextContent(p.getEnvLocation());
			} else if ("orgHome".equals(node.getNodeName())) {
				node.setTextContent(p.getOrgHome());
			}

		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(filepath));
		transformer.transform(source, result);

		System.out.println("Saved To XML");
	}

	/**
	 * Create the application.
	 */
	public GUITakePersonInfo	() {
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
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
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
		JLabel nameLabel = new JLabel("الأسم :");
		nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		nameLabel.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.gridwidth = 2;
		gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameLabel.gridx = 3;
		gbc_nameLabel.gridy = 0;
		frame.getContentPane().add(nameLabel, gbc_nameLabel);

		male = new JRadioButton("ذكر");
		GridBagConstraints gbc_male = new GridBagConstraints();
		gbc_male.anchor = GridBagConstraints.EAST;
		gbc_male.fill = GridBagConstraints.VERTICAL;
		gbc_male.insets = new Insets(0, 0, 5, 5);
		gbc_male.gridx = 1;
		gbc_male.gridy = 1;
		frame.getContentPane().add(male, gbc_male);

		female = new JRadioButton("انثى");
		GridBagConstraints gbc_female = new GridBagConstraints();
		gbc_female.anchor = GridBagConstraints.EAST;
		gbc_female.fill = GridBagConstraints.VERTICAL;
		gbc_female.insets = new Insets(0, 0, 5, 5);
		gbc_female.gridx = 2;
		gbc_female.gridy = 1;
		frame.getContentPane().add(female, gbc_female);

		genderLabel = new JLabel("الجنس :");
		GridBagConstraints gbc_genderLabel = new GridBagConstraints();
		gbc_genderLabel.gridwidth = 2;
		gbc_genderLabel.insets = new Insets(0, 0, 5, 5);
		gbc_genderLabel.gridx = 3;
		gbc_genderLabel.gridy = 1;
		frame.getContentPane().add(genderLabel, gbc_genderLabel);

		ageText = new JTextField();
		GridBagConstraints gbc_ageText = new GridBagConstraints();
		gbc_ageText.insets = new Insets(0, 0, 5, 5);
		gbc_ageText.fill = GridBagConstraints.HORIZONTAL;
		gbc_ageText.gridx = 2;
		gbc_ageText.gridy = 2;
		frame.getContentPane().add(ageText, gbc_ageText);
		ageText.setColumns(10);

		ageLabel = new JLabel("العمر :");
		ageLabel.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_ageLabel = new GridBagConstraints();
		gbc_ageLabel.gridwidth = 2;
		gbc_ageLabel.insets = new Insets(0, 0, 5, 5);
		gbc_ageLabel.gridx = 3;
		gbc_ageLabel.gridy = 2;
		frame.getContentPane().add(ageLabel, gbc_ageLabel);

		addressText = new JTextField();
		GridBagConstraints gbc_addressText = new GridBagConstraints();
		gbc_addressText.insets = new Insets(0, 0, 5, 5);
		gbc_addressText.fill = GridBagConstraints.HORIZONTAL;
		gbc_addressText.gridx = 2;
		gbc_addressText.gridy = 3;
		frame.getContentPane().add(addressText, gbc_addressText);
		addressText.setColumns(10);

		addressLabel = new JLabel("العنوان :");
		GridBagConstraints gbc_addressLabel = new GridBagConstraints();
		gbc_addressLabel.gridwidth = 2;
		gbc_addressLabel.insets = new Insets(0, 0, 5, 5);
		gbc_addressLabel.gridx = 3;
		gbc_addressLabel.gridy = 3;
		frame.getContentPane().add(addressLabel, gbc_addressLabel);

		birthAddressText = new JTextField();
		GridBagConstraints gbc_birthAdressText = new GridBagConstraints();
		gbc_birthAdressText.insets = new Insets(0, 0, 5, 5);
		gbc_birthAdressText.fill = GridBagConstraints.HORIZONTAL;
		gbc_birthAdressText.gridx = 2;
		gbc_birthAdressText.gridy = 4;
		frame.getContentPane().add(birthAddressText, gbc_birthAdressText);
		birthAddressText.setColumns(10);

		birthAddressLabel = new JLabel("محل الميلاد :");
		GridBagConstraints gbc_birthAddressLabel = new GridBagConstraints();
		gbc_birthAddressLabel.gridwidth = 2;
		gbc_birthAddressLabel.insets = new Insets(0, 0, 5, 5);
		gbc_birthAddressLabel.gridx = 3;
		gbc_birthAddressLabel.gridy = 4;
		frame.getContentPane().add(birthAddressLabel, gbc_birthAddressLabel);

		envCombo = new JComboBox<String>();
		GridBagConstraints gbc_envCombo = new GridBagConstraints();
		gbc_envCombo.insets = new Insets(0, 0, 5, 5);
		gbc_envCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_envCombo.gridx = 2;
		gbc_envCombo.gridy = 5;
		frame.setBounds(100, 100, 671, 453);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		envCombo.addItem("قروي");
		envCombo.addItem("حضري");
		envCombo.addItem("بدوي");
		envCombo.addItem("ساحلي");
		envCombo.addItem("نوبي");
		frame.getContentPane().add(envCombo, gbc_envCombo);

		envLabel = new JLabel("البيئه القادم منها :");
		GridBagConstraints gbc_envLabel = new GridBagConstraints();
		gbc_envLabel.gridwidth = 2;
		gbc_envLabel.insets = new Insets(0, 0, 5, 5);
		gbc_envLabel.gridx = 3;
		gbc_envLabel.gridy = 5;
		frame.getContentPane().add(envLabel, gbc_envLabel);

		orgCombo = new JComboBox<String>();
		GridBagConstraints gbc_orgCombo = new GridBagConstraints();
		gbc_orgCombo.insets = new Insets(0, 0, 5, 5);
		gbc_orgCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_orgCombo.gridx = 2;
		gbc_orgCombo.gridy = 6;
		orgCombo.addItem("مهاجر");
		orgCombo.addItem("حضري");
		orgCombo.addItem("صعيد");
		orgCombo.addItem("ساحلي");
		frame.getContentPane().add(orgCombo, gbc_orgCombo);

		lblNewLabel = new JLabel("الموطن الأصلي :");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 3;
		gbc_lblNewLabel.gridy = 6;
		frame.getContentPane().add(lblNewLabel, gbc_lblNewLabel);

		jobCombo = new JComboBox<String>();
		GridBagConstraints gbc_jobCombo = new GridBagConstraints();
		gbc_jobCombo.insets = new Insets(0, 0, 5, 5);
		gbc_jobCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_jobCombo.gridx = 2;
		gbc_jobCombo.gridy = 7;
		jobCombo.addItem("عامل صناعي");
		jobCombo.addItem("عامل زراعي");
		jobCombo.addItem("مكتبي");
		jobCombo.addItem("منزلي");
		jobCombo.addItem("حرفي");
		jobCombo.addItem("عمل حر");
		jobCombo.addItem("معاش");
		jobCombo.addItem("طفل");
		jobCombo.addItem("لا يعمل");
		frame.getContentPane().add(jobCombo, gbc_jobCombo);

		label = new JLabel("العمل :");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridwidth = 2;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 3;
		gbc_label.gridy = 7;
		frame.getContentPane().add(label, gbc_label);

		favNamePane = new JTextPane();
		GridBagConstraints gbc_favNames = new GridBagConstraints();
		gbc_favNames.insets = new Insets(0, 0, 5, 5);
		gbc_favNames.fill = GridBagConstraints.BOTH;
		gbc_favNames.gridx = 2;
		gbc_favNames.gridy = 8;
		frame.getContentPane().add(favNamePane, gbc_favNames);

		favNamesLabel = new JLabel("الأشخاص :");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.gridwidth = 2;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 3;
		gbc_label_1.gridy = 8;
		frame.getContentPane().add(favNamesLabel, gbc_label_1);

		whyLabel = new JLabel("لماذا يريد تعلم القراءه و الكتابه ؟");
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
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
		String why[] = { "سخريه و خداع", "العمل", "التقدم في شئ ما", "تكملة التعليم", "التكنولوجيا", "مساعدة  أولاده",
				"التعامل مع البيئه المحيطه", "السفر", "علم الأديان السماويه" };

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

		btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Person p = new Person();

				if (orgCombo.getSelectedItem().toString().equals("") || jobCombo.getSelectedItem().toString().equals("")
						|| favNamePane.getText().equals("") || whyList.isSelectionEmpty()
						|| nameText.getText().equals("") || addressText.getText().equals("")
						|| ageText.getText().equals("") || birthAddressText.getText().equals("")
						|| envCombo.getSelectedItem().toString().equals("")) {
					JOptionPane.showMessageDialog(null, "أملأ الفراغات من فضلك");

				} else {

					favNames.add(favNamePane.getText() + " ");

					p.setName(nameText.getText());
					p.setAddress(addressText.getText());
					p.setAge(Integer.parseInt(ageText.getText()));
					p.setBirthAddress(birthAddressText.getText());
					p.setEnvLocation(envCombo.getSelectedItem().toString());
					p.setFavNames(favNames);

					if (male.isSelected())
						p.setGender(male.getText());
					else if (female.isSelected())
						p.setGender(female.getText());

					p.setJob(jobCombo.getSelectedItem().toString());
					p.setWhyLearn(whyMsgs);
					p.setOrgHome(orgCombo.getSelectedItem().toString());

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
					SeShatEditorMain.LangCharsFinishingPaint_FV_TR__Pressed(p);
				}
					
			}
		});
		GridBagConstraints gbc_btnGenerate = new GridBagConstraints();
		gbc_btnGenerate.insets = new Insets(0, 0, 0, 5);
		gbc_btnGenerate.gridx = 2;
		gbc_btnGenerate.gridy = 10;
		frame.getContentPane().add(btnGenerate, gbc_btnGenerate);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void clear(){
		nameText.setText("");
		male.setSelected(false);
		female.setSelected(false);
		ageText.setText("");
		favNamePane.setText("");
		addressText.setText("");
		birthAddressText.setText("");
		whyList.setSelectionMode(0);
		JOptionPane.showMessageDialog(null, "لقد تم اضافة المتقدم");
	}
}
