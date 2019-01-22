public class TestSettings {

    private String _xPath;
    private String _expectedValue;
    private String _searchType;
    private Boolean performWrite;


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
}
