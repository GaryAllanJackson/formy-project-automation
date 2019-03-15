public class TestSettings {

    private String _xPath;              //this is the accessor that is going to be used to find the element
    private String _expectedValue;      //the value to check or action to perform (contains its own parameters)
    private String _searchType;         //accessor type
    private Boolean performWrite;       //True to perform an action, false to read text
    private Boolean _isCrucial;         //True if test should stop upon failed test step, false if test should continue


    public String get_xPath() {
        return _xPath;
    }

    public void set_xPath(String _xPath) {
        this._xPath = _xPath;
    }

    public String get_expectedValue() {
        return _expectedValue;
    }

    public void set_expectedValue(String _expectedValue) {
        this._expectedValue = _expectedValue;
    }

    public String get_searchType() {
        return _searchType;
    }

    public void set_searchType(String _searchType) {
        this._searchType = _searchType;
    }

    public Boolean getPerformWrite() {
        return performWrite;
    }

    public void setPerformWrite(Boolean performWrite) {
        this.performWrite = performWrite;
    }

    public Boolean get_isCrucial() {
        return _isCrucial;
    }

    public void set_isCrucial(Boolean _isCrucial) {
        this._isCrucial = _isCrucial;
    }
}
