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
		int i = 0;
		for (File f:logFiles) {
			Scanner sc = new Scanner(f);
			while (sc.hasNext()) {
				String word = sc.next();
				i++;
			}
		}
		System.out.println(i);
	}
}
