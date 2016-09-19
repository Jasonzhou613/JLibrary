package com.ttsea.jlibrary.jasynchttp.server.download;

import java.io.Serializable;

/**
 * 下载信息，该信息也存在与数据库中 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/1/5 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/5 10:05
 */
public class DownloadFileInfo extends FileInfo implements Serializable {
    //数据库字段
    private String thread_id;//下载线程的id
    private String title;//标题
    private String description;//描述
    private String add_timestamp;//添加下载时间
    private String last_modified_timestamp;//最后修改时间
    private String media_type;//文件类型
    private int reason;//原因
    private int status;//下载的状态
    private long total_size_bytes;//需下载总长度
    private long bytes_so_far;//已经下载了的长度
    private long start_bytes;//最初开始位置
    private long end_bytes;//结束位置
    private String etag;//E-TAG

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdd_timestamp() {
        return add_timestamp;
    }

    public void setAdd_timestamp(String add_timestamp) {
        this.add_timestamp = add_timestamp;
    }

    public String getLast_modified_timestamp() {
        return last_modified_timestamp;
    }

    public void setLast_modified_timestamp(String last_modified_timestamp) {
        this.last_modified_timestamp = last_modified_timestamp;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTotal_size_bytes() {
        return total_size_bytes;
    }

    public void setTotal_size_bytes(long total_size_bytes) {
        this.total_size_bytes = total_size_bytes;
    }

    public long getBytes_so_far() {
        return bytes_so_far;
    }

    public void setBytes_so_far(long bytes_so_far) {
        this.bytes_so_far = bytes_so_far;
    }

    public long getStart_bytes() {
        return start_bytes;
    }

    public void setStart_bytes(long start_bytes) {
        this.start_bytes = start_bytes;
    }

    public long getEnd_bytes() {
        return end_bytes;
    }

    public void setEnd_bytes(long end_bytes) {
        this.end_bytes = end_bytes;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    @Override
    public String toString() {
        return "DownloadFileInfo{" +
                "thread_id='" + thread_id + '\'' +
                ", url='" + getUrl() + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", add_timestamp='" + add_timestamp + '\'' +
                ", last_modified_timestamp='" + last_modified_timestamp + '\'' +
                ", local_filename='" + getLocal_filename() + '\'' +
                ", local_file_path='" + getLocal_file_path() + '\'' +
                ", media_type='" + media_type + '\'' +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", total_size_bytes=" + total_size_bytes +
                ", bytes_so_far=" + bytes_so_far +
                ", start_bytes=" + start_bytes +
                ", end_bytes=" + end_bytes +
                ", etag='" + etag + '\'' +
                ", FileInfo='" + super.toString() + '\'' +
                '}';
    }
}
