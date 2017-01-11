import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

// http://stackoverflow.com/questions/30482118/java-image-processing-sobel-edge-detection
// http://homepages.inf.ed.ac.uk/rbf/HIPR2/sobel.htm
public class Gradient {
    private BufferedImage input;
    private String inputName;
    private int[][] sobel_operator = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    private static int[][] pixelMatrix = new int[3][3];

    public Gradient(String inputFile) throws URISyntaxException, IOException {
        String[] bits = inputFile.split("-");
        this.inputName = bits[bits.length - 1];
        this.input = ImageIO.read(new File(this.getClass().getResource(inputFile).toURI()));
    }

    public void convert() {
        BufferedImage output = new BufferedImage(this.input.getWidth(), this.input.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 1; i < this.input.getWidth() - 1; i++) {
            for (int j = 1; j < this.input.getHeight() - 1; j++) {
                pixelMatrix[0][0] = new Color(this.input.getRGB(i - 1, j - 1)).getRed();
                pixelMatrix[0][1] = new Color(this.input.getRGB(i - 1, j)).getRed();
                pixelMatrix[0][2] = new Color(this.input.getRGB(i - 1, j + 1)).getRed();

                pixelMatrix[1][0] = new Color(this.input.getRGB(i, j - 1)).getRed();
                pixelMatrix[1][1] = new Color(this.input.getRGB(i, j)).getRed();
                pixelMatrix[1][2] = new Color(this.input.getRGB(i, j + 1)).getRed();

                pixelMatrix[2][0] = new Color(this.input.getRGB(i + 1, j - 1)).getRed();
                pixelMatrix[2][1] = new Color(this.input.getRGB(i + 1, j)).getRed();
                pixelMatrix[2][2] = new Color(this.input.getRGB(i + 1, j + 1)).getRed();

                int edge = (int) convolution(pixelMatrix);
                output.setRGB(i, j, (edge << 16 | edge << 8 | edge));

            }
        }

        System.out.println("tereee");

        File filename = new File("gradient-" + this.inputName);
        try {
            ImageIO.write(output, "png", filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private double convolution(int[][] matrix) {
        int gx = matrix[0][0] * sobel_operator[0][0] + matrix[0][2] * sobel_operator[0][2] + matrix[1][0] *
                sobel_operator[1][0] + matrix[1][2] * sobel_operator[1][2] + matrix[2][0] * sobel_operator[2][0] +
                matrix[2][2] * sobel_operator[2][2];
        int gy = matrix[0][0] * sobel_operator[0][2] + matrix[0][1] * sobel_operator[1][2] + matrix[0][2] *
                sobel_operator[2][2] + matrix[2][0] * sobel_operator[0][0] + matrix[2][1] * sobel_operator[1][0] +
                matrix[2][2] * sobel_operator[2][0];

        return Math.sqrt(Math.pow(gx, 2) + Math.pow(gy, 2));
    }
}