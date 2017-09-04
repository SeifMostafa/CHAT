package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

public class Character {
	char ch;
	/*
	 * Syllabus folder will contain CharactersFolder for data for this class
	 */
	// 3 files for character positions
	private String ImagesFilePath[], SpeechesFilePath[];
	private Direction[] FV;
	private Point[] TiggerPoints;

	public char getCh() {
		return ch;
	}

	public void setCh(char ch) {
		this.ch = ch;
	}

	public String[] getImagesFilePath() {
		return ImagesFilePath;
	}

	public void setImagesFilePath(String[] imagesFilePath) {
		ImagesFilePath = imagesFilePath;
	}

	public String[] getSpeechFilePath() {
		return SpeechesFilePath;
	}

	public void setSpeechesFilePath(String[] speechesFilePath) {
		SpeechesFilePath = speechesFilePath;
	}

	public Direction[] getFV() {
		return FV;
	}

	public void setFV(Direction[] fv) {
		FV = fv;
	}

	public Character(char ch) {
		super();
		this.ch = ch;
	}

	public Direction[] getFVwithoutINIT() {

		ArrayList<Direction> arrayList = new ArrayList<Direction>(Arrays.asList(FV));
		arrayList.remove(0);
		Direction[] a = new Direction[arrayList.size()];
		return arrayList.toArray(a);
	}

	public Direction[] getFVwithoutEND() {
		ArrayList<Direction> arrayList = new ArrayList<Direction>(Arrays.asList(FV));
		arrayList.remove(arrayList.size() - 1);
		Direction[] a = new Direction[arrayList.size()];
		return arrayList.toArray(a);
	}

	public Direction[] getFVwithoutEND_withoutINIT() {
		ArrayList<Direction> arrayList = new ArrayList<Direction>(Arrays.asList(FV));
		arrayList.remove(arrayList.size() - 1);
		arrayList.remove(0);
		Direction[] a = new Direction[arrayList.size()];
		return arrayList.toArray(a);
	}

	public Point[] getTiggerPoints() {
		return TiggerPoints;
	}

	public void setTiggerPoints(Point[] tiggerPoints) {
		TiggerPoints = tiggerPoints;
	}

}
