import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class FileAnalyzer {
	
	static String logDir = "TheFinalLogs";
	static String outputDir = "analyzed";
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		File logFiles = new File(logDir);
		PrintWriter writer = new PrintWriter("error.txt", "UTF-8");
		loopFiles(logFiles.listFiles(), writer);

	}
	
	public static void loopFiles(File[] logFiles, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		
		for (File f:logFiles) {
			if (f.isDirectory()) {
				File newDir = new File(logDir + "/" + f.getName());
				newDir.mkdir();
				PrintWriter newWriter = new PrintWriter(outputDir + "/" + f.getName() + ".txt", "UTF-8");
				loopFiles(f.listFiles(), newWriter);
			}
			else {
				Scanner sc = new Scanner(f);
				String infoLine = sc.nextLine();
				String videoLength = sc.nextLine().split(": ")[1];
				String videoHeight = sc.nextLine().split(": ")[1];
				String videoWidth = sc.nextLine().split(": ")[1];
				String lastLine = "";
				double lastPress = 0.0;
				boolean somethingWritten = false;
				boolean newSentence = false;
				writer.println(f.getName() + ":");
				while (sc.hasNextLine()) {
					String[] line = sc.nextLine().split(": ");
					if (line.length >= 3 ) {
						if (!line[2].equals(lastLine)) {
							double second = Double.parseDouble(line[0].split("\\.")[0])/10;
							if ((second - lastPress) > 5) newSentence = true;
							lastPress = second;
							somethingWritten = true;
							if (line[2].equals("SPACE")) {
								lastLine = line[2];
								if (newSentence) writer.println("");
								writer.print(" ");
							}
							else if (line[2].length() > 1) {
								String[] newLine = line[2].split("");
								if (!newLine[0].equals(lastLine)) {		//This excludes all characters except one when several keys are pressed
									lastLine = newLine[0];
									if (newSentence) writer.println("");
									writer.print(newLine[0]);
								}
//								for (String s: newLine) {				//This includes several characters clicked at once
//									if (!s.equals(lastLine)) {		
//										lastLine = s;
//										writer.print(s);
//									}
//								}
							}
							else {
								lastLine = line[2];
								writer.print(line[2]);
							}
							
						}
					}
					newSentence = false;
				}
				if (somethingWritten){
					writer.println("");
				}
				sc.close();
				
				
			}
		}
		writer.close();
	}
	
}
