import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public class KeypressAnalyzer {
	/*
	 * Control pixels
	 * X:171, Y:429 RGB: 84 96 110
	 * x217 y362 r208 g210 b209
	 * x3 y351 r115 g123 b134
	 * 
	 * Map of keys:
	 * A: ,B: ,C: ,D: ,E: ,F: ,G: ,H: ,I: ,J: ,K: ,L: ,M: ,N: ,O: ,P: ,Q: ,R: ,S: ,T: ,U: ,V: ,W: ,X: ,Y: ,Z:
	 */
	static final int NORMAL_HEIGHT = 480;
	static final int NORMAL_WIDTH = 320;
	static final int KEYBOARD_HEIGHT = 220;
	
	static final List<Integer> controlPixel = Arrays.asList(171, 429, 84, 96, 110);
	static final List<Integer> controlPixel2 = Arrays.asList(217, 362, 208, 210, 209);
	static final List<Integer> controlPixel3 = Arrays.asList(3, 351, 115, 123, 134);
	
	static final List<Integer> controlPixelSideways = Arrays.asList(280, 214, 86, 93, 112);
	static final List<Integer> controlPixelSideways2 = Arrays.asList(232, 159, 206, 207, 212);
	static final List<Integer> controlPixelSideways3 = Arrays.asList(229, 470, 111, 119, 130);
	
	static final List<String> keys = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", 
												   "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", 
												   "U", "V", "W", "X", "Y", "Z");
	// x, y, same index as keys list
	static final int[][] keyPixels = {{32,322}, {192,377}, {129,377}, {98,321}, {80,269}, {129,321}, {162,321}, {192,321}, {241,270}, {225,322}, 
			   {256,323}, {288,322}, {256,376}, {224,377}, {271,270}, {303,270}, {15,270}, {111,270}, {64,322}, {146,270}, 
			   {207,269}, {160,377}, {48,269}, {96,376}, {175,269}, {64,376}};
	
	static final int[][] keyPixelsSideways = {{200,430}, {240,192}, {240,289}, {200,335}, {161,357}, {200,288}, {200,240}, {200,190}, {161,120}, {199,144}, 
			   {200,95}, {200,47}, {240,95}, {240,145}, {161,73}, {160,25}, {160,454}, {160,312}, {200,384}, {160,262}, 
			   {160,169}, {240,240}, {160,407}, {240,337}, {160,215}, {240,384}};
	static final int PRESSEDKEY = 186;
	
	
//	public static void main(String[] args) {
//		/*
//		String imageLocation = "images/cameratest184.jpg";
//		BufferedImage image = readImage(imageLocation);
//		boolean isKeyboard = keyboardOpened(image);
//		if(isKeyboard) {
//			System.out.println("Is keyboard");
//		}
//		else System.out.println("Is not keyboard");
//		*/
//		checkAllImages();
//		
//
//	}
	
	public static void checkAllImages(File dir, String name, String logDir) {
		PrintWriter writer = null;
		File[] fileList = dir.listFiles();
		try {
			BufferedImage test = ImageIO.read(fileList[0]);
			writer = new PrintWriter(logDir + "/" + name + ".txt", "UTF-8");
			writer.println("Video info");
			writer.println("Video length: " + fileList.length);
			writer.println("Image height: " + test.getHeight());
			writer.println("Image width: " + test.getWidth());
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int frame = 0; //10=1sec
		Arrays.sort(fileList, (a, b) -> Long.compare(a.lastModified(), b.lastModified()));
		for (File f:fileList) {
			BufferedImage image;
			try {
				
				image = ImageIO.read(f);
				boolean isKeyboard = keyboardOpened(image);
				if(isKeyboard) {
					writer.println(f.getName() + ": Keyboard is opened");
					String pressedKeys = scanKeyPresses(image, keyPixels, 0);
					boolean spacePressed = spacePressed(image);
					if(!pressedKeys.isEmpty()) {
						writer.println(f.getName() + ": Key is pressed: " + pressedKeys);
					}
					if(spacePressed) {
						writer.println(f.getName() + ": Key is pressed: " + "SPACE");
					}
				}
				else {
					boolean isKeyboardSideways = keyboardOpenedSideways(image);
					if (isKeyboardSideways) {
						writer.println(frame + ": Keyboard is opened sideways");
						String pressedKeys = scanKeyPresses(image, keyPixelsSideways, 1);
						if(!pressedKeys.isEmpty()) {
							writer.println(frame + ": Key is pressed: " + pressedKeys);
						}
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			frame++;
		}
		writer.println("File closed");
		writer.close();
	}
	public static String scanKeyPresses(BufferedImage image, int[][] pixels, int sideways) {
		String keysPressed = "";
		int calibY = callibrateKeyboardY(image);
		int heightDiff = image.getHeight() - NORMAL_HEIGHT;
		int widthAdjust = image.getWidth() / NORMAL_WIDTH;
		for (int i=0; i < pixels.length; i++) {
			int pixel = 0;
			if (sideways == 0 && widthAdjust > 1) pixel = image.getRGB(pixels[i][0]*widthAdjust, ((pixels[i][1])*widthAdjust)+calibY);
			else if (sideways == 0) pixel = image.getRGB(pixels[i][0], ((pixels[i][1]))+heightDiff+calibY);
			else if (sideways == 1) pixel = image.getRGB(pixels[i][0]*widthAdjust, pixels[i][1]*widthAdjust);
			int red = (pixel >> 16) & 0x000000FF;
			int green = (pixel >> 8) & 0x000000FF;
			int blue = (pixel) & 0x000000FF;
			
			int redGreenDiff = Math.abs(red - PRESSEDKEY);
			int greenBlueDiff = Math.abs(green - PRESSEDKEY);
			int blueRedDiff = Math.abs(blue - PRESSEDKEY);

			if (redGreenDiff <= 6 && greenBlueDiff <= 6 && blueRedDiff <= 6) { 
				keysPressed += keys.get(i);
			}
		}
		return keysPressed;
		
	}
	public static boolean spacePressed(BufferedImage image) {
		int SPACE_X = 214;
		int SPACE_Y = 450;
		int widthAdjust = image.getWidth() / NORMAL_WIDTH;
		int calibY = callibrateKeyboardY(image);
		int heightDiff = image.getHeight() - NORMAL_HEIGHT;
		int pixel = image.getRGB(214*widthAdjust, 450+heightDiff+calibY);
		if (widthAdjust > 1) pixel = image.getRGB(SPACE_X*widthAdjust, (SPACE_Y*widthAdjust)+calibY);
		int red = (pixel >> 16) & 0x000000FF;
		int green = (pixel >> 8) & 0x000000FF;
		int blue = (pixel) & 0x000000FF;
		
		int redDiff = Math.abs(red - 153); //153 red space
		int blueDiff = Math.abs(blue - 157); //157 blue space
		int greenDiff = Math.abs(green - 166); //166 green space
		if (redDiff <= 15 && blueDiff <= 15 && greenDiff <= 15)	return true;
		
		else return false;
	}
	public static boolean keyboardOpenedSideways(BufferedImage image) {
		
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		int greyPixels = 0;
		int totPixels = 0;
		
		for (int i = (width/2); i < width; i++) {
			for (int j = 0; j < height; j++) {
				totPixels++;
				int pixel = image.getRGB(i, j);
				int red = (pixel >> 16) & 0x000000FF;
				int green = (pixel >> 8) & 0x000000FF;
				int blue = (pixel) & 0x000000FF;
				
				int redGreenDiff = Math.abs(red - green);
				int greenBlueDiff = Math.abs(green - blue);
				int blueRedDiff = Math.abs(blue - red);

				if (redGreenDiff <= 25 && greenBlueDiff <= 25 && blueRedDiff <= 25) { 
					//Greyscale pixels have the 'same' r, g and b
					
					greyPixels++;

					
				}
			}
		}// end of the outer for loop
		double diff = (double)greyPixels / (double)totPixels;
		if(diff > 0.90 && checkPixels(image, 1)) return true;
		else return false;
		
	}
	public static boolean keyboardOpened(BufferedImage image) {
		
		int widthAdjust = image.getWidth() / NORMAL_WIDTH;
		int width = image.getWidth();
		int height = image.getHeight();
		
		
		int greyPixels = 0;
		int totPixels = 0;
		
		for (int i = 0; i < width; i++) {
			for (int j = height - (KEYBOARD_HEIGHT*widthAdjust); j < height; j++) {
				totPixels++;
				int pixel = image.getRGB(i, j);
				int red = (pixel >> 16) & 0x000000FF;
				int green = (pixel >> 8) & 0x000000FF;
				int blue = (pixel) & 0x000000FF;
				
				int redGreenDiff = Math.abs(red - green);
				int greenBlueDiff = Math.abs(green - blue);
				int blueRedDiff = Math.abs(blue - red);

				if (redGreenDiff <= 25 && greenBlueDiff <= 25 && blueRedDiff <= 25) { 
					//Greyscale pixels have the 'same' r, g and b
					
					greyPixels++;

					
				}
			}
		}// end of the outer for loop
		double diff = (double)greyPixels / (double)totPixels;
		if(diff > 0.90 && checkPixels(image, 0)) return true;
		else return false;
		
	}
	
	public static int callibrateKeyboardY(BufferedImage image) {
		int calib = 0;
		int heightDiff = image.getHeight() - NORMAL_HEIGHT;
		int widthAdjust = image.getWidth() / NORMAL_WIDTH;
		int smallestDiff = 10000;
		for(int i = -10; i <= 10; i++) {
			int first = checkPixelDiff(image, controlPixel.get(0)*widthAdjust, controlPixel.get(1)+i+heightDiff, controlPixel.get(2), controlPixel.get(3), controlPixel.get(4));
			int second = checkPixelDiff(image, controlPixel2.get(0)*widthAdjust, controlPixel2.get(1)+i+heightDiff, controlPixel2.get(2), controlPixel2.get(3), controlPixel2.get(4));
			int third = checkPixelDiff(image, controlPixel3.get(0)*widthAdjust, controlPixel3.get(1)+i+heightDiff, controlPixel3.get(2), controlPixel3.get(3), controlPixel3.get(4));
			if (first+second+third < smallestDiff) {
				smallestDiff = first+second+third;
				calib = i;
			}
		}
		return calib;
	}
	
	public static boolean checkPixels(BufferedImage image, int sideways) {
		int calibY = callibrateKeyboardY(image);
		int first = 0;
		int second = 0;
		int third = 0;
		int heightDiff = image.getHeight() - NORMAL_HEIGHT;
		int heightMult = image.getHeight() / NORMAL_HEIGHT;
		int widthAdjust = image.getWidth() / NORMAL_WIDTH;
		if (sideways == 0 && widthAdjust > 1) {
			first = checkPixelDiff(image, controlPixel.get(0)*widthAdjust, (controlPixel.get(1)*widthAdjust)+calibY, controlPixel.get(2), controlPixel.get(3), controlPixel.get(4));
			second = checkPixelDiff(image, controlPixel2.get(0)*widthAdjust, (controlPixel2.get(1)*widthAdjust)+calibY, controlPixel2.get(2), controlPixel2.get(3), controlPixel2.get(4));
			third = checkPixelDiff(image, controlPixel3.get(0)*widthAdjust, (controlPixel3.get(1)*widthAdjust)+calibY, controlPixel3.get(2), controlPixel3.get(3), controlPixel3.get(4));
		}
		else if (sideways == 0) {
			first = checkPixelDiff(image, controlPixel.get(0)*widthAdjust, controlPixel.get(1)+calibY+heightDiff, controlPixel.get(2), controlPixel.get(3), controlPixel.get(4));
			second = checkPixelDiff(image, controlPixel2.get(0)*widthAdjust, controlPixel2.get(1)+calibY+heightDiff, controlPixel2.get(2), controlPixel2.get(3), controlPixel2.get(4));
			third = checkPixelDiff(image, controlPixel3.get(0)*widthAdjust, controlPixel3.get(1)+calibY+heightDiff, controlPixel3.get(2), controlPixel3.get(3), controlPixel3.get(4));
		}
		else if (sideways == 1) {
			first = checkPixelDiff(image, controlPixelSideways.get(0)*widthAdjust, controlPixelSideways.get(1)*heightMult, controlPixelSideways.get(2), controlPixelSideways.get(3), controlPixelSideways.get(4));
			second = checkPixelDiff(image, controlPixelSideways2.get(0)*widthAdjust, controlPixelSideways2.get(1)*heightMult, controlPixelSideways2.get(2), controlPixelSideways2.get(3), controlPixelSideways2.get(4));
			third = checkPixelDiff(image, controlPixelSideways3.get(0)*widthAdjust, controlPixelSideways3.get(1)*heightMult, controlPixelSideways3.get(2), controlPixelSideways3.get(3), controlPixelSideways3.get(4));
		}
		if (first <= 40 && second <= 40 && third <= 40) return true;
		else return false;
	}
	public static int checkPixelDiff(BufferedImage image, int x, int y, int r, int g, int b) {
		int pixel = image.getRGB(x, y);
		int red = (pixel >> 16) & 0x000000FF;
		int green = (pixel >> 8) & 0x000000FF;
		int blue = (pixel) & 0x000000FF;
		
		int redDiff = Math.abs(red - r);
		int greenDiff = Math.abs(green - g);
		int blueDiff = Math.abs(blue - b);
		
		return redDiff + greenDiff + blueDiff;
		
	}
	
	public static BufferedImage readImage(String fileLocation) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(fileLocation));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
}
