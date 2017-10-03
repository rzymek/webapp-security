package pl.lingaro.od.workshop.security.data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class Upload implements FileInfo {
    private String id;
    private String filename;
    private byte[] contents;
    private Date timestamp = new Date();
    private String owner;
    private String description;
    private boolean published = false;

    @Override
    @Id
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public String getFilename() {
        return filename;
    }

    @NotNull
    public String getOwner() {
        return owner;
    }

    @Override
    @NotNull
    public Date getTimestamp() {
        return timestamp;
    }

    @Basic(fetch = FetchType.LAZY)
    @Lob
    public byte[] getContents() {
        return contents;
    }

    @Lob
    public String getDescription() {
        return description;
    }

    @Override
    @NotNull
    public boolean isPublished() {
        return published;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
