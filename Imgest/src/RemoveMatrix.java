import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.stream.IntStream;

/**
 * Created by IHallik on 12/01/2017.
 */
public class RemoveMatrix {
    private Image valued;
    private int seamsCount;
    private int axis;   // 1 X, 2 Y

    public RemoveMatrix(Image valued, int seamsCount, int axis) {
        this.valued = valued;
        this.axis = axis;
        this.seamsCount = Math.abs(seamsCount);
    }

    public Boolean[][] getRemoveMatrix() {
        if (axis == 1)
            return helperX();
        if (axis == 2)
            return helperY();
        return null;
    }

    private Boolean[][] helperX() {
        Boolean[][] ignoreMatrix = new Boolean[(int) valued.getWidth()][(int) valued.getHeight()];
        IntStream
                .range(0, (int) valued.getHeight())
                .parallel()
                .forEach(readY -> IntStream
                        .range(0, (int) valued.getWidth())
                        .forEach(readX -> ignoreMatrix[readX][readY] = Boolean.FALSE));

        Integer[][] pathMatrix = new Integer[(int) valued.getWidth()][(int) valued.getHeight()];
        Double[] currentVal = new Double[(int) valued.getWidth()];
        Double[] oldVal = new Double[(int) valued.getWidth()];


        PixelReader pixelReader = valued.getPixelReader();

        for (int seam = 0; seam < seamsCount; seam++) {
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
                ignoreMatrix[minI][i] = Boolean.TRUE;
                minI += pathMatrix[minI][i];
            }
        }


        return ignoreMatrix;
    }

    public double colValSum(Color c) {
        return c.getBlue() + c.getGreen() + c.getRed();
    }


    private Boolean[][] helperY() {
        Boolean[][] ignoreMatrix = new Boolean[(int) valued.getWidth()][(int) valued.getHeight()];
        IntStream
                .range(0, (int) valued.getHeight())
                .parallel()
                .forEach(readY -> IntStream
                        .range(0, (int) valued.getWidth())
                        .forEach(readX -> ignoreMatrix[readX][readY] = Boolean.FALSE));

        PixelReader pixelReader = valued.getPixelReader();
        Integer[][] pathMatrix = new Integer[(int) valued.getWidth()][(int) valued.getHeight()];
        Double[] currentVal = new Double[(int) valued.getHeight()];
        Double[] oldVal = new Double[(int) valued.getHeight()];

        // initial nothing removed
        for (int i = 0; i < oldVal.length; i++) {
            oldVal[i] = colValSum(pixelReader.getColor(0, i));
            pathMatrix[0][i] = 0;
        }

        for (int seams = 0; seams < seamsCount; seams++) {

            for (int width = 1; width < valued.getWidth(); width++) {
                for (int height = 0; height < valued.getHeight(); height++) {
                    // figure the fucking skipping out

                    int next = Integer.MAX_VALUE;
                    double nextVal = Double.POSITIVE_INFINITY;

                    // look up left- the counter thing till no fail
                    int skip = -1;

                    while (height + skip > 0 && ignoreMatrix[width - 1][height + skip]) {
                        skip--;
                    }
                    if (height + skip > 0) {
                        if (oldVal[height] < nextVal) {
                            next = skip;
                            nextVal = oldVal[height + next];
                        }
                    }

                    // look up if there is non above we just lose one diagonal
                    if (!ignoreMatrix[width - 1][height]) {
                        if (oldVal[height] < nextVal) {
                            next = 0;
                            nextVal = oldVal[height];
                        }
                    }

                    // look up right
                    skip = 1;

                    while (height + skip < valued.getHeight() && ignoreMatrix[width - 1][height + skip]) {
                        skip++;
                    }

                    if (height + skip < valued.getHeight()) {
                        if (oldVal[height + skip] < nextVal) {
                            next = skip;
                            nextVal = oldVal[height + next];
                        }
                    }

                    // updating current
                    currentVal[height] = nextVal + colValSum(pixelReader.getColor(width, height));
                    // updating path matrix
                    pathMatrix[width][height] = next;
                }
                oldVal = currentVal.clone();
            }

            // find lowest value
            int minI = -1;
            double minVal = Integer.MAX_VALUE;
            for (int i = 0; i < currentVal.length; i++) {
                if (currentVal[i] < minVal && !ignoreMatrix[(int) valued.getWidth() - 1][i]) {
                    minI = i;
                    minVal = currentVal[i];
                }
            }

            for (int i = (int) valued.getWidth() - 1; i >= 0; i--) {
                ignoreMatrix[i][minI] = Boolean.TRUE;
                minI += pathMatrix[i][minI];
            }

        }
        return ignoreMatrix;
    }


    // 2 different heuristic methods that only find 1 seam at a time
    private Integer[] minPath2(Image image) {
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

    private Integer[] minPath(Image image) {
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

}
