import java.io.File;

public class FileVersionInfo {


    public long GetLastModifiedDate(String fileName) {
        File file = new File(fileName);
        return file.lastModified();
    }

}
