package com.ttsea.jlibrary.jasynchttp.mail;

/**
 * 发送多接收者类型邮件的基本信息 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2014.03.07 18:09 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2014.03.07 18:09
 */
public class MultiMailSenderInfo extends MailSenderInfo {

    public MultiMailSenderInfo() {

    }

    // 邮件的接收者，可以有多个
    private String[] receivers;
    // 邮件的抄送者，可以有多个
    private String[] ccs;

    public String[] getCcs() {
        return ccs;
    }

    public void setCcs(String[] ccs) {
        this.ccs = ccs;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }
}
