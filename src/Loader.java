import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.imageio.ImageIO;

import model.Direction;
import model.Word;

// SeShat editor has:
/*
 * txtfile contain characters, words to generate words and audio files from.
 * Images for each word in txtfile
 */
public class Loader {
	private String ImagesOutputPATH = "/SF/IF/";
	private String FVOutputPATH = "/SF/FV/";
	public static final String AppenddedToOutputFVfile = "_fv.txt";
	public static final String AppenddedToOutputImagefile = "_img";
	private String SpeechOutputPATH = "/SF/AF/";
	private String words_db_txtfilepath = "";
	private String chars_db_txtfilepath = "";
	private String OUTPUTPATH = "";

	public WordsGenerator generator;

	public Loader() {
		// read and assign
		Stack<String> configs = new Stack<>();
		configs = SeShatEditorMain.utils.readfileintoStack(SeShatEditorMain.utils.SHAREDPREF);
		if (configs.size() > 2) {
			words_db_txtfilepath = configs.pop().replace(SeShatEditorMain.utils.DBWORDSFILEKEY, "");
			chars_db_txtfilepath = configs.pop().replace(SeShatEditorMain.utils.CHARFILEKEY, "");
			SeShatEditorMain.utils.Lang =
					SeShatEditorMain.utils.readfileintoStack(SeShatEditorMain.utils.CONFIG).get(Integer.parseInt(configs.pop().replace(SeShatEditorMain.utils.LANGFILEKEY, "")));

		} else if (!configs.isEmpty()) {
			chars_db_txtfilepath = configs.pop().replace(SeShatEditorMain.utils.CHARFILEKEY, "");
			SeShatEditorMain.utils.Lang =
					SeShatEditorMain.utils.readfileintoStack(SeShatEditorMain.utils.CONFIG).get(Integer.parseInt(configs.pop().replace(SeShatEditorMain.utils.LANGFILEKEY, "")));

		} else {
		}
		assignFolderPathsInsideSyllabusFolder(new File(chars_db_txtfilepath).getParent());
	}

	/*
	 * Audio folder and ImagesFolder have data txtfile have words Syllabus is
	 * the output folder which this will create folder named by user name and
	 * contains data required for player Syllabus will contain Basic group of
	 * words and it's data
	 */
	public void GenerateSyllabus(WordsGenerator g) {
		this.generator = g;
		Stack<Word> words = generator.generate(SpeechOutputPATH, ImagesOutputPATH, words_db_txtfilepath);
		System.out.println("GenerateSyllabus" + words.size());
		String mainfolder = OUTPUTPATH + SeShatEditorMain.utils.SlashIndicator + generator.getPerson().getName();
		String ResultWordsFile = mainfolder + SeShatEditorMain.utils.SlashIndicator + SeShatEditorMain.utils.WordsOutputFileName;
		String ResultPhrasesFile = mainfolder + SeShatEditorMain.utils.SlashIndicator + SeShatEditorMain.utils.PhrasesOutputFileName;

		SeShatEditorMain.utils.createdir(mainfolder);
		SeShatEditorMain.utils.createfile(ResultWordsFile);
		SeShatEditorMain.utils.createfile(ResultPhrasesFile);

		for (Word w : words) {
			try {
				/*
				 * copyFileUsingFileStreams(new File(w.getImageFilePath()), new
				 * File(mainfolder + SlashIndicator + new
				 * File(w.getImageFilePath()).getName()));
				 */
				SeShatEditorMain.utils.copyFileUsingFileStreams(new File(w.getSpeechFilePath()),
						new File(mainfolder + SeShatEditorMain.utils.SlashIndicator + new File(w.getSpeechFilePath()).getName()));
				SeShatEditorMain.utils.writeStringToFile(w.getText(), ResultWordsFile);
				SeShatEditorMain.utils.writeStringToFile(w.getPhrase(), ResultPhrasesFile);

			} catch (Exception e) {
				System.out.println(e.toString() + "        from:GenerateSyllabus");
			}
		}
	}

	public Map<Character, model.Character> loadIn() {
		Map<Character, model.Character> characters = new HashMap<>();

		if (SeShatEditorMain.utils.state != State.DBWORDSLOADED) {

			/// if already done
			SeShatEditorMain.utils.UpdateStateInConfigFile(State.CHARSPAINTED);

		} else {
			loadwords();
			/*String filebackuppath = new File(chars_db_txtfilepath).getName();
			chars_db_txtfilepath = filebackuppath;
			filebackuppath = new File(words_db_txtfilepath).getName();
			words_db_txtfilepath = filebackuppath;*/
		}

		Stack<String> charsfromfile = SeShatEditorMain.utils.readfileintoStack(chars_db_txtfilepath);
		for (int i = 0; i < charsfromfile.size(); i++) {
			String ch = charsfromfile.get(i);
			if (i % 4 == 0) {
				model.Character character = new model.Character(ch.charAt(0));
				Character key_char = new Character(ch.charAt(0));
				Direction[][] directions = new Direction[4][];
				directions[0] = stringsToDirections(
						SeShatEditorMain.utils.readfileintoStack(FVOutputPATH + ch + "2" + AppenddedToOutputFVfile));
				directions[1] = stringsToDirections(
						SeShatEditorMain.utils.readfileintoStack(FVOutputPATH + charsfromfile.get(++i) + "1" + AppenddedToOutputFVfile));
				directions[2] = stringsToDirections(
						SeShatEditorMain.utils.readfileintoStack(FVOutputPATH + charsfromfile.get(++i) + "0" + AppenddedToOutputFVfile));

				directions[3] = stringsToDirections(
						SeShatEditorMain.utils.readfileintoStack(FVOutputPATH + charsfromfile.get(++i) + "3" + AppenddedToOutputFVfile));
				character.setFV(directions);
				character.setSpeechesFilePath(getspeechfilesforchar(ch.charAt(0)));
				character.setImagesFilePath(getImagesfilesforchar(ch.charAt(0)));
				characters.put(key_char, character);

			}
		}

		return characters;
	}

	private String[] getspeechfilesforchar(char charAt) {
		File folder = new File(SpeechOutputPATH + charAt);
		String[] filepaths;
		if (folder.isDirectory()) {
			File[] listOfFiles = folder.listFiles();
			filepaths = new String[listOfFiles.length];
			for (int i = 0; i < listOfFiles.length; i++) {
				filepaths[i] = listOfFiles[i].getAbsolutePath();
			}
		} else {
			filepaths = new String[1];
			filepaths[0] = folder.getAbsolutePath();
		}

		return filepaths;
	}

	private String[] getImagesfilesforchar(char charAt) {
		File folder = new File(ImagesOutputPATH + SeShatEditorMain.utils.SlashIndicator + charAt);
		String[] filepaths;

		if (folder.isDirectory()) {
			File[] listOfFiles = folder.listFiles();
			filepaths = new String[listOfFiles.length];
			for (int i = 0; i < listOfFiles.length; i++) {
				filepaths[i] = listOfFiles[i].getAbsolutePath();
			}
		} else {
			filepaths = new String[1];
			filepaths[0] = folder.getAbsolutePath();
		}
		return filepaths;
	}

	public static Direction[] stringsToDirections(Stack<String> strings) {
		Direction[] directios = new Direction[strings.size()];
		for (int i = 0; i < strings.size(); i++) {
			String s = strings.get(i);
			switch (s.charAt(0)) {

			case 'U':
				directios[i] = Direction.UP;
				break;
			case 'D':
				directios[i] = Direction.DOWN;
				break;
			case 'L':
				directios[i] = Direction.LEFT;
				break;
			case 'R':
				directios[i] = Direction.RIGHT;
				break;
			case 'I':
				directios[i] = Direction.INIT;
				break;
			case 'E':
				directios[i] = Direction.END;
				break;
			case 'S':
				directios[i] = Direction.SAME;
			}
		}
		return directios;
	}

	private void assignFolderPathsInsideSyllabusFolder(String SyllabusFolderPath) {
		if (OUTPUTPATH.equals("")) {

			OUTPUTPATH = SyllabusFolderPath;
			SpeechOutputPATH = SyllabusFolderPath + SpeechOutputPATH;
			ImagesOutputPATH = SyllabusFolderPath + ImagesOutputPATH;
			FVOutputPATH = SyllabusFolderPath + FVOutputPATH;

			SeShatEditorMain.utils.createdir(SpeechOutputPATH);
			SeShatEditorMain.utils.createdir(ImagesOutputPATH);
			SeShatEditorMain.utils.createdir(FVOutputPATH);
		}
	}

	public void loadchars() {

		Stack<String> words = SeShatEditorMain.utils.readfileintoStack(chars_db_txtfilepath);
		
		try {
			new PointsThread(words, this).run();
			new TextToImage((Stack<String>)words.clone(), ImagesOutputPATH).run();
			new TextToSpeech((Stack<String>)words.clone(), SpeechOutputPATH, SeShatEditorMain.utils.Lang).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void loadwords(){
		Stack<String> words = SeShatEditorMain.utils.readfileintoStack(words_db_txtfilepath);
		try {
			new ImagesThread(words, ImagesOutputPATH).run();
			new TextToSpeech(words, SpeechOutputPATH, "AR").run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeDirectionStackTofile(Stack<Direction> result_words, String filepath) {
		try {
			FileWriter writer = new FileWriter(FVOutputPATH + filepath + AppenddedToOutputFVfile);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);

			for (Direction s : result_words) {
				bufferedWriter.write(String.valueOf(s).charAt(0) + "\n");
			}
			bufferedWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static class TextToSpeech implements Runnable {

		private Stack<String> words;
		private String SpeechesPath;
		private String Lang;

		public TextToSpeech(Stack<String> words, String Speechespath, String language) {
			super();
			this.words = words;
			this.SpeechesPath = Speechespath;
			this.Lang = language;
		}

		@Override
		public void run() {
			// call function TTS
			for (String w : words) {
				SeShatEditorMain.utils.DoTTS(this.SpeechesPath, w, Lang);
			}
		}
	}

	static class TextToImage implements Runnable {

		private Stack<String> words;
		private String ImagesPath;

		public TextToImage(Stack<String> words, String Imagespath) {
			super();
			this.words = words;
			this.ImagesPath = Imagespath;
		}

		@Override
		public void run() {
			for (String w : words) {
				DoTTI(this.ImagesPath, w);
			}
		}
		
		/*
		 * return text in image form, doing Text To Image and throw it into
		 * ImagesFolder(para1)
		 */
		private  BufferedImage DoTTI(String ImagesFolder, String text) {

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
	}

	static class PointsThread implements Runnable {
		private Stack<String> words;
		private Loader loader;

		public PointsThread(Stack<String> words, Loader l) {
			super();
			this.words = words;
			this.loader = l;
		}

		@Override
		public void run() {
			// get fv and trigger ponts
			new Painter(words, loader);
		}
	}


	static class ImagesThread implements Runnable {
		private Stack<String> words;
		private String  ImagesOutputPATH;

		public ImagesThread(Stack<String> words, String ImagesPath) {
			super();
			this.words = words;
			this.ImagesOutputPATH = ImagesPath;
		}

		@Override
		public void run() {
			if (SeShatEditorMain.utils.checkfileExist(ImagesOutputPATH)) {
				for (String word : words) {
					word = word.replaceAll(" ", "+");
					String folder = ImagesOutputPATH + word;
					if (!SeShatEditorMain.utils.checkfileExist(folder)) {
						// find image from webservice
						SeShatEditorMain.utils.executeCommand("./googliser.sh -p " + "\"" + word + "\""
								+ " -n 1 -u 25000 -l 1000 -f 0 --minimum-pixels vga --output " + folder);
						File[] listOfFiles = new File(folder).listFiles();
						try {
							SeShatEditorMain.utils.createfile(folder + AppenddedToOutputImagefile);
							SeShatEditorMain.utils.copyFileUsingFileStreams(listOfFiles[0],
									new File(folder + AppenddedToOutputImagefile));
						} catch (IOException e) {
							System.err.println(e.getMessage());
							e.printStackTrace();
						}

						if (new File(folder).isDirectory()) {
							new File(folder).delete();
						}
					}
				}
			} else {
				System.err.println("FindHelpImage:: Invalid parameters!");
			}
		}
	}

}
