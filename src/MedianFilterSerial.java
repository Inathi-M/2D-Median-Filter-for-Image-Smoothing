import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MedianFilterSerial {
    private static int[] pixels;
    public static void main(String[] args) {


        BufferedImage bImage = null;
        File imageFile = null;
        int buff[];
        int maskSize = 9;

        //read image
        try {
            imageFile = new File("C:\\UCT\\CSC2002S\\Assignments\\Assignment 1\\EiffelTower.jpg");
            bImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.out.println(e);
        }

        pixels = new int[bImage.getWidth() * bImage.getHeight()];


        //image dimension
        int width = bImage.getWidth();
        int height = bImage.getHeight();

        int outputPixels[] = new int[width * height];


        /**
         * red, green and blue are a 2D square of odd size like 3x3, 5x5, 7x7, ...
         * For simplicity storing it into 1D array.
         */
        int red[], green[], blue[];

        /** Median Filter operation */
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int alpha = (pixels[x + (y * width)] >> 24) & 0xFF;
                red = new int[maskSize * maskSize];
                green = new int[maskSize * maskSize];
                blue = new int[maskSize * maskSize];
                int count = 0;
                for (int r = y - (maskSize / 2); r <= y + (maskSize / 2); r++) {
                    for (int c = x - (maskSize / 2); c <= x + (maskSize / 2); c++) {
                        if (r < 0 || r >= height || c < 0 || c >= width) {
                            /** Some portion of the mask is outside the image. */
                            continue;
                        } else {
                            red[count] = (pixels[c + (r * width)] >> 16) & 0xFF;
                            green[count] = (pixels[c + (r * width)] >> 8) & 0xFF;
                            blue[count] = pixels[c + (r * width)] & 0xFF;
                            count++;
                        }
                    }
                }

                /** sort red, green, blue array */
                java.util.Arrays.sort(red);
                java.util.Arrays.sort(green);
                java.util.Arrays.sort(blue);

                /** save median value in outputPixels array */
                int index = (count % 2 == 0) ? count / 2 - 1 : count / 2;
                int p = (alpha << 24) | (red[index] << 16) | (green[index] << 8) | blue[index];
                outputPixels[x + y * width] = p;
            }
        }

        /** Write the output pixels to the image pixels */
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[x + (y * width)] = outputPixels[x + y * width];
                bImage.setRGB(x, y, pixels[x + (y * width)]);
            }
        }

        //  }
        //write image
        try {
            imageFile = new File("Output.jpg");
            ImageIO.write(bImage, "jpg", imageFile);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
