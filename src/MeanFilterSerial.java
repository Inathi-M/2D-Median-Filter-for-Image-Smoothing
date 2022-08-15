import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.awt.Color;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

public class MeanFilterSerial {
    /*
     *
     * MUST REPLACE COLOR CLASS
     *
     */
    public static void MeanSmoothFilter(int height, int width, BufferedImage image, BufferedImage finalImage, int squareSize){
        /*Smoothing the image with sliding window approach*/
        for (int y = 0; y<height; y++){ // Gets y coord of pixel from input image

            int[] A = new int[squareSize*squareSize];
            int[] B = new int[squareSize*squareSize];
            int[] R = new int[squareSize*squareSize];
            int[] G = new int[squareSize*squareSize];

            if (y < height-squareSize){ // To prevent loop crashing at height minus window size pixel

                for (int x = 0; x<width; x++){  // Gets x coord of pixel from input image

                    int sumG = 0;
                    int sumB = 0;
                    int sumR = 0;
                    int sumAlpha = 0;

                    if (x < (width-squareSize)){ // To prevent loop crashing at width minus window size pixel

                        for (int a=0; a<squareSize; a++){ // y-axis of window

                            for (int n=0; n<squareSize; n++){ // x-axis of window

                                int pixel = image.getRGB( x+n, y+a);

                                int alpha = (pixel>>24) & 0xff;
                                A[a+n]= alpha;
                                sumAlpha = sumAlpha+ alpha;

                                int blue = pixel & 0xff;
                                B[a+n] = blue;
                                sumB = sumB + blue;
                                int red = (pixel>>16) & 0xff;
                                R[a+n] = red;
                                sumR = sumR + red;
                                int green = (pixel>>8) & 0xff;
                                G[a+n] = green;
                                sumG = sumG + green;


                            }


                        }

                    }
                    else {

                        for (int a=0; a<squareSize; a++){

                            for (int b=squareSize; b>0; b--){

                                int pixel = image.getRGB( x-b, y+a);

                                int alpha = (pixel>>24) & 0xff;
                                A[a+b]= alpha;
                                sumAlpha = sumAlpha+ alpha;

                                int blue = pixel & 0xff;
                                B[a+b] = blue;
                                sumB = sumB + blue;
                                int red = (pixel>>16) & 0xff;
                                R[a+b] = red;
                                sumR = sumR + red;
                                int green = (pixel>>8) & 0xff;
                                G[a+b] = green;
                                sumG = sumG + green;

                            }

                        }

                    }

                    /*Calculating average colour values */
                    int setB = sumB/B.length;
                    int setR = sumR/R.length;
                    int setG = sumG/G.length;
                    int setAl = sumAlpha/A.length;

                    int setColour = 0;


                    setColour = setColour | (setAl<<24);
                    setColour = setColour | (setR<<16);
                    setColour = setColour | (setG<<8);
                    setColour = setColour | setB;

                    finalImage.setRGB(x, y, setColour); // Seting output image pixel
                }

            }

            else{

                for (int x = 0; x<width; x++){

                    int sumAlpha = 0;
                    int sumB = 0;
                    int sumR = 0;
                    int sumG = 0;

                    if (x < (width-squareSize)){

                        for (int a=squareSize; a>0; a--){

                            for (int b=0; b<squareSize; b++){

                                int pixel = image.getRGB( x+b, y-(a));

                                int alpha = (pixel>>24) & 0xff;
                                A[a+b]= alpha;
                                sumAlpha = sumAlpha+ alpha;

                                int blue = pixel & 0xff;
                                B[a+b] = blue;
                                sumB = sumB + blue;
                                int red = (pixel>>16) & 0xff;
                                R[a+b] = red;
                                sumR = sumR + red;
                                int green = (pixel>>8) & 0xff;
                                G[a+b] = green;
                                sumG = sumG + green;


                            }

                        }
                    }
                    else {

                        for (int a=squareSize; a>0; a--){

                            for (int b=squareSize; b>0; b--){

                                int pixel = image.getRGB( x-b, y-a);

                                int alpha = (pixel>>24) & 0xff;
                                A[a+b]= alpha;
                                sumAlpha = sumAlpha+ alpha;

                                int blue = pixel & 0xff;
                                B[a+b] = blue;
                                sumB = sumB + blue;
                                int red = (pixel>>16) & 0xff;
                                R[a+b] = red;
                                sumR = sumR + red;
                                int green = (pixel>>8) & 0xff;
                                G[a+b] = green;
                                sumG = sumG + green;

                            }
                        }
                    }

                    int setB = sumB/B.length;
                    int setR = sumR/R.length;
                    int setG = sumG/G.length;
                    int setA = sumAlpha/A.length;

                    int setColour = 0;

                    setColour = setColour | (setA<<24);
                    setColour = setColour | (setR<<16);
                    setColour = setColour | (setG<<8);
                    setColour = setColour | setB;

                    finalImage.setRGB(x, y, setColour);
                }

            }

        }
    }
    public static void main(String[] args) throws IOException {

        Scanner input = new Scanner(System.in);

        System.out.println("Please enter command-line parameters (in that order): \n<inputImageName> <outputImageName> <windowWidth>\n");
        String inputString = input.nextLine(); input.close();
        String[] arrString = inputString.split(" ");

        int squareSize = 0;

        try {
            if (Integer.parseInt(arrString[2])%2 == 0 || Integer.parseInt(arrString[2]) < 3){
                System.out.println("\nIncorrect window size entered.\nProgramme ended.");
                System.exit(0);
            }

            squareSize = Integer.parseInt(arrString[2]);
        } catch (NumberFormatException e) {
            System.out.println("\nDid not enter a number.\nProgramme ended.");
            System.exit(0);
        }

        BufferedImage bImage;
        double startTime;

        try {
            bImage= ImageIO.read(new File(arrString[0]+".jpg"));

            int height = bImage.getHeight();
            int width = bImage.getWidth();
            System.out.println("h = "+height +" and w ="+width);

            BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            //Smoothing the image with sliding window approach
            startTime = System.currentTimeMillis();
            MeanSmoothFilter(height,width,bImage,finalImage,squareSize);

            double endTime = System.currentTimeMillis();

            double executionTime =  endTime - startTime;
            System.out.println("\nTime taken = " + executionTime);
            ImageIO.write(finalImage, "jpeg", new File(arrString[1]+".jpg"));

        } catch (IIOException e) {
            System.out.println("\nIncorrect input Image name entered.\nProgramme ended.");
            System.exit(0);
        }
    }
}
