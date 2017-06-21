import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class FileAnalyzer {
	
	static String logDir = "toanalyze";
	static String outputDir = "analyzed";
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		File logFiles = new File(logDir);
		PrintWriter writer = new PrintWriter("wordCount.txt", "UTF-8");
		loopFiles(logFiles.listFiles(), writer);

	}
	
	public static void loopFiles(File[] logFiles, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		HashMap<String, Integer> listOfWords = new HashMap<String, Integer>();
		for (File f:logFiles) {
			Scanner sc = new Scanner(f);
			System.out.println();
			System.out.print(f.getName() + ": " + sc.hasNext());
			while (sc.hasNext()) {
				String word = sc.next();
				System.out.print(word);
				if(!listOfWords.containsKey(word)) {                             
	                listOfWords.put(word, 1);
	            }
				else {
					int countWord = listOfWords.get(word) + 1;
	                listOfWords.remove(word);
	                listOfWords.put(word, countWord);
				}
			}
		}
		Set<Entry<String, Integer>> set = listOfWords.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(
                set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                    Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        int i = 1;
        for (Entry<String, Integer> entry : list) {
            writer.println(i + ": " + entry.getKey() + " " + entry.getValue());
            i++;

        }
	}
}
