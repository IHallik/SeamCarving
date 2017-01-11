import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

// http://codesquire.com/post/GrayScaleJava
public class Grayscale {
    private BufferedImage input;
    private String inputName;

    public Grayscale(String file) throws URISyntaxException, IOException {
        this.input = ImageIO.read(new File(this.getClass().getResource(file).toURI()));
        this.inputName = file;
    }

    public void toGray() {
        BufferedImage output = new BufferedImage(this.input.getWidth(), this.input.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < this.input.getWidth(); i++) {
            for (int j = 0; j < this.input.getHeight(); j++) {
                Color c = new Color(this.input.getRGB(i, j));
                int red = (int) (c.getRed() * 0.21);
                int blue = (int) (c.getBlue() * 0.72);
                int green = (int) (c.getGreen() * 0.07);
                int sum = red + blue + green;
                Color newColor = new Color(sum, sum, sum);
                output.setRGB(i, j, newColor.getRGB());
            }
        }


        File output2 = new File("grayscale-" + this.inputName);
        try {
            ImageIO.write(output, "png", output2);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
