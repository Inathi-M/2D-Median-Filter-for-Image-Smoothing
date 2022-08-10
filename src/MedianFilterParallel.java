import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Color;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

public class MedianFilterParallel {

    public static void main(String args[]) throws IOException {

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
            BufferedImage outImg = new BufferedImage(inImg.getWidth(), inImg.getHeight(), BufferedImage.TYPE_INT_RGB);

            Window window = new Window(inImg.getHeight(), inImg.getWidth(), winSize, inImg, outImg, 0, winSize, -1, inImg.getWidth());

            //new ForkJoinPool();
            ForkJoinPool pool = ForkJoinPool.commonPool();

            startTime = System.currentTimeMillis();
            pool.invoke(window);

            double endTime = System.currentTimeMillis();

            double exeTime =  endTime - startTime;
            System.out.println("\nTime taken = " + exeTime);

            try {
                // ImageIO.write(outImg, "jpeg", new File("C:/Users/dwebb/Desktop/UCT/CSC2002S/Assignments/ass1/Assignment working/test2.jpg"));
                ImageIO.write(outImg, "jpeg", new File(lineParaArr[1]+".jpg"));
            } catch (IOException e) {
                System.out.println("Image generation error");
            }
        } catch (IIOException e) {
            System.out.println("\nIncorrect input Image name entered.\nProgramme ended.");
            System.exit(0);
        }

    }


}

class Window2 extends RecursiveAction{

    protected int height;
    protected int width;
    protected int winSize;
    protected int startH;
    protected int endH;
    protected int startW;
    protected int endW;
    protected BufferedImage outImg;
    protected BufferedImage inImg;

    int hx= 100000;

    protected int thresholdH=0;
    protected int thresholdW=0;

    public Window2(int height, int width, int winSize, BufferedImage inImg ,BufferedImage outImg, int startH, int endH, int startW, int endW){

        this.height=height;
        this.width=width;
        this.winSize=winSize;
        this.outImg=outImg;
        this.inImg=inImg;
        this.startH=startH;
        this.endH=endH;
        this.startW=startW;
        this.endW=endW;
    }

    @Override
    protected void compute() {

        int threshold = 2;

        int midpoint = (int) Math.ceil(height/2);
        int midWe = (int) Math.ceil(height/2);

        int kn = (int) Math.floor((height)/winSize) +startH;
        int kn1 = (int) Math.floor((width)/winSize) +endW;
        hx=height-kn;

        if( startW!=-1){

            Smooth();
            System.out.println(midpoint+" "+height);
        }
        else{

            endH=kn+endH;
            //   startH=startH+kn;

            startW=0;
            Window winLeft = new Window(height, width, winSize, inImg, outImg, 0, midpoint, startW, width);
            Window winRight = new Window(height, width, winSize, inImg, outImg, midpoint, height-winSize, startW, endW);
            invokeAll(winLeft, winRight);
        }


    }

    public void Smooth(){

        int midpoint = (int) Math.ceil(height/2);
        int kn = (int) Math.floor((height)/winSize) +startH;

        //Smoothing the image with sliding window approach
        for (int i = startH; i<endH; i++){

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


                        int blue = color.getBlue();
                        pixB[k+n] = blue;
                        avgB = avgB + blue;
                        int red = color.getRed();
                        pixR[k+n] = red;
                        avgR = avgR + red;
                        int green = color.getGreen();
                        pixG[k+n] = green;
                        avgG = avgG + green;


                        //  inImg.getRaster().getPixel(x, y, iArray)

                    }


                }
                // int setPix = avg/pix.length;

                //Sort Arrays
                Arrays.sort(pixB);
                Arrays.sort(pixR);
                Arrays.sort(pixG);

                int posB = (int) Math.ceil(pixB.length / winSize);
                int posR = (int) Math.ceil(pixR.length / winSize);
                int posG = (int) Math.ceil(pixG.length / winSize);

                int setB = pixB[posB];
                int setR = pixR[posR];
                int setG = pixG[posG];
                //    System.out.println("aveRGB = "+ avgR+","+avgB+","+avgG+","+ "  set ="+setR+","+setG+","+setB);
                Color pixRGB = new Color(setR, setG, setB);

                outImg.setRGB(j, i, pixRGB.getRGB());
            }

        }

    }


}
