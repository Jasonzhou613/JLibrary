package com.ttsea.jlibrary.jasynchttp.mail;


import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.utils.Utils;

/**
 * 发送邮件
 */
public class SendEmail {
    private static final String TAG = "SendEmail";

    public static boolean send(String subject, String content) {
        String[] receivers = new String[]{"***@qq.com"};
        // String[] ccs = new String[] { };

        return send(subject, content, receivers, null);
    }

    /**
     * 执行发送邮件
     *
     * @param subject   邮件主题
     * @param content   邮件内容
     * @param receivers 邮件接收这，至少需要一个，可以有多个
     * @param ccs       抄送，可以为空
     * @return
     * @author Jason
     */
    public static boolean send(String subject, String content, String[] receivers, String[] ccs) {
        boolean isSended = false;

        if (Utils.isEmpty(subject)) {
            JLog.d(TAG, "send, subject is empty");
            return false;
        }
        if (Utils.isEmpty(content)) {
            JLog.d(TAG, "send, content is empty");
            return false;
        }
        if (receivers == null || receivers.length < 1) {
            JLog.d(TAG, "send, no receivers");
            return false;
        }
        // 这个类主要是设置邮件
        MultiMailSenderInfo mailInfo = new MultiMailSenderInfo();
        // 设置发送邮件的服务器信息
        mailInfo.setMailServerHost("smtp.exmail.qq.com");
        mailInfo.setMailServerPort("465");
        mailInfo.setFromAddress("***@qq.com");
        mailInfo.setUserName("***@qq.com");
        mailInfo.setPassword("888888");// email password
        mailInfo.setValidate(true);
        mailInfo.setSsl(true);

        String toAddress = receivers[0];
        // 接收者信息
        mailInfo.setToAddress(toAddress);
        // 邮件内容
        mailInfo.setSubject(subject);
        mailInfo.setContent(content);

        String[] moreRecievers = null;
        if (receivers.length > 1) {// 有两个接受者以上
            moreRecievers = new String[(receivers.length - 1)];
            // 除了第一个，其他的都赋值给moreRecievers
            for (int i = 1; i < receivers.length; i++) {
                moreRecievers[i - 1] = receivers[i];
            }
        }

        // 更多的接收者
        if (moreRecievers != null && moreRecievers.length > 0) {
            mailInfo.setReceivers(moreRecievers);
        }

        // 抄送
        if (ccs != null && ccs.length > 0) {
            mailInfo.setCcs(ccs);
        }

        // 这里主要来发送邮件
        JLog.d(TAG, "sending email as html...");

        if (MultiMailsender.sendTextMail(mailInfo)) {
            JLog.d(TAG, "sendHtmlMail successful.");// 发送html格式
            isSended = true;
        } else {
            JLog.d(TAG, "sendHtmlMail failed.");
            isSended = true;
        }
        JLog.d(TAG, "sended.");

        if (mailInfo.getCcs() != null && mailInfo.getCcs().length > 0) {
            if (MultiMailsender.sendTextMail(mailInfo)) { // 发送抄送，也以html形式发送
                JLog.d(TAG, "send cc as html successful.");
            } else {
                JLog.d(TAG, "send cc as html failed.");
            }
        }

        return isSended;
    }
}
