public class AppConstants {

    public static final int XmlElementNode = 1;
    public static final int XmlTextNode = 3;

    //region { XML Node name constants for Configuration Settings }
    public static final String RootConfigurationNode = "automatedTestConfiguration";
    public static final String ScreenShotSaveFolderNode = "screenShotSaveFolder";
    public static final String MaxScreenShotsToTakeNode = "maxScreenShotsToTake";
    public static final String BrowserTypeNode = "browserType";
    public static final String RunHeadlessNode = "runHeadless";
    public static final String TestAllBrowsersNode = "testAllBrowsers";
    public static final String SpecifyTestFilesNode = "specifyTestFiles";
    public static final String SortSpecifiedTestFilesNode = "sortSpecifiedTestFiles";
    public static final String TestFolderNameNode = "testFolderName";
    public static final String FolderFileFilterTypeNode = "folderFileFilterType";
    public static final String FolderFileFilterNode = "folderFileFilter";
    public static final String TestFilesNode = "testFiles";
    //endregion

    //region { XML Node name constants for Test Settings }
    public static final String RootTestNode = "testSteps";
    public static final String TestStepNode = "step";
    public static final String CommandNode = "command";
    public static final String ActionTypeNode = "actionType";
    public static final String ExpectedValueNode = "expectedValue";
    public static final String CrucialCheckNode = "crucial";
    public static final String AccessorNode = "accessor";
    public static final String AccessorTypeNode = "accessorType";
    public static final String ArgumentsNode = "arguments";
    //endregion


    //bold
    public static final String ANSI_BOLD = "\u001B[1m";

    //region { System.out Colors }
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    //public static final String ANSI_BLACK_ALT = "\033[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BRIGHTWHITETEXT = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BRIGHTWHITE = "\u001B[40m";
    public static final String ANSI_BRIGHTBLUE = "\u001b[94m";
    //endregion

    //region {System out background colors }
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static final String COMBO_YELLOW_BLACK = "\u001B[43m;\u001B[30m";
    public static final String FRAMED = "\u001B[51m";
    //endregion

    //region {Display Formatting Ansi characters and spaces}
    public static final String sectionStartFormatLeft =  "╔══════════════[ ";
    public static final String sectionStartFormatRight = " ]══════════════╗";
    public static final String sectionEndFormatLeft =    "╚══════════════[ ";
    public static final String sectionEndFormatRight =   " ]══════════════╝";
    public static final String subsectionLeft = "     «══════════════[ ";
    public static final String subsectionRight = " ]══════════════════════════════════════════»";
    public static final String indent5 = "     ";
    public static final String indent8 = "        ";
    public static final String sectionLeftDown =  "╔";
    public static final String sectionLeftUp =  "╚";
    public static final String sectionRightDown =  "╗";
    public static final String sectionRightUp =  "╝";
    public static final String subsectionArrowLeft = "«";
    public static final String subsectionArrowRight = "»";
    public static final String iFrameSectionTopLeft = "╒";
    public static final String iFrameSectionTopRight = "╕";
    public static final String iFrameSectionBottomLeft = "╘";
    public static final String iFrameSectionBottomRight = "╛";
    //endregion


    //Numeric Default Settings
    public static final int DefaultTimeDelay = 4000;
    public static final int DefaultElementWaitTimeInSeconds = 30;

    public static final String SqlServer = "Sql Server";
    public static final String MongoDb = "MongoDb";

}
