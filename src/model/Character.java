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
	private String ImagesFilePath[]= null, SpeechesFilePath[]= null;
	private Direction[] FV= null;
	private Point[] TiggerPoints = null;

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
		try {
			ArrayList<Direction> arrayList = new ArrayList<Direction>(Arrays.asList(FV));
			Direction[] a;
			if (arrayList.size() > 0) {
				arrayList.remove(0);
				a = new Direction[arrayList.size()];
			} else {
				a = new Direction[arrayList.size()];
			}
			return arrayList.toArray(a);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Direction[] getFVwithoutEND() {
		try {
			ArrayList<Direction> arrayList = new ArrayList<Direction>(Arrays.asList(FV));
			Direction[] a;
			if (arrayList.size() > 0) {
				arrayList.remove(arrayList.size() - 1);
				a = new Direction[arrayList.size()];
				return arrayList.toArray(a);
			} else {
				a = new Direction[arrayList.size()];
			}
			return arrayList.toArray(a);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Direction[] getFVwithoutEND_withoutINIT() {
		try {
			ArrayList<Direction> arrayList = new ArrayList<Direction>(Arrays.asList(FV));

			Direction[] a;

			if (arrayList.size() > 0) {
				arrayList.remove(arrayList.size() - 1);
				arrayList.remove(0);
				a = new Direction[arrayList.size()];
				return arrayList.toArray(a);
			} else {
				a = new Direction[arrayList.size()];
			}
			return arrayList.toArray(a);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Point[] getTiggerPoints() {
		
		return TiggerPoints;
	}

	public void setTiggerPoints(Point[] tiggerPoints) {
		TiggerPoints = tiggerPoints;
	}

	@Override
	public String toString() {
		return this.ch + this.FV.toString() + this.TiggerPoints.toString() + this.SpeechesFilePath
				+ this.SpeechesFilePath;
	}

}
