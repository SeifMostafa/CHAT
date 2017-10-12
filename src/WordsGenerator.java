import java.util.Map;
import java.util.Stack;

import model.Character.CharPosition;
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
	private String lang;

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

	public WordsGenerator(String lang, int phaseIndex, Person p) {
		super();
		this.PhaseIndex = phaseIndex;
		this.NumberOfRequiredWords = NUMBER_OF_WORDS_EACH_PHASE * PhaseIndex;
		this.person = p;
		this.lang = lang;
	}

	public Stack<Word> generate(String AudiosFolderPath, String ImagesFolderPath, String txtfilepath) {
		System.out.println("Hello from generate");
		SyllabusWords = new Stack<>();

		Stack<String> SyllabusWords_txt = new Stack<>();
		SyllabusWords_txt.push(this.person.getName().trim());
		Stack<String> SyllabusWords_BasicWords  =  SeShatEditorMain.utils.readfileintoStack(SeShatEditorMain.utils.BasicWordsFileName);
		if(SyllabusWords_BasicWords.size()>NumberOfRequiredWords){
			SyllabusWords_txt.addAll(SyllabusWords_BasicWords.subList(0, NumberOfRequiredWords));
		}else{
			SyllabusWords_txt.addAll(SyllabusWords_BasicWords);
		}
		
		int NumberOfWordsToBeGeneratedAsAdvanced = NumberOfRequiredWords - SyllabusWords_txt.size();
		SyllabusWords_txt.addAll(GenerateWordsTree(txtfilepath, NumberOfWordsToBeGeneratedAsAdvanced));
		for (String word_txt : SyllabusWords_txt) {	
			// check if exist and create audio files
			Word word = new Word(word_txt);
			//System.out.println(word_txt+"@@"+AudiosFolderPath+this.lang);
			word.setSpeechFilePath(SeShatEditorMain.utils.DoTTS(AudiosFolderPath,word_txt, this.lang));
			// check if exist and create imagepath
			// word.setImageFilePath(SeShatEditorMain.utils.FindHelpImage(ImagesFolderPath, word.getText()));
			// check if exist and create fv
			word.setPhrase(getWordPhrase(word_txt));
			this.SyllabusWords.push(word);
		}
		return this.SyllabusWords;
	}
	private Stack<String> GenerateWordsTree(String txtfilepath, int number_of_words) {
		Stack<String> AdvancedWords = new Stack<>();
		Map<java.lang.Character, Stack<String>> AvailableWordsOrganisedByCharacters = SeShatEditorMain.utils.readfileintoMap(txtfilepath,0);
		int ASCIINUM4_1stArChar = 1575;

		while (AdvancedWords.size() < number_of_words) {
			for (int i = 0; i < AvailableWordsOrganisedByCharacters.size() + 5; i++) {
				try {
					Stack<String> CurrentCharStack = AvailableWordsOrganisedByCharacters
							.get(new java.lang.Character((char) (ASCIINUM4_1stArChar + i)));
					AdvancedWords.push(CurrentCharStack.pop());
					AvailableWordsOrganisedByCharacters
							.remove(new java.lang.Character((char) (ASCIINUM4_1stArChar + i)));
					AvailableWordsOrganisedByCharacters.put(new java.lang.Character((char) (ASCIINUM4_1stArChar + i)),
							CurrentCharStack);
					if (AdvancedWords.size() >= number_of_words)
						break;
				} catch (Exception e) {
				 System.err.println("NULL: "+new
				java.lang.Character((char)(ASCIINUM4_1stArChar+i)));
				}
			}
		}
		return AdvancedWords;
	}

	/*
	 * after generate words syllabus Audio and Images and Mats should be ready
	 * to transfer to player
	 */

	public Direction[] GenerateWordFV(String word) {
		Direction[] word_directions = new Direction[0];

		for (int i = 0; i < word.length(); i++) {
			if (i == 0) {
				word_directions = SeShatEditorMain.utils.concatenate(word_directions, getCharacterFV(word.charAt(i), 0));
			} else if (i == (word.length() - 1)) {
				word_directions = SeShatEditorMain.utils.concatenate(word_directions, getCharacterFV(word.charAt(i), 2));
			} else {
				word_directions = SeShatEditorMain.utils.concatenate(word_directions, getCharacterFV(word.charAt(i), 1));
			}
		}
		System.out.println("word_directions"+word_directions.length);
		return word_directions;
	}

	// pass char and get from db or model
	private Direction[] getCharacterFV(char c, int index) {
		model.Character character = SeShatEditorMain.characters.get(new java.lang.Character(c));
		if (character != null) {
			switch (index) {
			
			case 0:
				return character.getFVwithoutEND(CharPosition.FIRST); // remove last
			case 2:
				return character.getFVwithoutINIT(CharPosition.LAST); // remove first
			default:
				return character.getFVwithoutEND_withoutINIT(CharPosition.MIDDLE); // remove last
																// // and first
			}
		} else {
			System.out.println("getCharacterFV:"+c+":NULL");
			return null;
		}
	}



	private String getWordPhrase(String word){
		return word;
	}
	/*
	 * need web service to download from
	 */
/*	public static String FindHelpImage(String ImagesFolder, String word) {
		if (SeShatEditorMain.utils.checkfileExist(ImagesFolder) && word.length() > 0) {
			word = word.replaceAll(" ", "+");
			String folder = ImagesFolder + word  ;
			if (!SeShatEditorMain.utils.checkfileExist(folder)) {
				// find image from webservice
				SeShatEditorMain.utils.executeCommand("./googliser.sh -p "+"\""+word+"\""+" -n 1 -u 25000 -l 1000 -f 0 --minimum-pixels vga --output "+folder);
				File[] listOfFiles = new File(folder).listFiles();
				try {
					createfile(folder+AppenddedToOutputImagefile);
					copyFileUsingFileStreams(listOfFiles[0],new File(folder+AppenddedToOutputImagefile));
				} catch (IOException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
				
				if(new File(folder).isDirectory()){
					new File(folder).delete();
				}
			}
			return folder+AppenddedToOutputImagefile;
		} else {
			System.err.println("FindHelpImage:: Invalid parameters!");
			return null;
		}
	}*/

}
