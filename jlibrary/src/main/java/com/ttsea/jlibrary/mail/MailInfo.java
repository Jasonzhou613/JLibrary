package com.ttsea.jlibrary.mail;

import java.util.Arrays;
import java.util.List;

/**
 * 邮件信息 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class MailInfo extends SenderInfo {
    // 邮件主题
    private String subject;
    // 邮件的文本内容
    private String content;
    // 邮件附件的文件名
    private List<MailAttachEntity> attachFiles;

    // 邮件的接收者，可以有多个
    private String[] receivers;
    // 邮件的抄送者，可以有多个
    private String[] ccs;
    // 密送，可以有多个
    private String[] bcc;

    public MailInfo() {
        super();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<MailAttachEntity> getAttachFiles() {
        return attachFiles;
    }

    public void setAttachFiles(List<MailAttachEntity> attachFiles) {
        this.attachFiles = attachFiles;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

    public String[] getCcs() {
        return ccs;
    }

    public void setCcs(String[] ccs) {
        this.ccs = ccs;
    }

    public String[] getBcc() {
        return bcc;
    }

    public void setBcc(String[] bcc) {
        this.bcc = bcc;
    }

    @Override
    public String toString() {
        return "MailInfo{" +
                "subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", attachFiles=" + attachFiles +
                ", receivers=" + Arrays.toString(receivers) +
                ", ccs=" + Arrays.toString(ccs) +
                ", bcc=" + Arrays.toString(bcc) +
                '}';
    }
}
