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
            bImage = ImageIO.read(new File(arrString[0]+".jpg"));
            BufferedImage finalImage = new BufferedImage(bImage.getWidth(), bImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            WindowSquare window = new WindowSquare(bImage.getHeight(), bImage.getWidth(), squareSize, bImage, finalImage, 0, squareSize, -1, bImage.getWidth());
            //new ForkJoinPool();
            ForkJoinPool pool = ForkJoinPool.commonPool();

            startTime = System.currentTimeMillis();
            pool.invoke(window);
            double endTime = System.currentTimeMillis();

            double exeTime =  endTime - startTime;
            System.out.println("\nTime taken = " + exeTime);


            try {
                ImageIO.write(finalImage, "jpeg", new File(arrString[1]+".jpg"));
            } catch (IOException e) {
                System.out.println("Image generation error");
            }
        } catch (IIOException e) {
            System.out.println("\nIncorrect input Image name entered.\nProgramme ended.");
            System.exit(0);
        }

    }


}

class WindowSquare extends RecursiveAction{
    protected int height;
    protected int width;
    protected int winSize;
    protected int startHeight;
    protected int endHeight;
    protected int startWidth;
    protected int endWidth;
    protected BufferedImage bImage;
    protected BufferedImage finalImage;

    int cutoff= 100000;

    protected int thresholdH=0;
    protected int thresholdW=0;


    public WindowSquare(int height, int width, int sqrSize, BufferedImage image ,BufferedImage finalImage, int startHeight, int endHeight, int startWidth, int endWidth){//, int start, int end){
        this.height=height;
        this.width=width;
        this.winSize=sqrSize;
        this.finalImage=finalImage;
        this.bImage=image;
        this.startHeight=startHeight;
        this.endHeight=endHeight;
        this.startWidth=startWidth;
        this.endWidth=endWidth;
    }
    // threshold window size
    @Override
    protected void compute() {
        int threshold = 2;

        int midpoint = (int) Math.ceil(height/2);
        int midWe = (int) Math.ceil(height/2);

        int kn = (int) Math.floor((height)/winSize) +startHeight;
        int kn1 = (int) Math.floor((width)/winSize) +endWidth;
        cutoff=height-kn;

        if( startWidth!=-1){

            Filter(kn-kn, kn, kn1-kn1, kn1);
            System.out.println(midpoint+" "+height);
        }
        else{

            endHeight=kn+endHeight;

            startWidth=0;
            WindowSquare winLeft = new WindowSquare(height, width, winSize, bImage, finalImage, 0, midpoint, startWidth, width);
            WindowSquare winRight = new WindowSquare(height, width, winSize, bImage, finalImage, midpoint, height-winSize, startWidth, endWidth);
            invokeAll(winLeft, winRight);
        }

    }
    public void Filter(int start, int end, int weSt, int weEn){

        int midpoint = (int) Math.ceil(height/2);
        int kn = (int) Math.floor((height)/winSize) +startHeight;

        for (int i = startHeight; i<endHeight; i++){

            int[] A = new int[winSize*winSize];
            int[] B = new int[winSize*winSize];
            int[] R = new int[winSize*winSize];
            int[] G = new int[winSize*winSize];

            for (int j = 0; j<width-winSize; j++){

                int sumAlpha = 0;
                int sumB = 0;
                int sumR = 0;
                int sumG = 0;

                for (int k=0; k<winSize; k++){

                    for (int n=0; n<winSize; n++){

                        // Color color = new Color(inImg.getRGB(i+k, j+n));
                        Color color = new Color(bImage.getRGB( j+n, i+k));

                        int blue = color.getBlue();
                        B[k+n] = blue;
                        sumB = sumB + blue;
                        int red = color.getRed();
                        R[k+n] = red;
                        sumR = sumR + red;
                        int green = color.getGreen();
                        G[k+n] = green;
                        sumG = sumG + green;

                    }


                }

                int setB = sumB/B.length;
                int setR = sumR/R.length;
                int setG = sumG/G.length;

                Color pixRGB = new Color(setR, setG, setB);

                finalImage.setRGB(j, i, pixRGB.getRGB());
            }

        }

    }

}
