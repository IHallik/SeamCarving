import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
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


        Double shortestPathLength = Double.POSITIVE_INFINITY;
        Integer[] shortestPath = new Integer[(int) image.getHeight()];
        Image valuedImg = valued(image);
        PixelReader pixelReader = valuedImg.getPixelReader();
        for (int i = 0; i < image.getWidth(); i++) {
            Integer[] path = new Integer[(int) image.getHeight()];
            Double total = 0.0;
            int last = i;

            path[0] = last;
            for (int j = 1; j < image.getHeight(); j++) {
                int next = -1;
                double nextVal = Double.POSITIVE_INFINITY;
                // look down left
                if (last - 1 > 0) {
                    if (colValSum(pixelReader.getColor(last - 1, j)) < nextVal) {
                        next = last - 1;
                        nextVal = colValSum(pixelReader.getColor(next, j));
                    }
                }
                // look down
                if (colValSum(pixelReader.getColor(last, j)) < nextVal) {
                    next = last;
                    nextVal = colValSum(pixelReader.getColor(next, j));
                }
                // look down right
                if (last + 1 < image.getWidth()) {
                    if (colValSum(pixelReader.getColor(last + 1, j)) < nextVal) {
                        next = last + 1;
                        nextVal = colValSum(pixelReader.getColor(next, j));
                    }
                }

                total += nextVal;
                path[j] = next;
                last = next;
            }
            if (total < shortestPathLength) {
                shortestPathLength = total;
                shortestPath = path;
            }
        }
        /*

        //Initial test code to see path in red

        WritableImage wImage = new WritableImage(
                (int) image.getWidth(),
                (int) image.getHeight());
        PixelWriter pixelWriter = wImage.getPixelWriter();
        for (int readY = 0; readY < image.getHeight(); readY++) {
            for (int readX = 0; readX < image.getWidth(); readX++) {
                pixelWriter.setColor(readX, readY, pixelReader.getColor(readX,readY));
            }
        }
        for (int i = 0; i < image.getHeight(); i++) {
            pixelWriter.setColor(shortestPath[i], i, Color.RED);
        }
        return wImage;
        */
        return shortestPath;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        // Create Image and ImageView objects
        String f = "test3.png"; // takes file from src folder
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

        for (int i = 0; i < 150; i++) {
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
            imageView.setImage(image);
            primaryStage.show();
        }

        // save result
        String format = "png";
        File file = new File("out.png");
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, file);

    }

    public static void main(String[] args) {
        launch(args);
    }
}