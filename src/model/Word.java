package model;

import java.awt.Point;

public class Word {
	private String Text, ImageFilePath=null, SpeechFilePath=null, Phrase=null;
	

	public Word(String text) {
		super();
		Text = text;
	}

	public Word(String text, String imageFilePath, String speechFilePath, String phrase, Point[] triggerpoints,
			Direction[] fV) {
		super();
		Text = text;
		ImageFilePath = imageFilePath;
		SpeechFilePath = speechFilePath;
		Phrase = phrase;

	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		Text = text;
	}

	public String getImageFilePath() {
		return ImageFilePath;
	}

	public void setImageFilePath(String imageFilePath) {
		ImageFilePath = imageFilePath;
	}

	public String getSpeechFilePath() {
		return SpeechFilePath;
	}

	public void setSpeechFilePath(String speechFilePath) {
		SpeechFilePath = speechFilePath;
	}

	public Character[] getWordChars() {
		Character[] chars = new Character[this.Text.length()];
		for (int i = 0; i < this.Text.length(); i++) {
			Character character;
			character = new Character(this.Text.charAt(i));
			chars[i] = character;
		}
		return chars;
	}


	public String getPhrase() {
		return Phrase;
	}

	public void setPhrase(String phrase) {
		Phrase = phrase;
	}

}
