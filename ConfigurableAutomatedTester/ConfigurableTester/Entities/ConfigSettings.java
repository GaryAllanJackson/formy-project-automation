import java.util.ArrayList;
import java.util.List;

public class ConfigSettings {
    private String _browserType;
    private String _testPageRoot;
    private Boolean _runHeadless;
    private String _screenShotSaveFolder;
    private Boolean _testAllBrowsers;
    private String _testSettingsFile;
//    private List<String> _testFiles;
    private ArrayList<String> _testFiles;
    private Boolean _specifyFileNames;
    private String _testFolderName;
    private String _folderFileFilterType;
    private String _folderFileFilter;
    private int _maxScreenShots;
    private Boolean _sortSpecifiedTestFiles;
    private String _createCsvStatusFiles;

    public ConfigSettings() {
        _testFiles = new ArrayList<>();
    }


    public String get_browserType() {
        return _browserType;
    }

    public void set_browserType(String _browserType) {
        this._browserType = _browserType;
    }

    public String get_testPageRoot() {
        return _testPageRoot;
    }

    public void set_testPageRoot(String _testPageRoot) {
        this._testPageRoot = _testPageRoot;
    }

    public Boolean get_runHeadless() {
        return _runHeadless;
    }

    public void set_runHeadless(Boolean _runHeadless) {
        this._runHeadless = _runHeadless;
    }

    public String get_screenShotSaveFolder() {
        return _screenShotSaveFolder;
    }

    public void set_screenShotSaveFolder(String _screenShotSaveFolder) {
        this._screenShotSaveFolder = _screenShotSaveFolder;
    }

    public Boolean get_testAllBrowsers() {
        return _testAllBrowsers;
    }

    public void set_testAllBrowsers(Boolean _testAllBrowsers) {
        this._testAllBrowsers = _testAllBrowsers;
    }

    public String get_testSettingsFile() {
        return _testSettingsFile;
    }

    public void set_testSettingsFile(String _testSettingsFile) {
        this._testSettingsFile = _testSettingsFile;
        _testFiles.add(_testSettingsFile);
        //Collections.sort(_testFiles);  //testing this
    }

    public void set_testSettingsFile(String _testSettingsFile, int position) {
        this._testSettingsFile = _testSettingsFile;
        if (position < 0 || _testFiles.size() <= 0) {
            _testFiles.add(_testSettingsFile);
        }
        else {
            _testFiles.add(position, _testSettingsFile);
        }
        //Collections.sort(_testFiles);  //testing this
    }

    public void reset_testSettingsFile() {
        _testFiles = new ArrayList<>();
    }

    public List<String> get_testFiles() {
        return _testFiles;
    }

    public List<String> reset_testFiles() {
        _testFiles = new ArrayList<>();
        return _testFiles;
    }

    public Boolean get_specifyFileNames() {
        return _specifyFileNames;
    }

    public void set_specifyFileNames(Boolean _specifyFileNames) {
        this._specifyFileNames = _specifyFileNames;
    }

    public String get_testFolderName() {
        return _testFolderName;
    }

    public void set_testFolderName(String _testFolderName) {
        this._testFolderName = _testFolderName;
    }

    public String get_folderFileFilterType() {
        return _folderFileFilterType;
    }

    public void set_folderFileFilterType(String _folderFileFilterType) {
        this._folderFileFilterType = _folderFileFilterType;
    }

    public String get_folderFileFilter() {
        return _folderFileFilter;
    }

    public void set_folderFileFilter(String _folderFileFilter) {
        this._folderFileFilter = _folderFileFilter;
    }

    public int get_maxScreenShots() {
        return _maxScreenShots;
    }

    public void set_maxScreenShots(int _maxScreenShots) {
        this._maxScreenShots = _maxScreenShots;
    }

    public Boolean get_sortSpecifiedTestFiles() {
        return _sortSpecifiedTestFiles;
    }

    public void set_sortSpecifiedTestFiles(Boolean _sortSpecifiedTestFiles) {
        this._sortSpecifiedTestFiles = _sortSpecifiedTestFiles;
    }

    public String get_createCsvStatusFiles() {
        return _createCsvStatusFiles;
    }

    public void set_createCsvStatusFiles(String _createCsvStatusFiles) {
        this._createCsvStatusFiles = _createCsvStatusFiles;
    }



}
