import com.google.common.io.Files;
import org.im4java.core.CompareCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;
import org.im4java.process.StandardStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//refer to the following page for instructions or reference on implementation
//https://www.swtestacademy.com/visual-testing-imagemagick-selenium/
public class HelperUtilities {

    //Image files
    public File baselineImageFile;
    public File actualImageFile;
    public File differenceImageFile;
    public File differenceFileForParent;
    private String _screenShotFolder;
    private static String OS = System.getProperty("os.name").toLowerCase();
    private TestHelper testHelper = new TestHelper();



    //ImageMagick Compare Method
    /********************************************************************************************
     * Description: Compares two images and creates a difference image using the ImageMagick
     *              application for the comparison, then does a pixel comparison of the
     *              two images to report a numeric percentage of differences between the images.
     * @param expected
     * @param actual
     * @param difference
     * @throws IOException
     *******************************************************************************************/
    public void CompareImagesWithImageMagick (String expected, String actual, String difference) throws Exception {
        // This class implements the processing of os-commands using a ProcessBuilder.
        // This is the core class of the im4java-library where all the magic takes place.
        //ProcessStarter.setGlobalSearchPath("C:\\Program Files\\ImageMagick-7.0.4-Q16");
        //ProcessStarter.setGlobalSearchPath("C:\\Program Files\\ImageMagick-7.0.8-Q16");

        String differenceImage = difference;
        ProcessStarter.setGlobalSearchPath("C:\\Program Files (x86)\\ImageMagick-7.0.8-Q16-HDRI");
        differenceImageFile = new File (difference);

        //if the difference file already exists, delete it and start fresh
        RemoveExistingFile(difference);

        // This instance wraps the compare command
        CompareCmd compare = new CompareCmd();

        // Set the ErrorConsumer for the stderr of the ProcessStarter.
        compare.setErrorConsumer(StandardStream.STDERR);

        // Create ImageMagick Operation Object
        IMOperation cmpOp = new IMOperation();

        //Add option -fuzz to the ImageMagick commandline
        //With Fuzz we can ignore small changes
        cmpOp.fuzz(10.0);

        //The special "-metric" setting of 'AE' (short for "Absolute Error" count), will report (to standard error),
        //a count of the actual number of pixels that were masked, at the current fuzz factor.
        cmpOp.metric("AE");

        // Add the expected image
        cmpOp.addImage(expected);

        // Add the actual image
        cmpOp.addImage(actual);

        // This stores the difference
        cmpOp.addImage(difference);
        try {
            //Do the compare
            compare.run(cmpOp);
        } catch (Exception ex) {
            //System.out.print(ex);
            //Put the difference image to the global differences folder
            Files.copy(differenceImageFile,differenceFileForParent);
            differenceImage = differenceFileForParent.getAbsoluteFile().toString();
            //throw ex;  //do not re-throw this!!!!!
        }

        File tempDifference = new File(differenceImage);
        if (differenceImage != null && tempDifference.exists()) {
            testHelper.UpdateTestResults("Successful comparison of images.  View the images to see the comparison results:\r\n" +
                    AppConstants.indent5 + "Baseline Image: (" + expected + ")\r\n" +
                    AppConstants.indent5 + "Actual Image: (" + actual + ")\r\n" +
                    AppConstants.indent5 + "Difference Image:(" + differenceImage + ")", true);
        } else {
            testHelper.UpdateTestResults("Failure something may have gone wrong as no difference image was created.", true);
        }
        try {
            GetPercentageDifference(expected, actual, difference);
        } catch(IllegalArgumentException ia) {
            if (ia.getMessage().contains("Images must have the same dimensions:")) {
                testHelper.UpdateTestResults("Failure Unable to get difference percentage of images with different dimensions", true);
            }
        }
    }

    /********************************************************************************************
     * Description: Gets the percentage of difference between two images
     * @param expected
     * @param actual
     * @param difference
     * @throws IOException
     *******************************************************************************************/
    private void GetPercentageDifference(String expected, String actual, String difference) throws IOException {

        BufferedImage img1 = ImageIO.read(new File(expected));
        BufferedImage img2 = ImageIO.read(new File(actual));

        double differencePercentage = GetDifferencePercent(img1, img2);
        String diffColor = differencePercentage > 0 ? AppConstants.ANSI_RED_BRIGHT : AppConstants.ANSI_GREEN_BRIGHT;
        testHelper.UpdateTestResults(diffColor + AppConstants.indent5 +  "Difference Percentage: " + differencePercentage + AppConstants.ANSI_RESET, true);
    }

    /********************************************************************************************
     * Description: Gets the numerica percentage of difference value between two images.
     * @param img1
     * @param img2
     *******************************************************************************************/
    private static double GetDifferencePercent(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        int width2 = img2.getWidth();
        int height2 = img2.getHeight();
        if (width != width2 || height != height2) {
            throw new IllegalArgumentException(String.format("Images must have the same dimensions: (%d,%d) vs. (%d,%d)", width, height, width2, height2));
        }

        long diff = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                diff += pixelDiff(img1.getRGB(x, y), img2.getRGB(x, y));
            }
        }
        long maxDiff = 3L * 255 * width * height;

        return 100.0 * diff / maxDiff;
    }

    /********************************************************************************************
     * Description: Gets the pixel difference between two images
     * @param rgb1
     * @param rgb2
     *******************************************************************************************/
    private static int pixelDiff(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >>  8) & 0xff;
        int b1 =  rgb1        & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >>  8) & 0xff;
        int b2 =  rgb2        & 0xff;
        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

    /**************************************************************
     * Description: Removes the existing file specified by the
     * fileName parameter.
     * @param fileName
     *************************************************************/
    private void RemoveExistingFile(String fileName) {
        File temp = new File(fileName);
        if (temp.exists()) {
            temp.delete();
        }
    }


    /****************************************************************
     * Description: Checks the OS and Returns true if Windows else
     *              false.
     * @return - True if Windows, else false
     ****************************************************************/
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    /****************************************************************
     * Description: Checks the OS and Returns true if iOS else
     *               false.
     * @return - True if iOS, else false
     ****************************************************************/
    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    /************************************************************************
     * Description: Changes the file path passed in so that the file is saved
     *              to the parent folder.
     * @param differenceImage
     * @return
     ************************************************************************/
    public File GetParentFolder(String differenceImage) {
        String delimiter = System.getProperty("file.separator").replace("\\", "\\\\");   //isWindows() ? "\\" : "/";
        String [] folderStructure = (differenceImage != null && !differenceImage.isEmpty()) ? differenceImage.split(delimiter) : null;
        String returnValue = null;

        if (folderStructure != null) {
            returnValue = "";
            for (int x=0;x<folderStructure.length;x++) {
                if (x != folderStructure.length - 2) {
                    returnValue += folderStructure[x] + "\\";
                    //testHelper.DebugDisplay("returnValue = " + returnValue);
                }
            }
            returnValue = returnValue.substring(0, returnValue.length() - 1);
        }
        return new File(returnValue);
    }

    private void SetUpExceptionVariables(String difference) {
        String delimiter = isWindows() ? "\\" : "//";
        differenceImageFile = new File (difference);
        int endPosition = difference.lastIndexOf(delimiter);
        String temp = difference.substring(0, endPosition - 1);
        endPosition = temp.lastIndexOf(delimiter);
        temp = temp.substring(0, endPosition);
        if (!temp.endsWith(delimiter)) {

        }

        //differenceFileForParent = new File (parentDifferencesLocation + diff);
    }

}
