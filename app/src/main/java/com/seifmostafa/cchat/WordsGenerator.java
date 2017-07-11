import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Stack;

public class WordsGenerator {
	public enum DependentVariable { Name, Age, Gender};  // interests 
	private Stack<String> AvailableWords;
	public static final String filepathINPUT = "/home/azizax/Desktop/input";
	public static final String filepathdependentvariables = "/home/azizax/Desktop/dpv";
	public static final String filepathOUTPUT = "/home/azizax/Desktop/output";
	public static final	int	 NUMBER_OF_WORDS = 1000;

	private Stack<String>  readfile(String filepath){
		
		File file = new File(filepath);
		 Stack<String> words = new Stack<>();
		try {
			Scanner scan = new Scanner(file);
			while(scan.hasNextLine())
			{
				String line =null;
				line = scan.nextLine();
				words.push(line);		
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
		}
		return words;
	}
   
	private void sortwords(List<String> arr_items) {

        // give path of folders and sort the inside folders by their names
        Locale lithuanian = new Locale("ar");
        Collator lithuanianCollator = Collator.getInstance(lithuanian);
        Collections.sort(arr_items, lithuanianCollator);
    }
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
	private void writefile(Stack<String> result_words) {
		try {
			PrintWriter writer = new PrintWriter(filepathOUTPUT, "UTF-8");
			writer.print(result_words);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void DoProcess(String[]dependentVariables){
		AvailableWords =  readfile(filepathINPUT);
       // sortwords(AvailableWords);
		 if(dependentVariables.length==0){
			 writefile( GenerateWordsTree("محمد",NUMBER_OF_WORDS));

		 }else{
			 Stack<String> dpv = readfile(filepathdependentvariables);
			 // works for dependent variable name only 
			 // later will use other dependent variables ..
			 writefile( GenerateWordsTree(dpv.pop(),NUMBER_OF_WORDS));  
			 System.out.println("DONE!");
			 Calendar cal = Calendar.getInstance();
		        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		        System.out.println( sdf.format(cal.getTime()) );
		 }
		 
	 }
}
