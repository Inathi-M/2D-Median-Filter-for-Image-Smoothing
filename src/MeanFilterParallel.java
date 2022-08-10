import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Color;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

public class MeanFilterParallel {

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

class Window extends RecursiveAction{
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


    public Window(int height, int width, int winSize, BufferedImage inImg ,BufferedImage outImg, int startH, int endH, int startW, int endW){//, int start, int end){
        this.height=height;
        this.width=width;
        this.winSize=winSize;
        this.outImg=outImg;
        this.inImg=inImg;
        this.startH=startH;
        this.endH=endH;
        this.startW=startW;
        this.endW=endW;
        // this.start=start;
        // this.end=end;
    }
    // threshold window size
    @Override
    protected void compute() {
        int threshold = 2;

        int midpoint = (int) Math.ceil(height/2);
        int midWe = (int) Math.ceil(height/2);

        int kn = (int) Math.floor((height)/winSize) +startH;
        int kn1 = (int) Math.floor((width)/winSize) +endW;
        hx=height-kn;

        if( startW!=-1){

            Filter(kn-kn, kn, kn1-kn1, kn1);
            System.out.println(midpoint+" "+height);
        }
        else{

            endH=kn+endH;
            //   startH=startH+kn;

            startW=0;
            Window winLeft = new Window(height, width, winSize, inImg, outImg, 0, midpoint, startW, width);
            Window winRight = new Window(height, width, winSize, inImg, outImg, midpoint, height-winSize, startW, endW);
            invokeAll(winLeft, winRight);
            //winRight.fork();
            // winLeft.compute();
            //winRight.compute();
            // winRight.join();
        }

        /*
        if (height < threshold){
           Filter(start, end);
        }
        else{

        int midpoint = (int) Math.ceil(height/2);
        Window winLeft = new Window(height, width, winSize, inImg, outImg, 0, midpoint);
        Window winRight = new Window(height, width, winSize, inImg, outImg, midpoint+1, height);

        winLeft.fork();
        winRight.compute();
        winLeft.join();
       }
       */
    }



    public void Filter(int start, int end, int weSt, int weEn){

        int midpoint = (int) Math.ceil(height/2);
        int kn = (int) Math.floor((height)/winSize) +startH;

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

                    }


                }

                int setB = avgB/pixB.length;
                int setR = avgR/pixR.length;
                int setG = avgG/pixG.length;

                Color pixRGB = new Color(setR, setG, setB);

                outImg.setRGB(j, i, pixRGB.getRGB());
            }

        }

    }


}
