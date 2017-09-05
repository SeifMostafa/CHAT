import java.util.Stack;

import model.Character;
import model.Direction;
import model.Person;
import model.Word;

public class WordsGenerator {
	/*
	 * Words: Basic (everyone should know / ready- just copy no need to
	 * generate) Personal/Environment (related to user's info - to be generated
	 * custom) Advanced (level 3, combination between Personal and Basic to
	 * generate custom level of words)
	 */
	private int NUMBER_OF_WORDS_EACH_PHASE = 300;

	private Stack<Word> SyllabusWords;
	public int PhaseIndex;
	private Person person;
	private int NumberOfRequiredWords;
	
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Stack<Word> getSyllabusWords() {
		return SyllabusWords;
	}

	public void setSyllabusWords(Stack<Word> syllabusWords) {
		SyllabusWords = syllabusWords;
	}

	public WordsGenerator(int phaseIndex, Person p) {
		super();
		PhaseIndex = phaseIndex;
		NumberOfRequiredWords = NUMBER_OF_WORDS_EACH_PHASE * PhaseIndex;
		this.person = p;

	}

	public void generate(String AudiosFolderPath, String ImagesFolderPath, String txtfilepath) {

		SyllabusWords = new Stack<>();

		int NumberOfWordsToBeGeneratedAsAdvanced = NumberOfRequiredWords - SyllabusWords.size();
		Stack<String> advanced_words = GenerateWordsTree(txtfilepath, NumberOfWordsToBeGeneratedAsAdvanced);

		for (String txt_to_word : advanced_words) {
			SyllabusWords.push(new Word(txt_to_word));
		}

		for (Word word : SyllabusWords) {
			// check if exist and create audio files
			System.out.println(word.getText());
			word.setSpeechFilePath(DoTTS(word.getText(), AudiosFolderPath));
			// check if exist and create imagepath
//			word.setImageFilePath(FindHelpImage(word.getText(), ImagesFolderPath));
//			// check if exist and create fv
//			word.setFV(GenerateWordFV(word.getText()));
		}

	}

	private String[] GenerateWord_Characters(String word) {
		String[] word_Characters = word.split("");
		return word_Characters;
	}

	private Stack<String> GenerateWordsTree(String txtfilepath, int number_of_words) {
		int levels = 0;
		int added = 0;
		Stack<String> AvailableWords = new Stack<>();
		// read AvailableWords read from txtfile
		AvailableWords = Utils.readfileintoStack(txtfilepath);
		Stack<String> tree = new Stack<>();
		tree.add(this.person.getName());
		added++;
		while (number_of_words > tree.size()) {
			for (int i = 0; i < added; i++) {
				added = 0;
				String[] Word_characters = GenerateWord_Characters(tree.get((i) + levels));
				for (String CH : Word_characters) {
					for (String searchword : AvailableWords) {
						if (searchword.substring(0, 1).equals(CH)) {
							tree.add(searchword);
							added++;
							AvailableWords.remove(searchword);
							break;
						}
					}
				}
			}
			levels++;
		}
		return tree;
	}

	/*
	 * after generate words syllabus Audio and Images and Mats should be ready
	 * to transfer to player
	 */

	public Direction[] GenerateWordFV(String word) {
		Direction[] word_directions = new Direction[0];

		for (int i = 0; i < word.length(); i++) {
			if (i == 0) {
				word_directions = Utils.concatenate(word_directions, getCharacterFV(word.charAt(i), 0));
			} else if (i == word.length() - 1) {
				word_directions = Utils.concatenate(word_directions, getCharacterFV(word.charAt(i), 2));
			} else {
				word_directions = Utils.concatenate(word_directions, getCharacterFV(word.charAt(i), 1));
			}
		}
		return word_directions;
	}

	// pass char and get from db or model
	private Direction[] getCharacterFV(char c, int index) {

		model.Character character = (model.Character) SeShatEditorMain.characters.get(new Character(c));
		if (character != null) {
			switch (index) {
			case 0:
				return character.getFVwithoutEND(); // remove last
			case 2:
				return character.getFVwithoutINIT(); // remove first
			default:
				return character.getFVwithoutEND_withoutINIT(); // remove last // and first
			}
		} else {
			return null;
		}
	}

	private String DoTTS(String AudioFoler, String word) {
		word = word.replaceAll(" ", "+");
		// filepath = audio folder
		String file = AudioFoler + Utils.SlashIndicator + word + ".wav";
		if (!Utils.checkfileExist(file)) {
			String DownloadSpeechFile_cmd = "wget -q -U Mozilla -O " + AudioFoler + Utils.SlashIndicator + word
					+ ".wav http://translate.google.com/translate_tts?ie=UTF-8&total=1&idx=0&textlen=32&client=tw-ob&q="
					+ word + "&tl=AR";
			Utils.executeCommand(DownloadSpeechFile_cmd);
			// exe cmd to create it as .wav
			String Mp3ToSpecificWav_command = "ffmpeg -i " + file + " -ar 16000 -ac 1 " + file + " -y";
			Utils.executeCommand(Mp3ToSpecificWav_command);
		}
		return file;
	}

	private String FindHelpImage(String ImagesFolder, String word) {
		word = word.replaceAll(" ", "+");
		// file = audio folder
		String file = ImagesFolder + word + ".png";
		if (!Utils.checkfileExist(file)) {
			// find image from webservice
		}
		return file;
	}

}
