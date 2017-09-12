import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import model.Direction;

/*
 * Hello,This class is responsible about initialize configuration for Desktop program cross platform(Win,Linux and Mac)
 * it has functions such as:getTodaysDate, writeStringToFile, readFileintoString, writeStackTofile,readfileintoStack
 */
enum State {
	CHARSNOTLOADED, CHARSLOADED, CHARSPAINTED, DBWORDSLOADED
};

public class Utils {

	private String OSNAME = "";

	public static String ImagesOutputPATH = "/SF/IF/";
	public static String FVOutputPATH = "/SF/FV/";
	public static String TriggerPointsOutputPATH = "/SF/TPF/";
	public static String SpeechOutputPATH = "/SF/AF/";
	public static String words_db_txtfilepath = "";
	public static String chars_db_txtfilepath = "";
	public static String OUTPUTPATH = "";
	public static String SlashIndicator = "/";
	public static String Lang = "AR";
	public static final String SHAREDPREF = "sharedpref";
	public static final String CONFIG = "Config";
	public static final String BasicWordsFileName = "basicwords.txt";
	public static final String PhrasesInputFile = "phrases";
	public static final String CHARFILEKEY = "chars:";
	public static final String DBWORDSFILEKEY = "dbwords:";
	public static final String LANGFILEKEY = "lang:";
	public static final String WordsOutputFileName = "WORDS.txt";
	public static final String PhrasesOutputFileName = "PHRASES.txt";

	public static final String AppenddedToOutputFVfile = "_fv.txt";
	public static final String AppenddedToOutputTriggerPointsfile = "_trpoints.txt";

	public static final String CharLangWindowTitle = "Please, Choose the file contains language charcters";
	public static final String DbWordsWindowTitle = "Please, Choose file contains language words";
	public static final String CharCustWindowTitle = "Customize language characters";
	public static final String FONTNAME = "KFGQPC Alphabet Dotted";
	public static State state = State.CHARSNOTLOADED;
	public static double width, height;

	/*
	 * Get today's date from OS
	 */
	public static String getTodaysDate() {

		final Calendar c = Calendar.getInstance();
		int todaysDate = (c.get(Calendar.YEAR) * 10000) + ((c.get(Calendar.MONTH) + 1) * 100)
				+ (c.get(Calendar.DAY_OF_MONTH));
		//System.out.println(String.valueOf(todaysDate));
		return (String.valueOf(todaysDate));
	}

	public static String DoTTS(String AudioFoler, String word, String lang) {
		if(checkfileExist(AudioFoler)&&word.length()>0&&lang.length()==2){
		word = word.replaceAll(" ", "+");
		// filepath = audio folder
		String file = AudioFoler + word;
		if (!Utils.checkfileExist(file)) {
			String DownloadSpeechFile_cmd = "wget -q -U Mozilla -O " + AudioFoler + Utils.SlashIndicator + word
					+ " http://translate.google.com/translate_tts?ie=UTF-8&total=1&idx=0&textlen=32&client=tw-ob&q="
					+ word + "&tl=" + lang;
			Utils.executeCommand(DownloadSpeechFile_cmd);
			// exe cmd to create it as .wav
			String Mp3ToSpecificWav_command = "ffmpeg -i " + file + " -ar 16000 -ac 1 " + file + " -y";
			Utils.executeCommand(Mp3ToSpecificWav_command);
		}
		return file;
		}else{
			System.err.println("Invalid parameters!");
			return null;
		}
	}

	/*
	 * need web service to download from
	 */
	public String FindHelpImage(String ImagesFolder, String word) {
		word = word.replaceAll(" ", "+");
		// file = audio folder
		String file = ImagesFolder + word + ".png";
		if (!Utils.checkfileExist(file)) {
			// find image from webservice
		}
		return file;
	}

	/*
	 * write String into file and \n at the end
	 */
	public static void writeStringToFile(String data, String filepath) {
		try {
			FileWriter writer = new FileWriter(filepath, true);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);

			bufferedWriter.write(data + "\n");
			bufferedWriter.close();

		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * read file into string and the end = \n and return this string
	 */
	public static String readFileintoString(String filepath) {

		String ret = "";

		try {
			FileReader reader = new FileReader(filepath);
			BufferedReader bufferedReader = new BufferedReader(reader);

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				ret += line;
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/*
	 * write stack into file first para is stack to be written sec para is
	 * filepath of the file to save the content of stack in
	 */
	public static void writeStackTofile(Stack<String> result_words, String filepath) {
		try {
			FileWriter writer = new FileWriter(filepath, false);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);

			for (String s : result_words) {
				bufferedWriter.write(s + "\n");
			}
			bufferedWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * read the content of the file into stack by endline return stack
	 */
	public static Stack<String> readfileintoStack(String filepath) {
		Stack<String> words = new Stack<>();
		try {
			FileReader reader = new FileReader(filepath);
			BufferedReader bufferedReader = new BufferedReader(reader);

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				words.push(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return words;
	}

	public static Stack<String> readfileintoStack(String filepath, int afterlines) {
		Stack<String> words = new Stack<>();
		try {
			try (Stream<String> lines = Files.lines(Paths.get(filepath))) {
				Iterator<String> iterator = lines.skip(afterlines).iterator();
				while (iterator.hasNext()) {
					words.add(iterator.next());
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return words;
	}

	public static Map<Character, Stack<String>> readfileintoMap(String filepath, int afterlines) {
		Map<Character, Stack<String>> result = new HashMap<>();
		try {
			try (Stream<String> lines = Files.lines(Paths.get(filepath))) {
				Iterator<String> iterator = lines.skip(afterlines).iterator();
				while (iterator.hasNext()) {
					String current = iterator.next();
					if (result.containsKey(new Character(current.charAt(0)))) {
						Stack<String> currentstack = result.get(new Character(current.charAt(0)));
						currentstack.push(current);
						result.remove(new Character(current.charAt(0)));
						result.put(new Character(current.charAt(0)), currentstack);
					}else{
						Stack<String> currentstack = new Stack<>();
						currentstack.push(current);
						result.put(new Character(current.charAt(0)), currentstack);
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}
	public static Map<String,String> readfileintoMap(String filepath){
		Map<String, String> result = new HashMap<>();
		try{
			FileReader reader = new FileReader(filepath);
			BufferedReader bufferedReader = new BufferedReader(reader);

			String line = bufferedReader.readLine();
			String[] k_v= line.split(",");
			result.put(k_v[0], k_v[1]);
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	public static int CharToASCII(final char character) {
		return (int) character;
	}
	public static char CodeToChar(final int code){
		return (char) code;
	}

	/*
	 * remove non ar, symbols words and print them- ar words- into file 
	 * line by line
	 */
	public static void cleanwordsfile(String wordsfilepath) {
		Stack<String> ret = new Stack<>();
		try {
			FileReader reader = new FileReader(wordsfilepath);
			BufferedReader bufferedReader = new BufferedReader(reader);

			String line;
			while ((line = bufferedReader.readLine()) != null) {

				line = line.replaceAll("[!-~]", "");
				for(int i=1610;i<1620;i++){
					line = line.replaceAll(""+CodeToChar(i), "");
				}
				String words[] = line.split(" ");
				for (String word : words) {
					if (!line.equals(""))
						ret.push(word);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writeStackTofile(ret, wordsfilepath);
	}

	/*
	 * filename is the absolute filepath to the file to count lines inside. this
	 * function uses java8 feature to skip lines without reading lines and skip
	 * it uses Stream (stateful lambda) to skip lines (afterlines) THIS FUNCTION
	 * NOT TO COUNT ALL FILE LINES, IT HAS LIMIT (MaxNumofWordsEachLoad), and
	 * flag (biggerThan10K) as runtime says.
	 */
	public static int countLines(String filename, int afterlines) throws IOException {

		InputStream is = new BufferedInputStream(new FileInputStream(filename));

		try {
			int count = 0;
			boolean empty = true;
			try (Stream<String> lines = Files.lines(Paths.get(filename))) {
				Iterator<String> iterator = lines.skip(afterlines).iterator();
				while (iterator.hasNext()) {
					count++;
				}
				return (count == 0 && !empty) ? 1 : count;
			}
		} finally {
			is.close();
		}
		/*
		 * if not java 8, can use the following code but notice some delay.
		 * LineNumberReader lnr = new LineNumberReader(new FileReader(new
		 * File(filename))); lnr.skip(Long.MAX_VALUE); int result=
		 * lnr.getLineNumber() + 1;//Add 1 because line index starts at 0
		 * lnr.close(); return result;
		 */

	}

	/*
	 * it can be used to listen to enter btn from keyboard.
	 */
	public static KeyListener enter = new KeyAdapter() {
		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() == KeyEvent.VK_ENTER) {
				((JButton) e.getComponent()).doClick();
			}
		}
	};

	/*
	 * to work on cross-platform , MWS has to know what is the filepath schema
	 * should it be obaied read the OSName from OS itself
	 */
	private void setOSName() {
		this.OSNAME = System.getProperty("os.name");
		if (OSNAME.charAt(0) == 'W' || OSNAME.charAt(0) == 'w') {
			Utils.SlashIndicator = "\\";
		} else if (OSNAME.charAt(0) == 'W' || OSNAME.charAt(0) == 'w') {
			Utils.SlashIndicator = "/";
		} else {
			// Mac
			Utils.SlashIndicator = "/";
		}
	}

	/*
	 * it's used to get current path to be used to save files to make sure that
	 * is path is exist be easy for user to navi to output files
	 */
	public static String getCurrentPath() throws IOException {
		String path = new File(".").getCanonicalPath();
		return path;
	}

	/*
	 * get current screen width and height to be dynamically usable for all
	 * screens
	 */
	private void SetScreenWidthHeight() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		DisplayMode dm = gs[0].getDisplayMode();
		width = dm.getWidth();
		height = dm.getHeight();
	}

	/*
	 * after loading words and user selecting process, the selected words should
	 * be saved it saved in filepath_output beside the selected file contain the
	 * words
	 */
	public static void createfile(String filepath) {
		new File(filepath);
	}

	public static void createdir(String dirpath) {
		new File(dirpath).mkdirs();
	}

	/*
	 * main function to load program prefs and OS prefs check prefs and run
	 * program
	 */
	public void init() {
		setOSName();
		SetScreenWidthHeight();
		Utils.state = InterpretToState(Integer.parseInt(Utils.readfileintoStack("Config").get(0)));
	}

	private State InterpretToState(int parseInt) {
		switch (parseInt) {
		case 0:
			return State.CHARSLOADED;
		case 1:
			return State.CHARSNOTLOADED;
		case 2:
			return State.CHARSPAINTED;
		case 3:
			return State.DBWORDSLOADED;
		}
		return null;
	}

	private static int InterpretState(State s) {
		switch (s) {
		case CHARSLOADED:
			return 0;
		case CHARSNOTLOADED:
			return 1;
		case CHARSPAINTED:
			return 2;
		case DBWORDSLOADED:
			return 3;
		default:
			break;
		}
		return 0;
	}

	public static Stack<String> read1MfromfileintoStack(String filepath, int afterlines) {
		Stack<String> data = new Stack<>();
		try {
			try (Stream<String> lines = Files.lines(Paths.get(filepath))) {
				Iterator<String> iterator = lines.skip(afterlines).iterator();
				for (int i = 0; i < 850562; i++) {
					if (iterator.hasNext()) {
						data.push(iterator.next());
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return data;
	}

	public static void writeDirectionStackTofile(Stack<Direction> result_words, String filepath) {
		try {
			FileWriter writer = new FileWriter(filepath, false);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);

			for (Direction s : result_words) {
				bufferedWriter.write(s + "\n");
			}
			bufferedWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writePointsStackTofile(ArrayList<Point> result_words, String filepath) {
		try {
			FileWriter writer = new FileWriter(filepath, false);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);

			for (Point p : result_words) {
				bufferedWriter.write(p.x + "," + p.y + "\n");
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void copyFileUsingFileStreams(File source, File dest) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}

	public static String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}

	public static <T> T[] concatenate(T[] a, T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	public static boolean checkfileExist(String filepath) {
		File f = new File(filepath);
		if (f.exists()) {
			return true;
		} else
			return false;
	}

	public static Direction[] ComparePointsToCheckFV(double x1, double y1, double x2, double y2) {
		Direction direction[] = new Direction[2];
		if (x1 > x2)
			direction[0] = Direction.RIGHT;
		else if (x1 < x2)
			direction[0] = Direction.LEFT;
		else
			direction[0] = null;

		if (y1 > y2)
			direction[1] = Direction.DOWN;
		else if (y1 < y2)
			direction[1] = Direction.UP;
		else
			direction[1] = null;
		return direction;
	}

	/*
	 * return text in image form, doing Text To Image and throw it into ImagesFolder(para1)
	 */
	public static BufferedImage DoTTI(String ImagesFolder, String text) {

		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D g2d = img.createGraphics();
		Font font = new Font("Level One Logica", Font.PLAIN, 512);
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();
		int height = fm.getHeight();
		int width = fm.stringWidth(text);
		img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setFont(font);

		fm = g2d.getFontMetrics();
		g2d.drawString(text, 0, fm.getAscent());
		g2d.dispose();
		img = invertImage(img);

		try {
			File outputFile = new File(ImagesFolder + text + ".png");
			ImageIO.write(img, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;

	}

	public static BufferedImage invertImage(BufferedImage imageName) {

		for (int x = 0; x < imageName.getWidth(); x++) {
			for (int y = 0; y < imageName.getHeight(); y++) {
				int rgba = imageName.getRGB(x, y);
				Color col = new Color(rgba, true);
				col = new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue());
				imageName.setRGB(x, y, col.getRGB());
			}
		}
		return imageName;
	}

	public static void UpdateStateInConfigFile(State s) {
		Utils.state = s;
		Stack<String> config_content = Utils.readfileintoStack(CONFIG);
		config_content.remove(0);
		config_content.add(0, "" + InterpretState(s));
		Utils.writeStackTofile(config_content, CONFIG);
	}

}
