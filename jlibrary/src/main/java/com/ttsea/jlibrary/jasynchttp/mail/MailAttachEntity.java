package com.ttsea.jlibrary.jasynchttp.mail;

import java.io.Serializable;

/**
 * 邮件附件实例 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/13 15:37 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class MailAttachEntity implements Serializable {
    String name;
    String path;
    String tag;

    public MailAttachEntity(String path) {
        this.path = path;

        if (path != null && path.lastIndexOf("/") != -1) {
            name = path.substring(path.lastIndexOf("/") + 1);
        }
    }

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "MailAttachEntity{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
