package com.ttsea.jlibrary.sample.demoActivity.async;


import com.ttsea.jlibrary.debug.JLog;
import com.ttsea.jlibrary.mail.MailAttachEntity;
import com.ttsea.jlibrary.mail.MailInfo;
import com.ttsea.jlibrary.mail.MultiMailSender;
import com.ttsea.jlibrary.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送邮件
 */
public class SendEmail {
    private static final String TAG = "SendEmail";

    public static boolean send(String subject, String content) {
        String[] receivers = new String[]{"******@qq.com"};
        String[] ccs = new String[]{"******@qq.com", "******@qq.com"};
        String[] bcc = new String[]{"******@qq.com"};
        String[] attachFiles = new String[]{"/storage/emulated/0/test-image/IMG_0423.JPG", "http://hws002.b0.upaiyun.com/team/2162187/20160820/7f39e67fb78adcbaad955b4466f74fe5"};

        List<MailAttachEntity> attachList = new ArrayList<>();
        for (int i = 0; i < attachFiles.length; i++) {
            MailAttachEntity attachEntity = new MailAttachEntity(attachFiles[i]);
            attachList.add(attachEntity);
        }

        //return sendSingleMail(receivers, ccs, bcc, subject, content, attachList);
        //return sendMultiMail(receivers, ccs, bcc, subject, content, attachList);
        return sendMultiMail(receivers, null, null, subject, content, attachList);
    }

    /**
     * 发送单一邮件，这个方法不会发送附件，并且所有的文字将以文本的形式发送
     *
     * @param receivers   邮件接收这，至少需要一个，可以有多个
     * @param ccs         抄送，可以为空
     * @param bcc         密送，可以为空
     * @param subject     邮件主题
     * @param content     邮件内容
     * @param attachFiles 邮件附件，可以为空
     * @return true发送成功，false发送失败
     */
    public static boolean sendSingleMail(String[] receivers, String[] ccs, String[] bcc,
                                         String subject, String content, List<MailAttachEntity> attachFiles) {
        return send(receivers, ccs, bcc, subject, content, attachFiles, false);
    }

    /**
     * 发送多媒体邮件，这个方法可以发送附件，并且所有的文本将以html的格式发送<br>
     *
     * @param receivers   邮件接收这，至少需要一个，可以有多个
     * @param ccs         抄送，可以为空
     * @param bcc         密送，可以为空
     * @param subject     邮件主题
     * @param content     邮件内容
     * @param attachFiles 邮件附件，可以为空
     * @return true发送成功，false发送失败
     */
    public static boolean sendMultiMail(String[] receivers, String[] ccs, String[] bcc,
                                        String subject, String content, List<MailAttachEntity> attachFiles) {
        return send(receivers, ccs, bcc, subject, content, attachFiles, true);
    }

    /**
     * 执行发送邮件
     *
     * @param receivers   邮件接收这，至少需要一个，可以有多个
     * @param ccs         抄送，可以为空
     * @param bcc         密送，可以为空
     * @param subject     邮件主题
     * @param content     邮件内容
     * @param attachFiles 邮件附件，可以为空
     * @param multi       是否以多媒体样式发送
     * @return true发送成功，false发送失败
     */
    private static boolean send(String[] receivers, String[] ccs, String[] bcc, String subject,
                                String content, List<MailAttachEntity> attachFiles, boolean multi) {
        boolean result = false;

        if (Utils.isEmpty(subject)) {
            JLog.d(TAG, "subject is empty, mail not sent.");
            return result;
        }
        if (Utils.isEmpty(content)) {
            JLog.d(TAG, "content is empty, mail not sent.");
            return result;
        }
        if (receivers == null || receivers.length < 1) {
            JLog.d(TAG, "no receivers, mail not sent.");
            return result;
        }

        // 这个类主要是设置邮件
        MailInfo mailInfo = new MailInfo();
        // 设置发送邮件的服务器信息
        mailInfo.setMailServerHost("smtp.exmail.qq.com");
        mailInfo.setMailServerPort("465");
        mailInfo.setValidate(true);
        mailInfo.setSsl(true);

        //设置发送者信息
        mailInfo.setFromAddress("******@qq.com");
        mailInfo.setUserName("******@qq.com");
        mailInfo.setPassword("88888888");// email password

        // 接收者信息
        mailInfo.setReceivers(receivers);
        // 抄送
        mailInfo.setCcs(ccs);
        //密送
        mailInfo.setBcc(bcc);
        // 邮件内容
        mailInfo.setSubject(subject);
        mailInfo.setContent(content);
        mailInfo.setAttachFiles(attachFiles);

        JLog.d(TAG, "mailInfo:" + mailInfo.toString());

        if (multi) {
            // 以html格式发送邮件
            JLog.d(TAG, "sending a multi email...");

            try {
                MultiMailSender.sendMultiMail(mailInfo);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                JLog.e(TAG, "Exception e:" + e.getMessage());
                result = false;
            }

        } else {

            // 以text格式发送邮件
            JLog.d(TAG, "sending a single email...");
            try {
                MultiMailSender.sendSingleMail(mailInfo);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                JLog.e(TAG, "Exception e:" + e.getMessage());
                result = false;
            }
        }

        return result;
    }
}
