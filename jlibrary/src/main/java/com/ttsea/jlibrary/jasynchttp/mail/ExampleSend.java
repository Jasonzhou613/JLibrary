package com.ttsea.jlibrary.jasynchttp.mail;

/**
 * 代码事例
 */
public class ExampleSend {

    public static void main(String[] args) {
        System.out.println("Yeah, this is a send email exmaple!");
        // sendMail();
    }

    public static void sendMail() {
        // 这个类主要是设置邮件
        MultiMailSenderInfo mailInfo = new MultiMailSenderInfo();
        // 设置发送邮件的服务器信息
        mailInfo.setMailServerHost("smtp.sina.com");
        mailInfo.setMailServerPort("25");
        mailInfo.setValidate(true);
        mailInfo.setUserName("user@sina.com");
        mailInfo.setPassword("password");// email password

        // 发送者信息
        mailInfo.setFromAddress("fromaddress@sina.com");
        // 接收者信息
        mailInfo.setToAddress("toaddress@qq.com");
        // 邮件内容
        mailInfo.setSubject("Hi,Jason. Html.");
        mailInfo.setContent("<h1><a href=\"http://www.ttsea.com\" target=\"_blank\">设置邮件 </a></h1>");

        // 更多的接收者
        String[] receivers = new String[] { "moreaddress@sina.com" };
        // 抄送
        String[] ccs = new String[] { "ccsaddress@sina.com" };
        mailInfo.setReceivers(receivers);
        mailInfo.setCcs(ccs);

        // 这个类主要来发送邮件
        System.out.println("sending mail as text...");
        if (MultiMailsender.sendTextMail(mailInfo))// 发送文体格式
        {
            System.out.println("sendTextMail Successful.");
        } else {
            System.out.println("sendTextMail failed.");
        }
        System.out.println("sended.");

        if (MultiMailsender.sendTextMailtoMultiCC(mailInfo)) {//
            // 发送抄送，也以文本形式发送
            System.out.println("send cc as text Successful.");
        } else {
            System.out.println("send cc as text failed.");
        }
        // ///////////////////////////////
        System.out.println("sending mail as html...");
        if (MultiMailsender.sendHtmlMail(mailInfo)) {
            System.out.println("sendHtmlMail Successful.");// 发送html格式
        } else {
            System.out.println("sendHtmlMail failed.");
        }
        System.out.println("sended.");

        if (MultiMailsender.sendHtmlMailtoMultiCC(mailInfo)) {//
            // 发送抄送，也以html形式发送
            System.out.println("send cc as html Successful.");
        } else {
            System.out.println("send cc as html failed.");
        }
    }
}
