import java.awt.Dimension;
import java.util.Map;
import java.util.Stack;

import model.Person;

public class SeShatEditorMain {
	/*
	 * config file contains: 1st line: STATE desc the program state as
	 * following: 1: no choose load for language and characters file 0: language
	 * and character file are chosen but customization of language is not been
	 * passed yet 2: wait db_words 3: let's go and get persons to make syllabus
	 */
	static int phaseindex = 1;
	static Map<Character, model.Character> characters = null;
	public static void main(String[] args) {
		new Utils().init();
		switch (Utils.state) {
		case CHARSNOTLOADED:
			new FileChooser(REASON.LANG_CHARS, Utils.CharLangWindowTitle).setSize(new Dimension(500, 300));
			break;
		case CHARSLOADED:
			Loader loader = new Loader();
			Stack<String> words = Utils.readfileintoStack(Utils.chars_db_txtfilepath);
			try {
				new PointsThread(words).run();
				new TextToImage(words, Utils.ImagesOutputPATH).run();
				new TextToSpeech(words, Utils.SpeechOutputPATH, "AR").run();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case CHARSPAINTED:
			loader = new Loader();
			if (characters == null)
				characters = loader.loadIn();
			new FileChooser(REASON.DB_WORDS, Utils.DbWordsWindowTitle).setSize(new Dimension(500, 100));
			break;
		case DBWORDSLOADED:
			loader = new Loader();
			if (characters == null)
				characters = loader.loadIn();
			new GUITakePersonInfo();
			break;
		default:
			break;
		}
	}

	public static void GenerateSyllabus_Pressed(Person p) {
		Loader loader = new Loader();
		if (characters == null)
			characters = loader.loadIn();

		loader.GenerateSyllabus(new WordsGenerator(Utils.Lang,phaseindex, p));
		try {
			loader.loadout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void LangCharsFinishingPaint_FV_TR__Pressed() {
		Loader loader = new Loader();
		if (characters == null)
			characters = loader.loadIn();
		new FileChooser(REASON.DB_WORDS, Utils.DbWordsWindowTitle).setSize(new Dimension(500, 100));
	}

	public static void LangCharsChoosingFile_Pressed() {
		Loader loader = new Loader();
		if (characters == null)
			characters = loader.loadIn();
		Stack<String> words = Utils.readfileintoStack(Utils.chars_db_txtfilepath);

		try {
			new PointsThread(words).run();
			new TextToImage(words, Utils.ImagesOutputPATH).run();
			new TextToSpeech(words, Utils.SpeechOutputPATH, "AR").run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void LangWordsChoosingFile_Pressed() {
		Loader loader = new Loader();
		if (characters == null)
			characters = loader.loadIn();
		new GUITakePersonInfo();
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
				Utils.DoTTS(this.SpeechesPath, w, Lang);
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
			// call function TTI
			for (String w : words) {
				Utils.DoTTI(this.ImagesPath, w);
			}
		}
	}

	static class PointsThread implements Runnable {
		private Stack<String> words;

		public PointsThread(Stack<String> words) {
			super();
			this.words = words;
		}

		@Override
		public void run() {
			// get fv and trigger ponts
			new Painter(words);
		}
	}
}