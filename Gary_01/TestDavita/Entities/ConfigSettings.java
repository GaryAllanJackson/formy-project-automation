public class ConfigSettings {
    private String _browserType;
    private String _testPageRoot;
    private Boolean _runHeadless;
    private String _screenShotSaveFolder;
    private Boolean _testAllBrowsers;
    private String _testSettingsFile;


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
    }
}
