package com.ttsea.jlibrary.photo.select;

import java.io.Serializable;
import java.util.List;

class Folder implements Serializable {
    private String name;
    private String path;
    private ImageItem cover;
    private List<ImageItem> images;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ImageItem getCover() {
        return cover;
    }

    public void setCover(ImageItem cover) {
        this.cover = cover;
    }

    public List<ImageItem> getImages() {
        return images;
    }

    public void setImages(List<ImageItem> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Folder other = (Folder) o;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

    @Override
    public String toString() {

        return "Folder{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", cover=" + cover +
                ", images=" + images.toString() +
                '}';
    }

}