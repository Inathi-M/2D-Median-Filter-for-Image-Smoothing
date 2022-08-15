import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Color;

public class MedianFilterSerial {

    public static void SmoothFilter(int height, int width, BufferedImage image, BufferedImage finalImage, int squareSize) {

        for (int y = 0; y < height - squareSize; y++) {

            int[] A = new int[squareSize * squareSize];
            int[] B = new int[squareSize * squareSize];
            int[] R = new int[squareSize * squareSize];
            int[] G = new int[squareSize * squareSize];

            for (int x = 0; x < width - squareSize; x++) {

                int avgB = 0;
                int avgR = 0;
                int avgG = 0;

                for (int a = 0; a < squareSize; a++) {

                    for (int n = 0; n < squareSize; n++) {
                        Color color = new Color(image.getRGB(x + n, y + a));

                        int blue = color.getBlue();
                        B[a + n] = blue;
                        avgB = avgB + blue;

                        int red = color.getRed();
                        R[a + n] = red;
                        avgR = avgR + red;

                        int green = color.getGreen();
                        G[a + n] = green;
                        avgG = avgG + green;
                    }

                }

                //Sort Arrays
                Arrays.sort(B);
                Arrays.sort(R);
                Arrays.sort(G);

                int setB = B[(int) Math.ceil(B.length / squareSize)];
                int setR = R[(int) Math.ceil(R.length / squareSize)];
                int setG = G[(int) Math.ceil(G.length / squareSize)];

                Color RGB = new Color(setR, setG, setB);

                finalImage.setRGB(x, y, RGB.getRGB());
            }

        }
    }

    public static void main(String[] args) throws IOException {

        Scanner input = new Scanner(System.in);

        System.out.println("Please enter command-line parameters (in that order): \n<inputImageName> <outputImageName> <windowWidth>\n");

        String inputString = input.nextLine(); input.close();
        String[] arrInput = inputString.split(" ");

        int squareSize = 0;

        try {
            if (Integer.parseInt(arrInput[2])%2 == 0 || Integer.parseInt(arrInput[2]) < 3){
                System.out.println("\nIncorrect window size entered.\nProgramme ended.");
                System.exit(0);
            }

            squareSize = Integer.parseInt(arrInput[2]);

        } catch (NumberFormatException e) {
            System.out.println("\nDid not enter a number.\nProgramme ended.");
            System.exit(0);
        }

        BufferedImage bImage;
        double startTime;

        try {
            bImage = ImageIO.read(new File(arrInput[0]+".jpg"));

            int height = bImage.getHeight();
            int width = bImage.getWidth();

            BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            //Smoothing the image with sliding window approach
            startTime = System.currentTimeMillis();
          SmoothFilter(height,width,bImage,finalImage,squareSize);

            double endTime = System.currentTimeMillis();

            double exeTime =  endTime - startTime;
            System.out.println("\nTime taken = " + exeTime);
            ImageIO.write(finalImage, "jpeg", new File(arrInput[1]+".jpg"));

        } catch (IIOException e) {
            System.out.println("\nIncorrect input Image name entered.\nProgramme ended.");
            System.exit(0);
        }

    }
}
