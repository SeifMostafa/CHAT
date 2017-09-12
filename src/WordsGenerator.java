import java.awt.Point;
import java.util.Map;
import java.util.Stack;

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
		SyllabusWords_txt.addAll(Utils.readfileintoStack(Utils.BasicWordsFileName));

		int NumberOfWordsToBeGeneratedAsAdvanced = NumberOfRequiredWords - SyllabusWords_txt.size();
		
		SyllabusWords_txt.addAll(GenerateWordsTree(txtfilepath, NumberOfWordsToBeGeneratedAsAdvanced));
		for (String word_txt : SyllabusWords_txt) {	
			// check if exist and create audio files
			Word word = new Word(word_txt);
			//System.out.println(word_txt+"@@"+AudiosFolderPath+this.lang);
			word.setSpeechFilePath(Utils.DoTTS(AudiosFolderPath,word_txt, this.lang));
			// check if exist and create imagepath
			// word.setImageFilePath(FindHelpImage(word.getText(),
			// ImagesFolderPath));
			// check if exist and create fv
			word.setFV(GenerateWordFV(word_txt));
			try{
				word.setTriggerpoints(GenerateWordTR(word_txt));
			}catch(Exception e){
				System.out.println(e.toString() + "         from:WordsGenerator::generate");
			}
			word.setPhrase(getWordPhrase(word_txt));
			this.SyllabusWords.push(word);
		}
		return this.SyllabusWords;
	}

//	private String[] GenerateWord_Characters(String word) {
//		String[] word_Characters = word.split("");
//		return word_Characters;
//	}
	 
	// private Stack<String> GenerateWordsTree(String txtfilepath, int
	// number_of_words) {
	// int levels = 0;
	// int added = 0;
	// Stack<String> AvailableWords = new Stack<>();
	// // read AvailableWords read from txtfile
	// AvailableWords = Utils.readfileintoStack(txtfilepath);
	// Stack<String> tree = new Stack<>();
	// tree.add(this.person.getName());
	// added++;
	// while (number_of_words > tree.size()) {
	// for (int i = 0; i < added; i++) {
	// added = 0;
	// String[] Word_characters = GenerateWord_Characters(tree.get((i) +
	// levels));
	// for (String CH : Word_characters) {
	// for (String searchword : AvailableWords) {
	// if (searchword.substring(0, 1).equals(CH)) {
	// tree.add(searchword);
	// added++;
	// AvailableWords.remove(searchword);
	// break;
	// }
	// }
	// }
	// }
	// levels++;
	// }
	// return tree;
	// }
	private Stack<String> GenerateWordsTree(String txtfilepath, int number_of_words) {
		Stack<String> AdvancedWords = new Stack<>();
		Map<java.lang.Character, Stack<String>> AvailableWordsOrganisedByCharacters = Utils.readfileintoMap(txtfilepath,0);
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
					// System.err.println("NULL: "+new
					// java.lang.Character((char)(ASCIINUM4_1stArChar+i)));
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
				word_directions = Utils.concatenate(word_directions, getCharacterFV(word.charAt(i), 0));
			} else if (i == (word.length() - 1)) {
				word_directions = Utils.concatenate(word_directions, getCharacterFV(word.charAt(i), 2));
			} else {
				word_directions = Utils.concatenate(word_directions, getCharacterFV(word.charAt(i), 1));
			}
		}
		return word_directions;
	}

	// pass char and get from db or model
	private Direction[] getCharacterFV(char c, int index) {
		model.Character character = SeShatEditorMain.characters.get(new java.lang.Character(c));
		if (character != null) {
			switch (index) {
			case 0:
				return character.getFVwithoutEND(); // remove last
			case 2:
				return character.getFVwithoutINIT(); // remove first
			default:
				return character.getFVwithoutEND_withoutINIT(); // remove last
																// // and first
			}
		} else {
			System.out.println("getCharacterFV:"+c+":NULL");
			
			return null;
		}
	}

	public Point[] GenerateWordTR(String word) {
		Point[] word_directions = new Point[0];

		for (int i = 0; i < word.length(); i++) {
			word_directions = Utils.concatenate(word_directions, getCharacterTR(word.charAt(i)));
		}
		return word_directions;
	}

	// pass char and get from db or model
	private Point[] getCharacterTR(char c) {
		model.Character character = SeShatEditorMain.characters.get(new java.lang.Character(c));
		if(character.getTiggerPoints().equals(null))return new Point[0];
		return character.getTiggerPoints();
	}


	private String getWordPhrase(String word){
		return word;
	}


}
