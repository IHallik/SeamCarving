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
import java.util.stream.IntStream;

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

    public Color colValAvg(Color c1, Color c2) {
        Color c = Color.color(
                (c1.getRed() + c2.getRed()) / 2.0,
                (c1.getGreen() + c2.getGreen()) / 2.0,
                (c1.getBlue() + c2.getBlue()) / 2.0
        );
        return c;
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

    public Integer[] minPath2(Image image) {
        Integer[][] pathMatrix = new Integer[(int) image.getWidth()][(int) image.getHeight()];
        Double[] dynamic = new Double[(int) image.getWidth() + 2];

        PixelReader pixelReader = image.getPixelReader();

        for (int i = 0; i < dynamic.length - 2; i++) {
            dynamic[i] = colValSum(pixelReader.getColor(i, 0));
        }
        for (int height = 0; height < image.getHeight(); height++) {

            // shift array ->
            for (int i = dynamic.length - 1; i > 0; i--) {
                dynamic[i] = dynamic[i - 1];
            }
            dynamic[0] = Double.POSITIVE_INFINITY;
            dynamic[dynamic.length - 1] = Double.POSITIVE_INFINITY;

            for (int width = 0; width < image.getWidth(); width++) {
                int next = Integer.MAX_VALUE;
                double nextVal = Double.POSITIVE_INFINITY;
                for (int i = 0; i < 3; i++) {
                    if (dynamic[width + i] < nextVal) {
                        nextVal = dynamic[width + i];
                        next = i - 1;
                    }
                }

                dynamic[width] = nextVal + colValSum(pixelReader.getColor(width, height));
                // updating path matrix
                pathMatrix[width][height] = next;
            }
        }

        // find lowest value
        int minI = -1;
        double minVal = Integer.MAX_VALUE;
        for (int i = 0; i < dynamic.length - 2; i++) {
            if (dynamic[i] < minVal) {
                minI = i;
                minVal = dynamic[i];
            }
        }

        ///-----------------------------------------------------------------------
        // find the path
        Integer[] shortestPath = new Integer[(int) image.getHeight()];

        for (int i = (int) image.getHeight() - 1; i >= 0; i--) {
            shortestPath[i] = minI;
            minI += pathMatrix[minI][i];
        }

        return shortestPath;
    }

    public Integer[] minPath(Image image) {
        Integer[][] pathMatrix = new Integer[(int) image.getWidth()][(int) image.getHeight()];
        Double[] currentVal = new Double[(int) image.getWidth()];
        Double[] oldVal = new Double[(int) image.getWidth()];


        PixelReader pixelReader = image.getPixelReader();

        for (int i = 0; i < oldVal.length; i++) {
            oldVal[i] = colValSum(pixelReader.getColor(i, 0));
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
            if (currentVal[i] < minVal) {
                minI = i;
                minVal = currentVal[i];
            }
        }

        // find the path
        Integer[] shortestPath = new Integer[(int) image.getHeight()];

        for (int i = (int) image.getHeight() - 1; i >= 0; i--) {
            shortestPath[i] = minI;
            minI += pathMatrix[minI][i];
        }

        return shortestPath;
    }

    public Image resizeHelper(Image image, Integer seamesToRemoveX, Integer seamesToRemoveY, Boolean[][] ignoreMatrix, Boolean[][] ignoreMatrix2) {
        WritableImage resized = new WritableImage(
                (int) image.getWidth() - seamesToRemoveX,
                (int) image.getHeight());

        WritableImage resized2 = new WritableImage(
                (int) image.getWidth() - seamesToRemoveX,
                (int) image.getHeight() - seamesToRemoveY);


        PixelWriter pixelWriter = resized.getPixelWriter();
        PixelReader pixelReaderResize = image.getPixelReader();
        Integer xSize = (int) image.getWidth();
        Integer ySize = (int) image.getHeight();
        Boolean[][] ignoreMatrixHelper = new Boolean[xSize][ySize];
        IntStream
                .range(0, xSize - seamesToRemoveX)
                .parallel()
                .forEach(readY -> IntStream
                        .range(0, ySize)
                        .forEach(readX -> ignoreMatrixHelper[readX][readY] = Boolean.FALSE));


        for (int y = 0; y < ySize; y++) {
            int skip = 0;
            for (int x = 0; x < xSize - seamesToRemoveX; x++) {
                while (x + skip < xSize && ignoreMatrix[x + skip][y]) {
                    skip++;
                }
                pixelWriter.setColor(x, y, pixelReaderResize.getColor(x + skip, y));
                if (ignoreMatrix2[x + skip][y]) ignoreMatrixHelper[x][y] = true;
            }

                /*
                while (y + ySift[x] < image.getHeight() && ignoreMatrix2[x + xSift[y]][y + ySift[x]]) {
                    ySift[x]++;
                }
                */
        }
        PixelReader pixelReaderResize2 = resized.getPixelReader();
        pixelWriter = resized2.getPixelWriter();
        try {

            for (int x = 0; x < xSize - seamesToRemoveX; x++) {
                int skip = 0;
                for (int y = 0; y < ySize - seamesToRemoveY; y++) {
                    while (y + skip < ySize && ignoreMatrixHelper[x][y + skip]) {
                        skip++;
                    }
                    pixelWriter.setColor(x, y, pixelReaderResize2.getColor(x, y + skip));
                }
                System.out.println(skip);

                /*
                while (y + ySift[x] < image.getHeight() && ignoreMatrix2[x + xSift[y]][y + ySift[x]]) {
                    ySift[x]++;
                }
                */
            }
        } catch (Exception e) {
        }
        ;
        return image;
    }

    public Image resizeHelperX(Image image, Integer seamesToRemoveX, Boolean[][] ignoreMatrix) {
        WritableImage resized = new WritableImage(
                (int) image.getWidth() - seamesToRemoveX,
                (int) image.getHeight());

        PixelWriter pixelWriter = resized.getPixelWriter();
        PixelReader pixelReaderResize = image.getPixelReader();
        Integer xSize = (int) image.getWidth();
        Integer ySize = (int) image.getHeight();

        for (int y = 0; y < ySize; y++) {
            int skip = 0;
            for (int x = 0; x < xSize - seamesToRemoveX; x++) {
                while (x + skip < xSize && ignoreMatrix[x + skip][y]) {
                    skip++;
                }
                pixelWriter.setColor(x, y, pixelReaderResize.getColor(x + skip, y));
            }

                /*
                while (y + ySift[x] < image.getHeight() && ignoreMatrix2[x + xSift[y]][y + ySift[x]]) {
                    ySift[x]++;
                }
                */
        }

        return resized;
    }

    public Image resizeHelperX2(Image image, Integer seamesToRemoveX, Boolean[][] ignoreMatrix) {
        WritableImage resized = new WritableImage(
                (int) image.getWidth() + seamesToRemoveX,
                (int) image.getHeight());

        PixelWriter pixelWriter = resized.getPixelWriter();
        PixelReader pixelReaderResize = image.getPixelReader();
        Integer xSize = (int) image.getWidth();
        Integer ySize = (int) image.getHeight();

        for (int y = 0; y < ySize; y++) {
            int skip = 0;
            for (int x = 0; x < xSize; x++) {
                if (ignoreMatrix[x][y]) {
                    if (x == 0) {
                        pixelWriter.setColor(x + skip, y, pixelReaderResize.getColor(x, y));
                    } else{
                        pixelWriter.setColor(x + skip, y, colValAvg(pixelReaderResize.getColor(x - 1, y), pixelReaderResize.getColor(x, y)));
                    }
                    skip++;
                }
                pixelWriter.setColor(x + skip, y, pixelReaderResize.getColor(x, y));
            }
                /*
                while (y + ySift[x] < image.getHeight() && ignoreMatrix2[x + xSift[y]][y + ySift[x]]) {
                    ySift[x]++;
                }
                */
        }

        return resized;
    }

    public Image resizeHelperY(Image image, Integer seamesToRemoveY, Boolean[][] ignoreMatrix) {
        WritableImage resized = new WritableImage(
                (int) image.getWidth(),
                (int) image.getHeight() - seamesToRemoveY);

        PixelWriter pixelWriter = resized.getPixelWriter();
        PixelReader pixelReaderResize = image.getPixelReader();
        Integer xSize = (int) image.getWidth();
        Integer ySize = (int) image.getHeight();


        for (int x = 0; x < xSize; x++) {
            int skip = 0;
            for (int y = 0; y < ySize - seamesToRemoveY; y++) {
                while (y + skip < ySize && ignoreMatrix[x][y + skip]) {
                    skip++;
                }
                pixelWriter.setColor(x, y, pixelReaderResize.getColor(x, y + skip));
            }
        }
        return resized;
    }

    public Image resize(Image image, Integer seamesToRemoveX, Integer seamesToRemoveY) {
        Image valued = valued(image);
        Boolean[][] ignoreMatrix = new Boolean[(int) valued.getWidth()][(int) valued.getHeight()];
        IntStream
                .range(0, (int) valued.getHeight())
                .parallel()
                .forEach(readY -> IntStream
                        .range(0, (int) valued.getWidth())
                        .forEach(readX -> ignoreMatrix[readX][readY] = Boolean.FALSE));

        Boolean[][] ignoreMatrix2 = new Boolean[(int) valued.getWidth()][(int) valued.getHeight()];
        IntStream
                .range(0, (int) valued.getHeight())
                .parallel()
                .forEach(readY -> IntStream
                        .range(0, (int) valued.getWidth())
                        .forEach(readX -> ignoreMatrix2[readX][readY] = Boolean.FALSE));

        Integer[][] pathMatrix = new Integer[(int) valued.getWidth()][(int) valued.getHeight()];
        Double[] currentVal = new Double[(int) valued.getWidth()];
        Double[] oldVal = new Double[(int) valued.getWidth()];


        PixelReader pixelReader = valued.getPixelReader();

        // create new smaller image
        WritableImage up = new WritableImage(
                (int) image.getWidth(),
                (int) image.getHeight());

        PixelReader pixelReaderDDD = image.getPixelReader();
        PixelWriter pixelWriterDDD = up.getPixelWriter();
        Image finalImage = image;
        IntStream
                .range(0, (int) image.getHeight())
                .parallel()
                .forEach(readY -> {
                    for (int readX = 0; readX < finalImage.getWidth(); readX++) {
                        pixelWriterDDD.setColor(readX, readY, pixelReaderDDD.getColor(readX, readY));
                    }
                });

        for (int seam = 0; seam < seamesToRemoveX; seam++) {
            // initial nothing removed
            for (int i = 0; i < oldVal.length; i++) {
                oldVal[i] = colValSum(pixelReader.getColor(i, 0));
                pathMatrix[i][0] = 0;
            }

            for (int height = 1; height < valued.getHeight(); height++) {
                // figure the fucking skipping out
                for (int width = 0; width < valued.getWidth(); width++) {
                    int next = Integer.MAX_VALUE;
                    double nextVal = Double.POSITIVE_INFINITY;

                    // look up left- the counter thing till no fail
                    int skip = -1;
                    while (width + skip > 0 && ignoreMatrix[width + skip][height - 1]) {
                        skip--;
                    }
                    if (width + skip > 0) {
                        if (oldVal[width + skip] < nextVal) {
                            next = skip;
                            nextVal = oldVal[width + next];
                        }
                    }

                    // look up if there is non above we just lose one diagonal
                    if (!ignoreMatrix[width][height - 1]) {
                        if (oldVal[width] < nextVal) {
                            next = 0;
                            nextVal = oldVal[width];
                        }
                    }

                    // look up right
                    skip = 1;
                    while (width + skip < valued.getWidth() && ignoreMatrix[width + skip][height - 1]) {
                        skip++;
                    }
                    if (width + skip < valued.getWidth()) {
                        if (oldVal[width + skip] < nextVal) {
                            next = skip;
                            nextVal = oldVal[width + next];
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
                if (currentVal[i] < minVal && !ignoreMatrix[i][(int) valued.getHeight() - 1]) {
                    minI = i;
                    minVal = currentVal[i];
                }
            }

            for (int i = (int) valued.getHeight() - 1; i >= 0; i--) {
                pixelWriterDDD.setColor(minI, i, Color.RED);
                ignoreMatrix[minI][i] = Boolean.TRUE;
                minI += pathMatrix[minI][i];
            }
        }


        //  paint removed seams red
        /*
        WritableImage resized = new WritableImage(
                (int) valued.getWidth(),
                (int) valued.getHeight());

        PixelWriter pixelWriter = resized.getPixelWriter();
        PixelReader pixelReaderResize = image.getPixelReader();
        Image finalImage = valued;
        IntStream
                .range(0, (int) valued.getHeight())
                .parallel()
                .forEach(readY -> {
                    int shift = 0;
                    for (int readX = 0; readX < finalImage.getWidth(); readX++) {
                        if (ignoreMatrix[readX][readY]) {
                            pixelWriter.setColor(readX, readY, Color.RED);
                        } else {
                            pixelWriter.setColor(readX, readY, pixelReaderResize.getColor(readX + shift, readY));
                        }
                    }
                });

        for (int y = 0; y < (int) valued.getHeight(); y++) {
            int s = 0;
            for (int x = 0; x < (int) valued.getWidth(); x++) {
                if (ignoreMatrix[x][y]) s++;
            }
        }
        //*/

        Image valued2 = valued2(image);
        PixelReader pixelReader2 = valued.getPixelReader();

        currentVal = new Double[(int) valued.getHeight()];
        oldVal = new Double[(int) valued.getHeight()];

        // initial nothing removed
        for (int i = 0; i < oldVal.length; i++) {
            oldVal[i] = colValSum(pixelReader2.getColor(0, i));
            pathMatrix[0][i] = 0;
        }

        for (int jjj = 0; jjj < seamesToRemoveY; jjj++) {

            for (int width = 1; width < valued.getWidth(); width++) {
                for (int height = 0; height < valued.getHeight(); height++) {
                    // figure the fucking skipping out

                    int next = Integer.MAX_VALUE;
                    double nextVal = Double.POSITIVE_INFINITY;

                    // look up left- the counter thing till no fail
                    int skip = -1;

                    while (height + skip > 0 && ignoreMatrix2[width - 1][height + skip]) {
                        skip--;
                    }
                    if (height + skip > 0) {
                        if (oldVal[height] < nextVal) {
                            next = skip;
                            nextVal = oldVal[height + next];
                        }
                    }

                    // look up if there is non above we just lose one diagonal
                    if (!ignoreMatrix2[width - 1][height]) {
                        if (oldVal[height] < nextVal) {
                            next = 0;
                            nextVal = oldVal[height];
                        }
                    }

                    // look up right
                    skip = 1;

                    while (height + skip < valued.getHeight() && ignoreMatrix2[width - 1][height + skip]) {
                        skip++;
                    }

                    if (height + skip < valued.getHeight()) {
                        if (oldVal[height + skip] < nextVal) {
                            next = skip;
                            nextVal = oldVal[height + next];
                        }
                    }

                    // updating current
                    currentVal[height] = nextVal + colValSum(pixelReader2.getColor(width, height));
                    // updating path matrix
                    pathMatrix[width][height] = next;
                }
                oldVal = currentVal.clone();
            }

            // find lowest value
            int minI = -1;
            double minVal = Integer.MAX_VALUE;
            for (int i = 0; i < currentVal.length; i++) {
                if (currentVal[i] < minVal && !ignoreMatrix2[(int) valued.getWidth() - 1][i]) {
                    minI = i;
                    minVal = currentVal[i];
                }
            }

            for (int i = (int) valued.getWidth() - 1; i >= 0; i--) {
                pixelWriterDDD.setColor(i, minI, Color.LAWNGREEN);
                ignoreMatrix2[i][minI] = Boolean.TRUE;
                minI += pathMatrix[i][minI];
            }

        }
        // clean the stuff and make new image
        // create new smaller image


        //return resizeHelper(image, seamesToRemoveX, seamesToRemoveY, ignoreMatrix, ignoreMatrix2);
        return resizeHelperX(image, seamesToRemoveX, ignoreMatrix);
        //return resizeHelperX2(image, seamesToRemoveX, ignoreMatrix);
        //return resizeHelperY(image, seamesToRemoveY, ignoreMatrix2);
        //return up;
    }

    @Override
    public void start(Stage primaryStage) throws IOException, URISyntaxException {

        //Grayscale n = new Grayscale("test2.png");
        //n.toGray();

        Gradient m = new Gradient("Imgest/src/grayscale-test2.png");
        m.convert();

        // Create Image and ImageView objects
        String f = null;
        if (true) {
            f = "test.png"; // takes file from src folder
        } else {
            f = "test_large.png"; // takes file from src folder
        }
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
        //primaryStage.setResizable(false);
        primaryStage.show();
        int squareHelper = (int) (image.getWidth() - image.getHeight());
        /*
        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        Double valueTime = 0.0;
        Double seamTime = 0.0;
        Double resizeTime = 0.0;

        for (int i = 0; i < squareHelper; i++) {
            // find valued
            startTime = System.nanoTime();
            Image valuedImg = valued(image);
            endTime = System.nanoTime();
            valueTime += (endTime - startTime) / 1000000;
            startTime = endTime;
            // find min path
            Integer[] remove = minPath(valuedImg);
            //Integer[] remove2 = minPath2(valuedImg);

            endTime = System.nanoTime();
            seamTime += (endTime - startTime) / 1000000;
            startTime = endTime;
            // create new smaller image
            WritableImage up = new WritableImage(
                    (int) image.getWidth() - 1,
                    (int) image.getHeight());

            PixelReader pixelReader = image.getPixelReader();
            PixelWriter pixelWriter = up.getPixelWriter();
            Image finalImage = image;
            IntStream
                    .range(0, (int) image.getHeight())
                    .parallel()
                    .forEach(readY -> {
                        int shift = 0;
                        for (int readX = 0; readX < finalImage.getWidth() - 1; readX++) {
                            if (remove[readY] == readX) shift = 1;
                            pixelWriter.setColor(readX, readY, pixelReader.getColor(readX + shift, readY));
                        }
                    });
            image = up;
            endTime = System.nanoTime();
            resizeTime += (endTime - startTime) / 1000000;
        }
        //long endTime = System.nanoTime();
        //System.out.println((endTime - startTime)/1000000);
        System.out.println("Val time\t" + valueTime);
        System.out.println("Seam time\t" + seamTime);
        System.out.println("Resize time\t" + resizeTime);
        System.out.println("save");
        imageView.setImage(image);
        primaryStage.show();
        */
        // resize example

        image = resize(image, squareHelper, 0);
        //image = valued2(image);
        imageView.setImage(image);
        primaryStage.show();
        // save result
        String format = "png";
        File file = new File("out.png");
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, file);
        System.out.println("no crash");
    }

    public static void main(String[] args) {
        launch(args);
    }
}