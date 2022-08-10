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
    public static void main(String[] args) throws IOException {

        Scanner input = new Scanner(System.in);

        System.out.println("Please enter command-line parameters (in that order): \n<inputImageName> <outputImageName> <windowWidth>\n");
        String linePara = input.nextLine(); input.close();
        String[] lineParaArr = linePara.split(" ");

        int winSize = 0;

        try {
            if (Integer.parseInt(lineParaArr[2])%2 == 0 || Integer.parseInt(lineParaArr[2]) < 3){
                System.out.println("\nIncorrect window size entered.\nProgramme ended.");
                System.exit(0);
            }

            winSize = Integer.parseInt(lineParaArr[2]);
        } catch (NumberFormatException e) {
            System.out.println("\nDid not enter a number.\nProgramme ended.");
            System.exit(0);
        }

        BufferedImage inImg;
        double startTime;

        try {
            inImg = ImageIO.read(new File(lineParaArr[0]+".jpg"));



            int height = inImg.getHeight();
            int width = inImg.getWidth();
            System.out.println("h = "+height +" and w ="+width);

            BufferedImage outImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            //Smoothing the image with sliding window approach
            startTime = System.currentTimeMillis();
            for (int i = 0; i<height-winSize; i++){

                int[] pix = new int[winSize*winSize];
                int[] pixB = new int[winSize*winSize];
                int[] pixR = new int[winSize*winSize];
                int[] pixG = new int[winSize*winSize];

                for (int j = 0; j<width-winSize; j++){

                    int avg = 0;
                    int avgB = 0;
                    int avgR = 0;
                    int avgG = 0;

                    for (int k=0; k<winSize; k++){

                        for (int n=0; n<winSize; n++){

                            // int pixel = inImg.getRGB(i+k, j+n);

                            //avg = avg + pixel;
                            //pix[k+n] = pixel;
                            //  System.out.println(i +" + "+k +" + "+n);

                            // Color color = new Color(inImg.getRGB(i+k, j+n));
                            Color color = new Color(inImg.getRGB( j+n, i+k));
                            //  int rgb = inImg.getRGB( j+n, i+k);

                            int blue = color.getBlue();
                            //int blue = rgb & 0xff;
                            pixB[k+n] = blue;
                            avgB = avgB + blue;
                            int red = color.getRed();
                            //  int red = (rgb>>16) & 0xff;;
                            pixR[k+n] = red;
                            avgR = avgR + red;
                            int green = color.getGreen();
                            // int green = (rgb>>8) & 0xff;;
                            pixG[k+n] = green;
                            avgG = avgG + green;


                            //  inImg.getRaster().getPixel(x, y, iArray)

                        }


                    }
                    // int setPix = avg/pix.length;

                    int setB = avgB/pixB.length;
                    int setR = avgR/pixR.length;
                    int setG = avgG/pixG.length;
                    //    System.out.println("aveRGB = "+ avgR+","+avgB+","+avgG+","+ "  set ="+setR+","+setG+","+setB);
                    Color pixRGB = new Color(setR, setG, setB);

                    outImg.setRGB(j, i, pixRGB.getRGB());
                }

            }

            double endTime = System.currentTimeMillis();

            double exeTime =  endTime - startTime;
            System.out.println("\nTime taken = " + exeTime);
            ImageIO.write(outImg, "jpeg", new File(lineParaArr[1]+".jpg"));

        } catch (IIOException e) {
            System.out.println("\nIncorrect input Image name entered.\nProgramme ended.");
            System.exit(0);
        }


    }

}
