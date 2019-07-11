import java.util.List;

public class TestStep {

    private String _command;
    private String _actionType;
    private String _expectedValue;
    private String _accessor;
    private String _accessorType;
    private Boolean _crucial;

    public List<Argument> ArgumentList;

    public TestStep() {

    }

    public String get_command() {
        return _command;
    }

    public void set_command(String _command) {
        this._command = _command;
    }

    public String get_actionType() {
        return _actionType;
    }

    public void set_actionType(String _actionType) {
        this._actionType = _actionType;
    }


    public String get_expectedValue() {
        return _expectedValue;
    }

    public void set_expectedValue(String _expectedValue) {
        this._expectedValue = _expectedValue;
    }

    public String get_accessor() {
        return _accessor;
    }

    public void set_accessor(String _accessor) {
        this._accessor = _accessor;
    }

    public String get_accessorType() {
        return _accessorType;
    }

    public void set_accessorType(String _accessorType) {
        this._accessorType = _accessorType;
    }

    public Boolean get_crucial() {
        return _crucial;
    }

    public void set_crucial(Boolean _crucial) {
        this._crucial = _crucial;
    }



}
