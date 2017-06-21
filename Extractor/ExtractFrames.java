

import org.opencv.core.*;       
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;


public class ExtractFrames {
	
	public static void makeFrames(String videoDir, String name, String imageDir) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		VideoCapture camera = new VideoCapture();
		camera.open(videoDir + name);
		
    	if(!camera.isOpened()){
    		System.out.println("Error opening camera");
    	} else {
    		System.out.println("Extracting " + camera.get(7) + " frames");
    		boolean hasNext = true;
    		int i = 0;
    		while(hasNext) {
	    		Mat frame = new Mat();
		    	if (camera.read(frame)){
		    		String fileName = imageDir + "/" + i + ".jpg";
		    		Imgcodecs.imwrite(fileName, frame);
	    	    }	
		    	else hasNext = false;
		    	frame.release();
		    	i++;
    		}
    	}
    	camera.release();
	}

}