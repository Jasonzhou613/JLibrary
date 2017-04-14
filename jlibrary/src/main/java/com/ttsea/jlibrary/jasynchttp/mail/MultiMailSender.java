package com.ttsea.jlibrary.jasynchttp.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * 发送邮件给多个接收者、抄送邮件 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class MultiMailSender {

    /**
     * 发送单一邮件，这个方法不会发送附件，并且所有的文字将以文本的形式发送
     *
     * @param mailInfo 待发送邮件的信息
     */
    public static void sendSingleMail(MailInfo mailInfo) throws
            MessagingException, MalformedURLException, FileNotFoundException, UnsupportedEncodingException {

        Message mailMessage = null;
        mailMessage = mailInfoToMessage(mailInfo);
        mailMessage.setText(mailInfo.getContent());
        // 发送邮件
        Transport.send(mailMessage);
    }

    /**
     * 发送多媒体邮件，这个方法可以发送附件，并且所有的文本将以html的格式发送<br>
     *
     * @param mailInfo 待发送邮件的信息
     */
    public static void sendMultiMail(MailInfo mailInfo) throws
            MessagingException, MalformedURLException, FileNotFoundException, UnsupportedEncodingException {

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        Message mailMessage = mailInfoToMessage(mailInfo);
        // 发送邮件
        Transport.send(mailMessage);
    }

    /**
     * 将MailInfo转换成Message
     *
     * @param mailInfo 待发邮件内容及相关信息
     * @return Message
     * @throws MessagingException
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     */
    private static Message mailInfoToMessage(MailInfo mailInfo) throws
            MessagingException, MalformedURLException, UnsupportedEncodingException, FileNotFoundException {

        MyAuthenticator authenticator = null;
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(),
                    mailInfo.getPassword());
        }
        Session session = Session.getInstance(mailInfo.getProperties(), authenticator);

        Message mailMessage = new MimeMessage(session);

        // 设置发送者地址
        Address from = new InternetAddress(mailInfo.getFromAddress());
        mailMessage.setFrom(from);

        // 设置接收地址
        String[] receivers = mailInfo.getReceivers();
        Address[] tos = new InternetAddress[receivers.length];
        for (int i = 0; i < receivers.length; i++) {
            tos[i] = new InternetAddress(receivers[i]);
        }
        // 将所有接收者地址都添加到邮件接收者属性中
        mailMessage.setRecipients(Message.RecipientType.TO, tos);

        // 设置抄送地址
        String[] ccs = mailInfo.getCcs();
        if (ccs != null && ccs.length > 0) {
            Address[] aCcs = new InternetAddress[ccs.length];
            for (int i = 0; i < ccs.length; i++) {
                aCcs[i] = new InternetAddress(ccs[i]);
            }
            mailMessage.setRecipients(Message.RecipientType.CC, aCcs);
        }

        //设置密送地址
        String[] bcc = mailInfo.getBcc();
        if (bcc != null && bcc.length > 0) {
            Address[] aBcc = new InternetAddress[bcc.length];
            for (int i = 0; i < bcc.length; i++) {
                aBcc[i] = new InternetAddress(bcc[i]);
            }
            mailMessage.setRecipients(Message.RecipientType.BCC, aBcc);
        }

        Multipart mainPart = new MimeMultipart();

        //设置主题
        mailMessage.setSubject(mailInfo.getSubject());
        //设置时间
        mailMessage.setSentDate(new Date());
        //设置内容
        BodyPart contentPart = new MimeBodyPart();
        contentPart.setContent(mailInfo.getContent(), "text/html; charset=UTF-8");
        mainPart.addBodyPart(contentPart);

        //如果有附件则需要将附件带上
        List<MailAttachEntity> attachFiles = mailInfo.getAttachFiles();
        for (int i = 0; attachFiles != null && i < attachFiles.size(); i++) {
            MailAttachEntity attach = attachFiles.get(i);
            String filePath = attach.getPath();
            BodyPart attachFilesPart = new MimeBodyPart();

            //设置文件源
            if (filePath.toLowerCase().startsWith("http:")
                    || filePath.toLowerCase().startsWith("https:")) {
                URL url = new URL(attach.getPath());
                attachFilesPart.setDataHandler(new DataHandler(url));

            } else {
                File file = new File(attach.getPath());
                if (!file.exists()) {
                    throw new FileNotFoundException("file not found, file:" + file.getAbsolutePath());
                }
                FileDataSource fds = new FileDataSource(file);
                attachFilesPart.setDataHandler(new DataHandler(fds));
            }
            //设置文件名
            if (attach.getName() != null && attach.getName().trim().length() > 0) {
                attachFilesPart.setFileName(MimeUtility.encodeText(attach.getName()));
            }

            mainPart.addBodyPart(attachFilesPart);
        }
        mailMessage.setContent(mainPart);

        return mailMessage;
    }
}