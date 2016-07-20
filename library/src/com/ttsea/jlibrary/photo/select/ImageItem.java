package com.ttsea.jlibrary.photo.select;

import com.ttsea.jlibrary.utils.Utils;

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
        try {
            ImageItem other = (ImageItem) o;
            return this.path.equalsIgnoreCase(other.path);

        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", tag='" + tag + '\'' +
                ", time=" + time +
                ", isSelected=" + isSelected +
                '}';
    }
}