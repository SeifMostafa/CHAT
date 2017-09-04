import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import model.Direction;
import model.Word;

// SeShat editor has:
/*
 * txtfile contain characters, words to generate words and audio files from.
 * Images for each word in txtfile
 */
public class Loader {
	public WordsGenerator generator;
	 
	public Loader() {
		// read and assign
		Stack<String> configs = new Stack<>();
		configs = Utils.readfileintoStack(Utils.SHAREDPREF);
		Utils.words_db_txtfilepath = configs.pop().replace(Utils.CHARFILEKEY, "");
		assignFolderPathsInsideSyllabusFolder(new File(Utils.words_db_txtfilepath).getParent()); 
	}

	public Loader(String filepath){
		// write and assign
		Stack<String> configs = new Stack<>();
		configs.push(Utils.CHARFILEKEY + filepath);
		Utils.writeStackTofile(configs, Utils.SHAREDPREF);
		assignFolderPathsInsideSyllabusFolder(new File(filepath).getParent());
	}

	/*
	 * Audio folder and ImagesFolder have data txtfile have words Syllabus is
	 * the output folder which this will create folder named by user name and
	 * contains data required for player Syllabus will contain Basic group of
	 * words and it's data
	 */
	public void GenerateSyllabus(WordsGenerator g) {
		this.generator = g;
		generator.generate(Utils.SpeechOutputPATH, Utils.ImagesOutputPATH, Utils.words_db_txtfilepath);
	}

	public Loader getInctanse() {
		return this;
	}

	// write syllabus
	public void loadout() throws Exception {
		String mainfolder = Utils.OUTPUTPATH + Utils.SlashIndicator + generator.getPerson().getName();
		String ResultWordsFile = mainfolder + Utils.SlashIndicator + Utils.WordsOutputFileName;
		String ResultPhrasesFile = mainfolder + Utils.SlashIndicator + Utils.PhrasesOutputFileName;
		Utils.createdir(mainfolder);
		Utils.createfile(ResultWordsFile);
		Utils.createfile(ResultPhrasesFile);

		List<Direction> fv_list;
		Stack<Direction> fv_stack = new Stack<Direction>();
		ArrayList<Point> tr_list;

		/*
		 * create 4 folders and 2 file file contains words texts file contains
		 * phrases 4 folders: triggerpoints,fvs,speeches and images
		 */

		for (Word w : generator.getSyllabusWords()) {
			Utils.copyFileUsingFileStreams(new File(w.getImageFilePath()),
					new File(mainfolder + Utils.SlashIndicator + new File(w.getImageFilePath()).getName()));
			Utils.copyFileUsingFileStreams(new File(w.getSpeechFilePath()),
					new File(mainfolder + Utils.SlashIndicator + new File(w.getSpeechFilePath()).getName()));
			fv_list = Arrays.asList(w.getFV());
			fv_stack.addAll(fv_list);
			Utils.writeDirectionStackTofile(fv_stack,
					mainfolder + Utils.SlashIndicator + w.getText() + Utils.AppenddedToOutputFVfile);
			tr_list = new ArrayList<>(Arrays.asList(w.getTriggerpoints()));
			Utils.writePointsStackTofile(tr_list,
					mainfolder + Utils.SlashIndicator + w.getText() + Utils.AppenddedToOutputTriggerPointsfile);
			Utils.writeStringToFile(w.getText(), ResultWordsFile);
			Utils.writeStringToFile(w.getPhrase(), ResultPhrasesFile);

		}
	}

	public Map<Character, model.Character> loadChars() {
		Map<Character, model.Character> characters = new HashMap<>();
		Stack<String> charsfromfile = Utils.readfileintoStack(Utils.words_db_txtfilepath);

		for (String ch : charsfromfile) {
			model.Character character = new model.Character(ch.charAt(0));
			Character key_char = new Character(ch.charAt(0));
			character.setFV(stringsToDirections(
					Utils.readfileintoStack(Utils.FVOutputPATH + Utils.SlashIndicator + ch + Utils.AppenddedToOutputFVfile)));
			character.setSpeechesFilePath(getspeechfilesforchar(ch.charAt(0)));
			character.setImagesFilePath(getImagesfilesforchar(ch.charAt(0)));
			character.setTiggerPoints(gettriggerpointsforchar(ch.charAt(ch.charAt(0))));
			characters.put(key_char, character);
		}
		Utils.UpdateStateInConfigFile(State.CHARSPAINTED);
		
		return characters;
	}

	private Point[] gettriggerpointsforchar(char charAt) {
		Stack<String> points_string = Utils
				.readfileintoStack(Utils.TriggerPointsOutputPATH + Utils.SlashIndicator + charAt+Utils.AppenddedToOutputTriggerPointsfile);
		Point[] points = new Point[points_string.size()];

		for (int i = 0; i < points_string.size(); i++) {
			String[] p_x_y = points_string.get(i).split(",");
			points[i] = new Point(Integer.parseInt(p_x_y[0]), Integer.parseInt(p_x_y[1]));
		}
		return points;
	}

	private String[] getspeechfilesforchar(char charAt) {
		File folder = new File(Utils.SpeechOutputPATH + Utils.SlashIndicator + charAt);
		File[] listOfFiles = folder.listFiles();
		String[] filepaths = new String[listOfFiles.length];
		for (int i = 0; i < listOfFiles.length; i++) {
			filepaths[i] = listOfFiles[i].getAbsolutePath();
		}
		return filepaths;
	}

	private String[] getImagesfilesforchar(char charAt) {
		File folder = new File(Utils.ImagesOutputPATH + Utils.SlashIndicator + charAt);
		String[] filepaths = new String[2];
		filepaths[0] = folder + Utils.SlashIndicator + charAt;
		filepaths[1] = folder + Utils.SlashIndicator + charAt + 'ـ';
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
			}
		}
		return directios;
	}

	private void assignFolderPathsInsideSyllabusFolder(String SyllabusFolderPath) {
		Utils.SpeechOutputPATH = SyllabusFolderPath + Utils.SpeechOutputPATH;
		Utils.ImagesOutputPATH = SyllabusFolderPath + Utils.ImagesOutputPATH;
		Utils.FVOutputPATH = SyllabusFolderPath + Utils.FVOutputPATH;
		Utils.TriggerPointsOutputPATH = SyllabusFolderPath + Utils.TriggerPointsOutputPATH;

	}

}
