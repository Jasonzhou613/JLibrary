package com.ttsea.jlibrary.jasynchttp.server.download;

import java.io.Serializable;

/**
 * 文件信息 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/5/6 14:32 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/6 14:32
 */
class FileInfo implements Serializable {
    private String url;//文件下载地址
    private String local_filename;//本地保存文件的文件名
    private String local_file_path;//本地保存路径

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocal_filename() {
        return local_filename;
    }

    public void setLocal_filename(String local_filename) {
        this.local_filename = local_filename;
    }

    public String getLocal_file_path() {
        return local_file_path;
    }

    public void setLocal_file_path(String local_file_path) {
        this.local_file_path = local_file_path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FileInfo fileInfo = (FileInfo) o;

        return url != null ? url.equals(fileInfo.url) : fileInfo.url == null;

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "url='" + url + '\'' +
                ", local_filename='" + local_filename + '\'' +
                ", local_file_path='" + local_file_path + '\'' +
                '}';
    }
}
