import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main extends Application {

    public Color deferenceCalculator(Color c1, Color c2) {
        c1 = c1.grayscale();
        c2 = c2.grayscale();
        Color c = Color.color(Math.abs(c1.getRed() - c2.getRed()),
                Math.abs(c1.getGreen() - c2.getGreen()),
                Math.abs(c1.getBlue() - c2.getBlue()));
        /*rgb*/
        return c;
    }

    public double colValSum(Color c) {
        return c.getBlue() + c.getGreen() + c.getRed();
    }

    public Image valued(Image image) {
        PixelReader pixelReader = image.getPixelReader();

        // Create WritableImage
        WritableImage wImage = new WritableImage(
                (int) image.getWidth(),
                (int) image.getHeight());
        PixelWriter pixelWriter = wImage.getPixelWriter();

        // getting our important
        Color color0 = Color.color(0.5, 0.5, 0.5);
        for (int readY = 0; readY < image.getHeight(); readY++) {
            for (int readX = 0; readX < image.getWidth(); readX++) {
                Color color = pixelReader.getColor(readX, readY);
                Color newColor = deferenceCalculator(color0, color);
                pixelWriter.setColor(readX, readY, newColor);
                color0 = color;
            }
        }

        return wImage;
    }

    public Integer[] minPath(Image image) {
        Integer[][] pathMatrix = new Integer[(int) image.getWidth()][(int) image.getHeight()];
        Double[] currentVal = new Double[(int) image.getWidth()];
        Double[] oldVal = new Double[(int) image.getWidth()];

        Image valuedImg = valued(image);
        PixelReader pixelReader = valuedImg.getPixelReader();

        for (int i = 0; i < oldVal.length; i++) {
            oldVal[i] = colValSum(pixelReader.getColor(i,0));
        }

        for (int height = 0; height < image.getHeight(); height++) {

            for (int width = 0; width < image.getWidth(); width++) {
                int next = Integer.MAX_VALUE;
                double nextVal = Double.POSITIVE_INFINITY;
                // look up left
                if (width - 1 > 0) {
                    if (oldVal[width - 1] < nextVal) {
                        next = -1;
                        nextVal = oldVal[width - 1];
                    }
                }

                // look up
                if (oldVal[width] < nextVal) {
                    next = 0;
                    nextVal = oldVal[width];

                }

                // look up right
                if (width + 1 < image.getWidth()) {
                    if (oldVal[width + 1] < nextVal) {
                        next = 1;
                        nextVal = oldVal[width + 1];
                    }
                }

                // updating current
                currentVal[width] = nextVal + colValSum(pixelReader.getColor(width, height));
                // updating path matrix
                pathMatrix[width][height] = next;
            }
            oldVal = currentVal.clone();
        }

        // find lowest value
        int minI = -1;
        double minVal = Integer.MAX_VALUE;
        for (int i = 0; i < currentVal.length; i++) {
            if (currentVal[i]<minVal){
                minI = i;
                minVal = currentVal[i];
            }
        }

        // find the path
        Integer[] shortestPath = new Integer[(int) image.getHeight()];

        for (int i = (int) image.getHeight()-1; i >= 0; i--) {
            shortestPath[i] = minI;
            minI += pathMatrix[minI][i];
        }

        return shortestPath;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        // Create Image and ImageView objects
        String f = "test_large.png"; // takes file from src folder
        // using "file:../test-png" causes some problems with getting variables
        Image image = new Image(f);
        //Image image = new Image(getClass().getResourceAsStream("a/test.png"));
        ImageView imageView = new ImageView();
        imageView.setImage(image);

        // Display image on screen
        StackPane root = new StackPane();
        root.getChildren().add(imageView);


        Scene scene = new Scene(root, image.getWidth(), image.getHeight());
        primaryStage.setTitle("Thing");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        long startTime = System.nanoTime();
        for (int i = 0; i < image.getWidth()-image.getHeight(); i++) {
            PixelReader pixelReader = image.getPixelReader();

            Integer[] remove = minPath(image);
            WritableImage up = new WritableImage(
                    (int) image.getWidth() - 1,
                    (int) image.getHeight());

            PixelWriter pixelWriter = up.getPixelWriter();

            for (int readY = 0; readY < image.getHeight(); readY++) {
                int shift = 0;
                for (int readX = 0; readX < image.getWidth() - 1; readX++) {
                    if (remove[readY] == readX) shift = 1;
                    pixelWriter.setColor(readX, readY, pixelReader.getColor(readX + shift, readY));
                }
            }
            image = up;
        }
        long endTime = System.nanoTime();
        System.out.println((endTime - startTime)/1000000);
        System.out.println("save");
        imageView.setImage(image);
        primaryStage.show();
        // save result
        String format = "png";
        File file = new File("out.png");
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, file);

    }

    public static void main(String[] args) {
        launch(args);
    }
}