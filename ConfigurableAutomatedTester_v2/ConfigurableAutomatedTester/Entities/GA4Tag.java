import java.util.ArrayList;
import java.util.List;

public class GA4Tag {

    private String _pageRef;            //page number starts at 0
    private String _requestUrl;         //Google URL with all information in parameters
    private String _hitType;            //Event or PageView
    private String _trackingId;         //GA Tracking Id
    private String _documentLocation;   //Actual Page URL (page where event happened)
    private String _documentTitle;      //Document Title
    private String _gtmTagName;
    private String _eventName;

    private String _siteSection;
    private String _pageTemplate;
    private String _hitTimeStamp;
    private String _idField;

    private String _productName;

    private List<GA4Parameter> GA4Parameters;

    public void set_PageRef(String _pageRef) { this._pageRef = _pageRef; }
    public void set_RequestUrl(String _requestUrl) { this._requestUrl = _requestUrl; }
    public void set_HitType(String _hitType) { this._hitType = _hitType; }
    public void set_TrackingId(String _trackingId) { this._trackingId = _trackingId; }
    public void set_DocumentLocation(String _documentLocation) { this._documentLocation = _documentLocation; }
    public void set_DocumentTitle(String _documentTitle) { this._documentTitle = _documentTitle; }
    public void set_GtmTagName(String _gtmTagName) { this._gtmTagName = _gtmTagName; }
    public void set_EventName(String _eventName) { this._eventName = _eventName; }
    public void set_SiteSection(String _siteSection) { this._siteSection = _siteSection; }
    public void set_PageTemplate(String _pageTemplate) { this._pageTemplate = _pageTemplate; }
    public void set_HitTimeStamp(String _hitTimeStamp) { this._hitTimeStamp = _hitTimeStamp; }
    public void set_IdField(String _idField) { this._idField = _idField; }
    public void set_ProductName(String _productName) { this._productName = _productName; }
    public void set_GA4Parameters(List<GA4Parameter> _ga4Parameters) {this.GA4Parameters = _ga4Parameters;}

    public String get_requestUrl() { return _requestUrl;}
    public String get_hitType() { return _hitType;}
    public String get_trackingId() {return _trackingId;}
    public String get_documentLocation() { return _documentLocation;}
    public String get_documentTitle() {return _documentTitle;}
    public String get_gtmTagName() {return _gtmTagName;}
    public String get_eventName() {return _eventName;}

    public String get_siteSection() {return _siteSection;}
    public String get_pageTemplate() {return _pageTemplate;}
    public String get_hitTimeStamp() {return _hitTimeStamp;}
    public String get_idField() {return _idField;}

    public String get_productName() {return _productName;}

    public List<GA4Parameter> getGA4Parameters() { return GA4Parameters;}
    public GA4Parameter getGA4Parameter(int index) { return GA4Parameters.get(index);}



    public GA4Tag() {
        set_PageRef(null);
        set_RequestUrl(null);
        set_HitType(null);
        set_TrackingId(null);
        set_DocumentLocation(null);
        set_DocumentTitle(null);
        set_GtmTagName(null);
        set_EventName(null);
        set_SiteSection(null);
        set_PageTemplate(null);
        set_HitTimeStamp(null);
        set_IdField(null);
        set_ProductName(null);
        GA4Parameters = new ArrayList<>();
        //List<GA4Parameter> ga4Parameters = new ArrayList<>();
    }
}
