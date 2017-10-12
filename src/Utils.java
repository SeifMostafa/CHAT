import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

import javax.swing.JButton;

/*
 * Hello,This class is responsible about initialize configuration for Desktop program cross platform(Win,Linux and Mac)
 * it has functions such as:getTodaysDate, writeStringToFile, readFileintoString, writeStackTofile,readfileintoStack
 */
enum State {
	CHARSNOTLOADED, CHARSLOADED, CHARSPAINTED, DBWORDSLOADED
};

public class Utils {

	private String OSNAME = "";
	private static Utils utils = new Utils();


	public String SlashIndicator = "/";
	public String Lang = "AR";
	public final String SHAREDPREF = "sharedpref";
	public final String CONFIG = "Config";
	public final String BasicWordsFileName = "basicwords.txt";
	public final String PhrasesInputFile = "phrases";
	public final String CHARFILEKEY = "chars:";
	public final String DBWORDSFILEKEY = "dbwords:";
	public final String LANGFILEKEY = "lang:";
	public final String WordsOutputFileName = "WORDS.txt";
	public final String PhrasesOutputFileName = "PHRASES.txt";



	public final String CharLangWindowTitle = "Please, Choose the file contains language charcters";
	public final String DbWordsWindowTitle = "Please, Choose file contains language words";
	public final String CharCustWindowTitle = "Customize language characters";
	public final String FONTNAME = "KFGQPC Alphabet Dotted";
	public State state = State.CHARSNOTLOADED;
	public double width, height;

	/*
	 * Get today's date from OS
	 */
	private Utils(){
		this.init();
	}
	public String getTodaysDate() {

		final Calendar c = Calendar.getInstance();
		int todaysDate = (c.get(Calendar.YEAR) * 10000) + ((c.get(Calendar.MONTH) + 1) * 100)
				+ (c.get(Calendar.DAY_OF_MONTH));
		// System.out.println(String.valueOf(todaysDate));
		return (String.valueOf(todaysDate));
	}

	public String DoTTS(String AudioFoler, String word, String lang) {
		if (checkfileExist(AudioFoler) && word.length() > 0 && lang.length() == 2) {
			word = word.replaceAll(" ", "+");
			// filepath = audio folder
			String file = AudioFoler + word;
			if (!checkfileExist(file)) {
				String DownloadSpeechFile_cmd = "wget -q -U Mozilla -O " + AudioFoler + SlashIndicator + word
						+ " http://translate.google.com/translate_tts?ie=UTF-8&total=1&idx=0&textlen=32&client=tw-ob&q="
						+ word + "&tl=" + lang;
				executeCommand(DownloadSpeechFile_cmd);
				// exe cmd to create it as .wav
				String Mp3ToSpecificWav_command = "ffmpeg -i " + file + " -ar 16000 -ac 1 " + file + " -y";
				executeCommand(Mp3ToSpecificWav_command);
			}
			return file;
		} else {
			System.err.println("DoTTS:: Invalid parameters!");
			System.err.println("DoTTS:: lang:: "+lang);
			System.err.println("DoTTS:: word:: "+word);
			System.err.println("DoTTS:: path:: "+AudioFoler);

			return null;
		}
	}



	/*
	 * write String into file and \n at the end
	 */
	public void writeStringToFile(String data, String filepath) {
		try {
			FileWriter writer = new FileWriter(filepath, true);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);

			bufferedWriter.write(data + "\n");
			bufferedWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * read file into string and the end = \n and return this string
	 */
	public String readFileintoString(String filepath) {

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
	public void writeStackTofile(Stack<String> result_words, String filepath) {
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
	public Stack<String> readfileintoStack(String filepath) {
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

	public Stack<String> readfileintoStack(String filepath, int afterlines) {
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

	public Map<Character, Stack<String>> readfileintoMap(String filepath, int afterlines) {
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
					} else {
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

	public Map<String, String> readfileintoMap(String filepath) {
		Map<String, String> result = new HashMap<>();
		try {
			FileReader reader = new FileReader(filepath);
			BufferedReader bufferedReader = new BufferedReader(reader);

			String line = bufferedReader.readLine();
			String[] k_v = line.split(",");
			result.put(k_v[0], k_v[1]);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int CharToASCII(final char character) {
		return (int) character;
	}

	public char CodeToChar(final int code) {
		return (char) code;
	}

	/*
	 * remove non ar, symbols words and print them- ar words- into file line by
	 * line
	 */
	public void cleanwordsfile(String wordsfilepath) {
		Stack<String> ret = new Stack<>();
		try {
			FileReader reader = new FileReader(wordsfilepath);
			BufferedReader bufferedReader = new BufferedReader(reader);

			String line;
			while ((line = bufferedReader.readLine()) != null) {

				line = line.replaceAll("[!-~]", "");
				for (int i = 1610; i < 1620; i++) {
					line = line.replaceAll("" + CodeToChar(i), "");
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
	public int countLines(String filename, int afterlines) throws IOException {

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
	public KeyListener enter = new KeyAdapter() {
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
			SlashIndicator = "\\";
		} else if (OSNAME.charAt(0) == 'W' || OSNAME.charAt(0) == 'w') {
			SlashIndicator = "/";
		} else {
			// Mac
			SlashIndicator = "/";
		}
	}

	/*
	 * it's used to get current path to be used to save files to make sure that
	 * is path is exist be easy for user to navi to output files
	 */
	public String getCurrentPath() throws IOException {
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
	public void createfile(String filepath) {
		new File(filepath);
	}

	public void createdir(String dirpath) {
		new File(dirpath).mkdirs();
	}

	/*
	 * main function to load program prefs and OS prefs check prefs and run
	 * program
	 */
	public void init() {
		setOSName();
		SetScreenWidthHeight();
		state = InterpretToState(Integer.parseInt(readfileintoStack("Config").get(0)));
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

	private int InterpretState(State s) {
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

	public Stack<String> read1MfromfileintoStack(String filepath, int afterlines) {
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

	public void copyFileUsingFileStreams(File source, File dest) throws IOException {
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

	public String executeCommand(String command) {

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

	public <T> T[] concatenate(T[] a, T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	public boolean checkfileExist(String filepath) {
		File f = new File(filepath);
		if (f.exists()) {
			return true;
		} else
			return false;
	}

	public void UpdateStateInConfigFile(State s) {
		state = s;
		Stack<String> config_content = readfileintoStack(CONFIG);
		config_content.remove(0);
		config_content.add(0, "" + InterpretState(s));
		writeStackTofile(config_content, CONFIG);
	}

	public static Utils getInstance() {
		return utils;
	}

}
