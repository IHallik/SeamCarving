import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.stream.IntStream;

/**
 * Created by IHallik on 12/01/2017.
 */
public class Resize {
    private Image input;
    private Boolean[][] removeMatrix;
    private int resizeAmount;
    private int axis;  // 1 x, 2 y


    public Resize(Image input, Boolean[][] ignoreMatrix, int resizeAmount, int axis) {
        this.input = input;
        this.removeMatrix = ignoreMatrix;
        this.resizeAmount = resizeAmount;
        this.axis = axis;
    }

    public Image getImage() {
        if (axis == 1) {
            if (resizeAmount > 0) {
                return resizeHelperXadd();
            } else {
                return resizeHelperXremove();
            }
        }
        if (axis == 2) {
            if (resizeAmount > 0) {
                //return resizeHelperYadd();
            } else {
                return resizeHelperYremove();
            }
        }
        return null;
    }

    public Color colValAvg(Color c1, Color c2) {
        Color c = Color.color(
                (c1.getRed() + c2.getRed()) / 2.0,
                (c1.getGreen() + c2.getGreen()) / 2.0,
                (c1.getBlue() + c2.getBlue()) / 2.0
        );
        return c;
    }

    public Image resizeHelperXremove() {
        WritableImage resized = new WritableImage(
                (int) input.getWidth() + resizeAmount,
                (int) input.getHeight());

        PixelWriter pixelWriter = resized.getPixelWriter();
        PixelReader pixelReaderResize = input.getPixelReader();
        Integer xSize = (int) input.getWidth();
        Integer ySize = (int) input.getHeight();

        for (int y = 0; y < ySize; y++) {
            int skip = 0;
            for (int x = 0; x < xSize + resizeAmount; x++) {
                while (x + skip < xSize && removeMatrix[x + skip][y]) {
                    skip++;
                }
                pixelWriter.setColor(x, y, pixelReaderResize.getColor(x + skip, y));
            }
        }
        return resized;
    }

    public Image resizeHelperXadd() {
        WritableImage resized = new WritableImage(
                (int) input.getWidth() + resizeAmount,
                (int) input.getHeight());

        PixelWriter pixelWriter = resized.getPixelWriter();
        PixelReader pixelReaderResize = input.getPixelReader();
        Integer xSize = (int) input.getWidth();
        Integer ySize = (int) input.getHeight();

        for (int y = 0; y < ySize; y++) {
            int skip = 0;
            for (int x = 0; x < xSize; x++) {
                if (removeMatrix[x][y]) {
                    if (x == 0) {
                        pixelWriter.setColor(x + skip, y, pixelReaderResize.getColor(x, y));
                    } else {
                        pixelWriter.setColor(x + skip, y, colValAvg(pixelReaderResize.getColor(x - 1, y), pixelReaderResize.getColor(x, y)));
                    }
                    skip++;
                }
                pixelWriter.setColor(x + skip, y, pixelReaderResize.getColor(x, y));
            }
        }
        return resized;
    }

    public Image resizeHelperYremove() {
        WritableImage resized = new WritableImage(
                (int) input.getWidth(),
                (int) input.getHeight() + resizeAmount);

        PixelWriter pixelWriter = resized.getPixelWriter();
        PixelReader pixelReaderResize = input.getPixelReader();
        Integer xSize = (int) input.getWidth();
        Integer ySize = (int) input.getHeight();


        for (int x = 0; x < xSize; x++) {
            int skip = 0;
            for (int y = 0; y < ySize + resizeAmount; y++) {
                while (y + skip < ySize && removeMatrix[x][y + skip]) {
                    skip++;
                }
                pixelWriter.setColor(x, y, pixelReaderResize.getColor(x, y + skip));
            }
        }
        return resized;
    }


    public Image resizeHelper(Image image, Integer seamesToRemoveX, Integer seamesToRemoveY, Boolean[][] ignoreMatrixX, Boolean[][] ignoreMatrixY) {
        // NOT WORKING
        // something wrong with logic, did not manage to debug
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
                while (x + skip < xSize && ignoreMatrixX[x + skip][y]) {
                    skip++;
                }
                pixelWriter.setColor(x, y, pixelReaderResize.getColor(x + skip, y));
                if (ignoreMatrixY[x + skip][y]) ignoreMatrixHelper[x][y] = true;
            }
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

            }
        } catch (Exception e) {
        }
        return image;
    }
}
