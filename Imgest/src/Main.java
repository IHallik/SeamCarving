import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

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

    public Image valued2(Image image) {
        PixelReader pixelReader = image.getPixelReader();

        // Create WritableImage
        WritableImage wImage = new WritableImage(
                (int) image.getWidth(),
                (int) image.getHeight());
        PixelWriter pixelWriter = wImage.getPixelWriter();

        // getting our important
        Color color0 = Color.color(0.5, 0.5, 0.5);
        for (int readX = 0; readX < image.getWidth(); readX++) {
            for (int readY = 0; readY < image.getHeight(); readY++) {
                Color color = pixelReader.getColor(readX, readY);
                Color newColor = deferenceCalculator(color0, color);
                pixelWriter.setColor(readX, readY, newColor);
                color0 = color;
            }
        }
        return wImage;
    }

    @Override
    public void start(Stage primaryStage) throws IOException, URISyntaxException {
        String f = null;
        if (true) {
            f = "test.png"; // takes file from src folder
        } else {
            f = "test_large.png"; // takes file from src folder
        }

        int seams = -10;
        int axis = 2;

        Image image = new Image(f);
        System.out.println("image loaded");

        ImageView imageView = new ImageView();
        imageView.setImage(image);
        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, image.getWidth(), image.getHeight());
        primaryStage.setTitle("Thing");
        primaryStage.setScene(scene);
        //primaryStage.setResizable(false);
        primaryStage.show();
        int squareHelper = (int) (image.getWidth() - image.getHeight());
        int dwith = (int) (image.getWidth() * 2 - image.getHeight());



        Image valued = valued(image);
        Boolean[][] ignore = new RemoveMatrix(valued, seams, axis).getRemoveMatrix();
        System.out.println("valued calculated");
        image = new Resize(image, ignore, seams, axis).getImage();
        System.out.println("seams removed");
        //image = valued(image);
        imageView.setImage(image);
        primaryStage.show();
        System.out.println("image displayed");

        // save result
        String format = "png";
        File file = new File("out.png");
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, file);
        System.out.println("save done");
    }

    public static void main(String[] args) {
        launch(args);
    }
}