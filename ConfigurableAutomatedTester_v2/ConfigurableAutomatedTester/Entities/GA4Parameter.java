public class GA4Parameter {

    private String _parameterName;
    private String _parameterValue;

    public GA4Parameter() {

    }

    public GA4Parameter(String parameterName, String parameterValue) {
        _parameterName = parameterName;
        _parameterValue = parameterValue;
    }



    public String get_parameterName() {
        return _parameterName;
    }
    public String get_parameterValue() {
        return _parameterValue;
    }

    public void set_parameterName(String _parameterName) {
        this._parameterName = _parameterName;
    }
    public void set_parameterValue(String _parameterValue) {
        this._parameterValue = _parameterValue;
    }
}
