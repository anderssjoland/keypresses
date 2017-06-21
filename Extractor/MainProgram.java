import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class MainProgram {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		String videoDir = "videos";
		String imageDir = "images";
		
		File videoFiles = new File(videoDir);
		File imageFiles = new File(imageDir);
		int totalFiles = countFiles(videoFiles.listFiles());
		PrintWriter writer = new PrintWriter("error.txt", "UTF-8");
		loopFiles(videoFiles.listFiles(), imageFiles, videoDir, imageDir, totalFiles, 1);
		
		
//		for (File f:videoFiles.listFiles()) {
//			
//			deleteAll(imageFiles);
//			ExtractFrames.makeFrames(videoDir + "/", f.getName(), imageDir);
//			KeypressAnalyzer.checkAllImages(imageFiles, f.getName());
//		}
		
		
	}
	
	public static int loopFiles(File[] videoFiles, File imageFiles, String videoDir, String imageDir, int totalFiles, int current) throws FileNotFoundException, UnsupportedEncodingException {
		for (File f:videoFiles) {
			if (f.isDirectory()) {
				System.out.println(videoDir + "/" + f.getName());
				File newDir = new File("FinalLogs/" + videoDir + "/" + f.getName());
				newDir.mkdir();
				current = loopFiles(f.listFiles(), imageFiles, videoDir + "/" + f.getName(), imageDir, totalFiles, current);
				
			}
			else {
				try {
					deleteAll(imageFiles);
					System.out.println("Extracting frames from video " + f.getName() + " " + current + " of " + totalFiles);
					ExtractFrames.makeFrames(videoDir + "/", f.getName(), imageDir);
					System.out.println("Analyzing keypresses");
					KeypressAnalyzer.checkAllImages(imageFiles, f.getName(), "FinalLogs/" + videoDir);
					current++;
				}
				catch (Throwable e) {
					PrintWriter writer = new PrintWriter("errors/"+f.getName() + "error.txt", "UTF-8");
					e.printStackTrace(writer);
					writer.close();
				}
			}
		}
		return current;
	}
	
	public static int countFiles(File[] videoFiles) {
		int i = 0;
		for (File f:videoFiles) {
			if (f.isDirectory()) {
				i = i+countFiles(f.listFiles());
			}
			else i++;
		}
		
		return i;
	}
	private static void deleteAll(File dir) {
		for (File f: dir.listFiles()) {
			f.delete();
		}
	}

}
