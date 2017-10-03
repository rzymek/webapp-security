package pl.lingaro.od.workshop.security.data;

import java.util.Date;

public interface FileInfo {
    int getId();
    String getFilename();
    Date getTimestamp();
    boolean isPublished();
}
