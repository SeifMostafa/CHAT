package com.example.seif.seshatplayer.model;

import java.util.ArrayList;
import java.util.Arrays;



public class Character {
	public enum CharPosition {
		FREE,FIRST,MIDDLE,LAST
	}
	char ch;
	/*
	 * Syllabus folder will contain CharactersFolder for data for this class
	 */
	// 3 files for character positions
	private String ImagesFilePath[]= null, SpeechesFilePath[]= null;
	private Direction[][] FV= null;
	//private Point[][] TiggerPoints = null;

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

	public Direction[][] getFV() {
		return FV;
	}

	public void setFV(Direction[][] fv) {
		FV = fv;
	}

	public Character(char ch) {
		super();
		this.ch = ch;
	}

	public Direction[] getFVwithoutINIT(CharPosition pos) {
		try {
			ArrayList<Direction> arrayList = new ArrayList<Direction>(Arrays.asList(FV[CharPosition2PositionIndex(pos)]));
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

	public Direction[] getFVwithoutEND(CharPosition pos) {
		try {
			ArrayList<Direction> arrayList = new ArrayList<Direction>(Arrays.asList(FV[CharPosition2PositionIndex(pos)]));
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

	public Direction[] getFVwithoutEND_withoutINIT(CharPosition pos) {
		try {
			ArrayList<Direction> arrayList = new ArrayList<Direction>(Arrays.asList(FV[CharPosition2PositionIndex(pos)]));

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

/*	public Point[] getTiggerPoints() {
		
		return TiggerPoints;
	}

	public void setTiggerPoints(Point[] tiggerPoints) {
		TiggerPoints = tiggerPoints;
	}*/

	@Override
	public String toString() {
		return this.ch + this.FV.toString()/* + this.TiggerPoints.toString()*/ + this.SpeechesFilePath
				+ this.SpeechesFilePath;
	}
	private int CharPosition2PositionIndex(CharPosition charPosition){
		switch(charPosition){
		case FIRST:
			return 0;
		case FREE:
			return 1;
		case LAST:
			return 3;
		case MIDDLE:
			return 2;
		default:
			return -1;
		}
	}

}
