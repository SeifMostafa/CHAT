import java.awt.Dimension;
import java.util.Locale;
import java.util.Map;

import model.Direction;
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
	static Utils utils;
	public static void main(String[] args) {
		utils = Utils.getInstance();
		
		switch (utils.state) {
		case CHARSNOTLOADED:
			new FileChooser(REASON.LANG_CHARS, utils.CharLangWindowTitle).setSize(new Dimension(500, 300));
			break;
		case CHARSLOADED:
			Loader loader = new Loader();
			loader.loadchars();
			
			break;
		case CHARSPAINTED:
			loader =  new Loader();
			if (characters == null)
				characters = loader.loadIn();
			new FileChooser(REASON.DB_WORDS, utils.DbWordsWindowTitle).setSize(new Dimension(500, 100));
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
		if (characters.equals(null))
			characters = loader.loadIn();
		new Thread(new Runnable() {
			@Override
			public void run() {
				loader.GenerateSyllabus(new WordsGenerator("AR", phaseindex, p));
			}
		}).start();
	}

	public static void LangCharsFinishingPaint_FV_TR__Pressed() {
		Loader loader = new Loader();
		if (characters == null)
			characters = loader.loadIn();
		new FileChooser(REASON.DB_WORDS, utils.DbWordsWindowTitle).setSize(new Dimension(500, 100));
	}

	public static void LangCharsChoosingFile_Pressed() {
		Loader loader = new Loader();
		loader.loadchars();
	}

	public static void LangWordsChoosingFile_Pressed() {
		Loader loader = new Loader();
		if (characters == null)
			characters = loader.loadIn();
		new GUITakePersonInfo();
	}


}