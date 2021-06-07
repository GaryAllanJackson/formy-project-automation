public class GtmTag {

    private String _pageRef;            //page number starts at 0
    private String _requestUrl;         //Google URL with all information in parameters
    private String _hitType;            //Event or PageView
    private String _trackingId;         //GA Tracking Id
    private String _documentLocation;   //Actual Page URL (page where event happened)
    private String _documentTitle;      //Document Title
    private String _eventCategory;      //Event Category
    private String _eventAction;        //Event Action
    private String _eventLabel;         //Event Label
    private String _contentGroup1;      //Content Group 1 (Page Template)
    private String _contentGroup2;      //
    private String _customDimension9;

    public void set_PageRef(String _pageRef) { this._pageRef = _pageRef; }
    public void set_RequestUrl(String _requestUrl) { this._requestUrl = _requestUrl; }
    public void set_HitType(String _hitType) { this._hitType = _hitType; }
    public void set_TrackingId(String _trackingId) { this._trackingId = _trackingId; }
    public void set_DocumentLocation(String _documentLocation) { this._documentLocation = _documentLocation; }
    public void set_DocumentTitle(String _documentTitle) { this._documentTitle = _documentTitle; }
    public void set_EventCategory(String _eventCategory) { this._eventCategory = _eventCategory; }
    public void set_EventAction(String _eventAction) { this._eventAction = _eventAction; }
    public void set_EventLabel(String _eventLabel) { this._eventLabel = _eventLabel; }
    public void set_ContentGroup1(String _contentGroup1) { this._contentGroup1 = _contentGroup1; }
    public void set_ContentGroup2(String _contentGroup2) { this._contentGroup2 = _contentGroup2; }
    public void set_CustomDimension9(String _customDimension9) { this._customDimension9 = _customDimension9; }

    public String get_pageRef() { return _pageRef;}
    public String get_requestUrl() { return _requestUrl;}
    public String get_hitType() { return _hitType;}
    public String get_trackingId() {return _trackingId;}
    public String get_documentLocation() { return _documentLocation;}
    public String get_documentTitle() {return _documentTitle;}
    public String get_eventCategory() {return _eventCategory;}
    public String get_eventAction() { return _eventAction;}
    public String get_eventLabel() { return _eventLabel;}
    public String get_contentGroup1() {return _contentGroup1;}
    public String get_contentGroup2() {return _contentGroup2;}
    public String get_customDimension9() {return _customDimension9;}

    public GtmTag() {
        set_ContentGroup1(null);
        set_ContentGroup2(null);
        set_CustomDimension9(null);
        set_DocumentTitle(null);
        set_DocumentLocation(null);
        set_EventCategory(null);
        set_EventAction(null);
        set_EventLabel(null);
        set_HitType(null);
        set_PageRef(null);
        set_RequestUrl(null);
        set_TrackingId(null);
    }

}
