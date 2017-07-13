package com.seifmostafa.cchat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Stack;

import static com.seifmostafa.cchat.Utils.readfileintoStack;
import static com.seifmostafa.cchat.Utils.writeStackTofile;


public class WordsGenerator {
	public enum DependentVariable { Name, Age, Gender};  // interests 
	private Stack<String> AvailableWords;
	public static final String filepathINPUT = "/home/azizax/Desktop/input";
	public static final String filepathdependentvariables = "/home/azizax/Desktop/dpv";
	public static final String filepathOUTPUT = "/home/azizax/Desktop/output";
	public static final	int	 NUMBER_OF_WORDS = 1000;


	private String[] GenerateName_Characters(String name) {
        String[] Name_Characters = name.split("");
        return Name_Characters;
    }

	private Stack<String> GenerateWordsTree(String Name, int number_of_words) {
        int levels = 0;
        int added = 0;
        Stack<String> tree = new Stack<>();
        tree.add(Name);
        added++;
        while (number_of_words > tree.size()) {
            for (int i = 0; i < added; i++) {
                added = 0;
                String[] Word_characters = GenerateName_Characters(tree.get((i) + levels));
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

	public void DoProcess(String[]dependentVariables){
		AvailableWords =  readfileintoStack(filepathINPUT);
       // sortwords(AvailableWords);
		 if(dependentVariables.length==0){
			 writeStackTofile( GenerateWordsTree("محمد",NUMBER_OF_WORDS),filepathOUTPUT);

		 }else{
			 Stack<String> dpv = readfileintoStack(filepathdependentvariables);
			 // works for dependent variable name only 
			 // later will use other dependent variables ..
			 writeStackTofile( GenerateWordsTree(dpv.pop(),NUMBER_OF_WORDS),filepathOUTPUT);
			 System.out.println("DONE!");
			 Calendar cal = Calendar.getInstance();
		        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		        System.out.println( sdf.format(cal.getTime()) );
		 }
		 
	 }

}
