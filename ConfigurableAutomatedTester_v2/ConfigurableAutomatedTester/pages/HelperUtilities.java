import com.google.common.io.Files;
import org.im4java.core.CompareCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;
import org.im4java.process.StandardStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

//refer to the following page for instructions or reference on implementation
//https://www.swtestacademy.com/visual-testing-imagemagick-selenium/
public class HelperUtilities {

    //Image files
    public File differenceImageFile;
    public File differenceFileForParent = null;
    private static final String OS = System.getProperty("os.name").toLowerCase();
    TestHelper testHelper; // = new TestHelper();
    private boolean _executedFromMain;
    private boolean is_executedFromMain() {
        return _executedFromMain;
    }

    void set_executedFromMain(boolean _executedFromMain) {
        this._executedFromMain = _executedFromMain;
    }
    private String logFileName;
    private String numType = "bytes";
    private double _acceptableDifference;

    public double get_acceptableDifference() {
        return _acceptableDifference;
    }

    public void set_acceptableDifference(double _acceptableDifference) {
        this._acceptableDifference = _acceptableDifference;
    }

    public HelperUtilities(TestCentral testCentral) {
        testHelper = new TestHelper(testCentral);
    }

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
    public void CompareImagesWithImageMagick (String expected, String actual, String difference, String fileStepIndex) throws Exception {
        // This class implements the processing of os-commands using a ProcessBuilder.
        // This is the core class of the im4java-library where all the magic takes place.
        //ProcessStarter.setGlobalSearchPath("C:\\Program Files\\ImageMagick-7.0.4-Q16");
        //ProcessStarter.setGlobalSearchPath("C:\\Program Files\\ImageMagick-7.0.8-Q16");
        if (isMac()) {
            expected = EscapeMacPath(expected);
            actual = EscapeMacPath(actual);
            difference = EscapeMacPath(difference);
        }
        final double fuzz = 10.0;
        final String metric = "AE";

        String differenceImage = difference;
        ProcessStarter.setGlobalSearchPath("C:\\Program Files (x86)\\ImageMagick-7.0.8-Q16-HDRI");
        differenceImageFile = new File (difference);
        testHelper.set_executedFromMain(is_executedFromMain());
        String globalImageMessage = "";

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
        //cmpOp.fuzz(10.0);
        cmpOp.fuzz(fuzz);

        //The special "-metric" setting of 'AE' (short for "Absolute Error" count), will report (to standard error),
        //a count of the actual number of pixels that were masked, at the current fuzz factor.
        //cmpOp.metric("AE");
        cmpOp.metric(metric);

        // Add the expected image
        cmpOp.addImage(expected);

        // Add the actual image
        cmpOp.addImage(actual);

        // This stores the difference
        cmpOp.addImage(difference);
        try {
            if (isWindows()) {
                //Do the compare
                compare.run(cmpOp);
            } else {
                //boolean status = ExecuteCompareForMac("compare -fuzz " + fuzz + " -metric " + metric + expected + " " + actual + " " + difference);
//                boolean status = ExecuteShellCommand("compare", "-fuzz " + fuzz, " -metric " + metric, expected, actual, difference);
//                if (!status) {
//                    testHelper.UpdateTestResults("\r\nFailure something went wrong while issuing the compare command on Mac.  Ensure the ImageMagick bin folder is in your path!", true);
//                }
                Runtime runTime = Runtime.getRuntime();
                Process proc = runTime.exec("compare -fuzz " + fuzz + " -metric " + metric + expected + " " + actual + " " + difference);
            }
        } catch (Exception ex) {
            //the java.lang.NullPointerException exception happens because of a flaw in img4java and needs to be suppressed but all other errors should be shown.
            if (!ex.getMessage().equals("java.lang.NullPointerException")) {
                testHelper.UpdateTestResults(AppConstants.ANSI_RED_BRIGHT + "The following error occurred while performing the image comparison: " + ex.getMessage() + AppConstants.ANSI_RESET, true);
            }
            //System.out.print(ex);
            //Put the difference image to the global differences folder
            //throw ex;  //do not re-throw this!!!!!
        }

        //make a copy of the difference file in the global folder
        if (differenceImageFile.exists() && differenceFileForParent != null) {
            Files.copy(differenceImageFile, differenceFileForParent);
            globalImageMessage = "\r\n" + AppConstants.indent5 + "Global Difference Image:(" + differenceFileForParent.getAbsoluteFile().toString() + ")\r\n";
            globalImageMessage += AppConstants.indent5 + GetGlobalFileStorageInformation(new File(differenceFileForParent.getParent()));
            //differenceImage = differenceFileForParent.getAbsoluteFile().toString();
        }

        File tempDifference = new File(differenceImage);
        if (is_executedFromMain()) {
            testHelper.UpdateTestResults(" - ImageMagick DB Level (0 for identical images, numeric value for non-identical images)", false);
        }
        if (differenceImage != null && tempDifference.exists()) {
            testHelper.UpdateTestResults("\r\nSuccessful comparison of images.  View the images to see the comparison results:\r\n" +
                    AppConstants.indent5 + "Baseline Image: (" + expected + ")\r\n" +
                    AppConstants.indent5 + "Actual Image: (" + actual + ")\r\n" +
                    AppConstants.indent5 + "Difference Image:(" + differenceImage + ")" + globalImageMessage + " for step " + fileStepIndex, true);
        } else {
            testHelper.UpdateTestResults("\r\nFailed something may have gone wrong as no difference image was created for step " + fileStepIndex, true);
        }
        try {
            GetPercentageDifference(expected, actual, difference, fileStepIndex);
        } catch(IllegalArgumentException ia) {
            if (ia.getMessage().contains("Images must have the same dimensions:")) {
                testHelper.UpdateTestResults("Failed Unable to get difference percentage of images with different dimensions" +
                                "Baseline image dimensions: " + testHelper.GetImageDimensions(expected) + "\r\nActual image dimensions: " + testHelper.GetImageDimensions(actual)  + " for step " + fileStepIndex, true);
//                testHelper.UpdateTestResults("Failure Unable to get difference percentage of images with different dimensions for step " + fileStepIndex, true);
//                testHelper.UpdateTestResults("Baseline image dimensions: " + testHelper.GetImageDimensions(expected) + "\r\nActual image dimensions: " + testHelper.GetImageDimensions(actual)  + " for step " + fileStepIndex, true);
            }
        }
    }

//    public printOutput getStreamWrapper(InputStream inputStream, String type) {
//        return new printOutput(inputStream, type);
//    }


    private boolean ExecuteCompareForMac(String command) {
        boolean status = false;

        try {
            //add the ImageMagick bin folder to the command so that it can find the command program
            command = "\"" + ProcessStarter.getGlobalSearchPath() + "bin/" + command + "\"";

            // Execute command
            Process child = Runtime.getRuntime().exec(command);
            status = true;
        } catch (IOException e) {
            testHelper.UpdateTestResults("The following error occurred while trying to shut down ChromeDriver: " + e.getMessage(), true);
        }
        return status;
    }

    private boolean ExecuteShellCommand(String command, String fuzz, String metric, String expected, String actual, String difference) {
        boolean status = false;

        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.command(command, fuzz, metric, expected, actual, difference);
        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                System.out.println(output);
                System.exit(0);
                status = true;
            } else {
                //abnormal...
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        return status;

    }


    /********************************************************************************************
     * Description: Gets the percentage of difference between two images
     * @param expected
     * @param actual
     * @param difference
     * @throws IOException
     *******************************************************************************************/
    private void GetPercentageDifference(String expected, String actual, String difference, String fileStepIndex) throws IOException {
        BufferedImage img1 = ImageIO.read(new File(expected));
        BufferedImage img2 = ImageIO.read(new File(actual));
//      region {Refactored remove this once completely validated }
//        double differencePercentage = GetDifferencePercent(img1, img2);
//        String diffColor = differencePercentage > 0 ? AppConstants.ANSI_RED_BRIGHT : AppConstants.ANSI_GREEN_BRIGHT;
//        testHelper.UpdateTestResults(diffColor + AppConstants.indent5 +  "Difference Percentage: " + differencePercentage + "%" + AppConstants.ANSI_RESET, true);
//         String diffColor = differencePercentage > 0 ? AppConstants.ANSI_RED_BRIGHT : AppConstants.ANSI_GREEN_BRIGHT;
        //testHelper.UpdateTestResults(diffColor + AppConstants.indent5 +  successFailurePrefix + " Difference Percentage: " + df.format(differencePercentage) + "% Acceptable Difference Percentage: " + get_acceptableDifference() + "% for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
        //endregion
        double differencePercentage = GetPixelPercentageDifference(img1, img2);
        DecimalFormat df = new DecimalFormat("###.##");

        double acceptableDifference = get_acceptableDifference() > 0 ? get_acceptableDifference() : 0;
        String diffColor = differencePercentage > acceptableDifference ? AppConstants.ANSI_RED_BRIGHT : AppConstants.ANSI_GREEN_BRIGHT;
        String successFailurePrefix = differencePercentage > acceptableDifference ? "Failed" : "Successful";

        testHelper.UpdateTestResults( successFailurePrefix + " Difference Percentage: " + df.format(differencePercentage) + "% (Acceptable Difference Percentage: " + get_acceptableDifference() + "%) for step " + fileStepIndex, true);
    }


    /********************************************************************************************
     * Description: Gets the numerical percentage of difference value between two images.
     * @param img1
     * @param img2
     *******************************************************************************************/
    //private static double GetDifferencePercent(BufferedImage img1, BufferedImage img2) {
    private double GetDifferencePercent(BufferedImage img1, BufferedImage img2) {
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
     * Description: Gets the numerical pixel percentage of difference value between two images
     *              using the 100 * (pixelDifference / (image height * image width)).
     *
     * @param img1 - First image to be compared
     * @param img2 - Second image to be compared
     * @return - double representing the Pixel Difference Percentage
     *******************************************************************************************/
    private double GetPixelPercentageDifference(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        int width2 = img2.getWidth();
        int height2 = img2.getHeight();
        if (width != width2 || height != height2) {
            throw new IllegalArgumentException(String.format("Images must have the same dimensions: (%d,%d) vs. (%d,%d)", width, height, width2, height2));
        }

        double diff = 0;
        double gDiff = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                diff = pixelDiff(img1.getRGB(x, y), img2.getRGB(x, y));
                if (diff != 0) {
                    gDiff += 1;  //2;
                }
            }
        }
        //long maxDiff = 3L * 255 * width * height;
        double maxDiff = width * height;
        DecimalFormat df = new DecimalFormat("###.##");
        //region {Debugging view difference percentages }
//        testHelper.DebugDisplay("maxDiff = (" + width + " * " + height + ") = " + maxDiff);
//        testHelper.DebugDisplay("gDiff = " + gDiff);
//        testHelper.DebugDisplay("return 100 * (" + gDiff + " / " + maxDiff + ") = " + (100.0 * (gDiff / maxDiff)));
//        testHelper.DebugDisplay("return Math.round(100 * (" + gDiff + " / " + maxDiff + ")) = " + Math.round(100.0 * (gDiff / maxDiff)));
//        testHelper.DebugDisplay("return Math.round(100 * (" + gDiff + " / " + maxDiff + ")) = " + df.format(100.0 * (gDiff / maxDiff)));
        //endregion
        return 100.0 * (gDiff / maxDiff);
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

    public String EscapeMacPath(String filePath) {
        return filePath.replace(" ", "\\ ");
    }

    /*******************************************************************************
     * Description: Retrieves the available space for all mapped drives, the number of
     *          files in the Global Backup folder as well as the total size in bytes of
     *          all files in the Global Backup Folder and returns this to the calling
     *          method as one formatted string containing this information.
     * @param folder - Global Backup Folder
     * @return  - String of folder and drive space information
     * @throws IOException - An IOException can be thrown if the folder does not exist.
     **********************************************************************************/
    private String GetGlobalFileStorageInformation(final File folder) throws IOException {
        NumberFormat nf = NumberFormat.getNumberInstance();
        String availableSpace = "";
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            try {
                FileStore store = java.nio.file.Files.getFileStore(root);
                float tmpLong = ReduceNumberAndSetDescriptor(store.getUsableSpace());
                availableSpace += root + " = " + nf.format(tmpLong) + " " + numType + "\r\n\t" + AppConstants.indent8;

            } catch(IOException e) {
                availableSpace = "??";
            }
        }

        availableSpace = availableSpace.contains("\r") ? availableSpace.substring(0, availableSpace.lastIndexOf("\r")) : " unknown";
        String temp = "\tThe " + folder.getName() + " folder contains x files totaling xx bytes.\r\n" + AppConstants.indent8 + "Available Space: \r\n\t" + AppConstants.indent8 + availableSpace;
        float totalSize = 0;
        int totalFiles = 0;

        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isFile()) {
                totalSize += fileEntry.length();
                totalFiles++;
            }
        }

        DecimalFormat formatter =  new DecimalFormat("###,###,###,###.###");
        totalSize = ReduceNumberAndSetDescriptor((long)totalSize);

        return temp.replace("xx", formatter.format(totalSize)).replace(" x ", " " + Integer.toString(totalFiles) + " ").replace("bytes", numType);
    }

    /***********************************************************************************
     * Description: This method reduces the number passed in by the highest 10s multiplier
     *              and sets the global numType variable accordingly so that
     *              1,000,000 = 1 and the numType is set to MB
     *              thus reducing 1,000,000 to 1 MB.
     * @param size
     * @return
     ***************************************************************************************/
    float ReduceNumberAndSetDescriptor(long size) {

        if (size > 1000000000) {
            size = size / 1000000000;
            numType = "GB";
            if (size > 1000) {
                size = size / 1000;
                numType = "TB";
            }
        } else if (size > 1000000) {
            size = size / 1000000;
            numType = "MB";
        } else if (size > 1000) {
            size = size / 1000;
            numType = "KB";
        }
        return size;
    }


    //region { Refactored and removed }
//    private void SetUpExceptionVariables(String difference) {
//        String delimiter = isWindows() ? "\\" : "//";
//        differenceImageFile = new File (difference);
//        int endPosition = difference.lastIndexOf(delimiter);
//        String temp = difference.substring(0, endPosition - 1);
//        endPosition = temp.lastIndexOf(delimiter);
//        temp = temp.substring(0, endPosition);
//        if (!temp.endsWith(delimiter)) {
//
//        }
//
//        //differenceFileForParent = new File (parentDifferencesLocation + diff);
//    }
    //endregion
}
