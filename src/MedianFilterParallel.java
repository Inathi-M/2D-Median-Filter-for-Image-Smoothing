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

            Window2 window = new Window2(inImg.getHeight(), inImg.getWidth(), winSize, inImg, outImg, 0, winSize, -1, inImg.getWidth());

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
    protected int squareSize;

    protected int startHeight;

    protected int endHeight;

    protected int startWidth;

    protected int endWidth;
    protected BufferedImage bImage;
    protected BufferedImage finalImage;

    int hx= 100000;

    protected int thresholdH=0;
    protected int thresholdW=0;

    public Window2(int height, int width, int sqrSize, BufferedImage image ,BufferedImage finalImage, int startH, int endH, int startW, int endW){

        this.height=height;
        this.width=width;
        this.squareSize=sqrSize;
        this.finalImage=finalImage;
        this.bImage=image;
        this.startHeight=startH;
        this.endHeight=endH;
        this.startWidth=startW;
        this.endWidth=endW;
    }

    @Override
    protected void compute() {

       // int threshold = 2;

        int midpoint = (int) Math.ceil(height/2);
        int midWe = (int) Math.ceil(height/2);

        int kn = (int) Math.floor((height)/squareSize) +startHeight;
        int kn1 = (int) Math.floor((width)/squareSize) +endWidth;
        hx=height-kn;

        if( startWidth!=-1){

            Smooth();
            System.out.println(midpoint+" "+height);
        }
        else{

            endHeight=kn+ endHeight;
            //   startH=startH+kn;

            startWidth=0;
            WindowSquare winLeft = new WindowSquare(height, width, squareSize, bImage, finalImage, 0, midpoint, startWidth, width);
            WindowSquare winRight = new WindowSquare(height, width, squareSize, bImage, finalImage, midpoint, height-squareSize, startWidth, endWidth);
            invokeAll(winLeft, winRight);
        }


    }

    public void Smooth(){

        int midpoint = (int) Math.ceil(height/2);
        int kn = (int) Math.floor((height)/squareSize) +startHeight;

        //Smoothing the image with sliding window approach
        for (int i = startHeight; i<endHeight; i++){

            int[] pix = new int[squareSize*squareSize];
            int[] pixB = new int[squareSize*squareSize];
            int[] pixR = new int[squareSize*squareSize];
            int[] pixG = new int[squareSize*squareSize];


                for (int j = 0; j<bImage.getWidth(); j++){

                    int avg = 0;
                    int avgB = 0;
                    int avgR = 0;
                    int avgG = 0;

                    if (j < bImage.getWidth()-squareSize){

                        for (int k=0; k<squareSize; k++){

                            for (int n=0; n<squareSize; n++){

                                int pixel = bImage.getRGB( j+n, i+k);

                                int alpha = (pixel>>24) & 0xff;
                                pix[k+n]= alpha;
                                avg = avg+ alpha;

                                int blue = pixel & 0xff;
                                pixB[k+n] = blue;
                                avgB = avgB + blue;
                                int red = (pixel>>16) & 0xff;
                                pixR[k+n] = red;
                                avgR = avgR + red;
                                int green = (pixel>>8) & 0xff;
                                pixG[k+n] = green;
                                avgG = avgG + green;

                            }


                        }

                    }

                    else{

                        for (int k=0; k<squareSize; k++){

                            for (int n=squareSize; n>0; n--){

                                int pixel = bImage.getRGB( j-n, i+k);

                                int alpha = (pixel>>24) & 0xff;
                                pix[k+n]= alpha;
                                avg = avg+ alpha;

                                int blue = pixel & 0xff;
                                pixB[k+n] = blue;
                                avgB = avgB + blue;
                                int red = (pixel>>16) & 0xff;
                                pixR[k+n] = red;
                                avgR = avgR + red;
                                int green = (pixel>>8) & 0xff;
                                pixG[k+n] = green;
                                avgG = avgG + green;


                            }


                        }

                    }

                    //Sort Arrays
                    Arrays.sort(pixB);
                    Arrays.sort(pixR);
                    Arrays.sort(pixG);
                    Arrays.sort(pix);

                    int posB = (int) Math.ceil(pixB.length / squareSize);
                    int posR = (int) Math.ceil(pixR.length / squareSize);
                    int posG = (int) Math.ceil(pixG.length / squareSize);
                    int posAl = (int) Math.ceil(pix.length /squareSize);

                    int setB = pixB[posB];
                    int setR = pixR[posR];
                    int setG = pixG[posG];
                    int setAl = pix[posAl];

                    int setColour = 0;

                    setColour = setColour | (setAl<<24);
                    setColour = setColour | (setR<<16);
                    setColour = setColour | (setG<<8);
                    setColour = setColour | setB;

                    finalImage.setRGB(j, i, setColour);

                }

            } // i end

            }

}






