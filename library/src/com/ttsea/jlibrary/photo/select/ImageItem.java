package com.ttsea.jlibrary.photo.select;

import com.ttsea.jlibrary.utils.Utils;

import java.io.File;
import java.io.Serializable;

public class ImageItem implements Serializable {
    private String path;
    private String name;
    private String tag;
    private long time;
    private boolean isSelected = false;

    public ImageItem(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    public ImageItem(String path) {
        this.path = path;

        File file = new File(path);
        if (file.exists()) {
            this.name = file.getName();
            this.time = file.lastModified();
        } else {
            this.time = 0;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isNetWorkImage() {
        if (Utils.isEmpty(path)) {
            return false;
        }
        if (path.toLowerCase().startsWith("http")
                || path.toLowerCase().startsWith("https")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ImageItem item = (ImageItem) o;

        return path != null ? path.equals(item.path) : item.path == null;

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}