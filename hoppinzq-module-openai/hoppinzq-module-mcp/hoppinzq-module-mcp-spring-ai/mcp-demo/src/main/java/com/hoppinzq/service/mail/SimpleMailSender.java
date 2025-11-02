package com.hoppinzq.service.mail;

import org.apache.commons.io.IOUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

import static cn.hutool.core.util.ClassLoaderUtil.getClassLoader;

public class SimpleMailSender {

    private static final String smtp = "smtp.163.com";
    private static final String port = "25";
    private static final String fromEmail = "anmiezata@163.com";
    private static final String password = "XXPGRJMTYQVMTVQF";


    public static String sendEmail(String toEmail, String content) {
        toEmail = toEmail.replaceAll("；", ";").replaceAll(" ", "");
        String[] emailAddresses = toEmail.split(";");
        String title = "你好，这里是HOPPINAI给您发送的邮件！";
        String href = "http://localhost:8080/MVN_AM_war/main/index";
        String btnText = "进入审批页面";

        try (InputStream resource = getClassLoader().getResourceAsStream("email.html")) {
            String emailContent = IOUtils.toString(resource, StandardCharsets.UTF_8);
            String emailTemplate = emailContent.replace("${content}", content)
                    .replace("${href}", href)
                    .replace("${btnText}", btnText);

            for (String address : emailAddresses) {
                SimpleMailSender.sendEmail(smtp, port, fromEmail, password, address, title, emailTemplate);
            }
            return "发送成功！";
        } catch (Exception e) {
            return "发送失败：" + e.getMessage();
        }
    }

    public static void sendEmail(String smtp, String port, String email, String password, String toEmail, String title, String content) throws Exception {
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost(smtp);
        mailInfo.setMailServerPort(port);
        mailInfo.setValidate(true);
        mailInfo.setUserName(email);
        mailInfo.setPassword(password);
        mailInfo.setFromAddress(email);
        mailInfo.setToAddress(toEmail);
        mailInfo.setSubject(title);
        mailInfo.setContent(content);
        SimpleMailSender sms = new SimpleMailSender();
        sms.sendHtmlMail(mailInfo);
    }

    /**
     * 以HTML格式发送邮件
     *
     * @param mailInfo 待发送的邮件信息
     */
    public boolean sendHtmlMail(MailSenderInfo mailInfo) throws Exception {
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        Session sendMailSession = Session.getInstance(pro, authenticator);
        Message mailMessage = new MimeMessage(sendMailSession);
        Address from = new InternetAddress(mailInfo.getFromAddress());
        mailMessage.setFrom(from);
        Address to = new InternetAddress(mailInfo.getToAddress());
        mailMessage.setRecipient(Message.RecipientType.TO, to);
        mailMessage.setSubject(mailInfo.getSubject());
        mailMessage.setSentDate(new Date());
        Multipart mainPart = new MimeMultipart();
        BodyPart html = new MimeBodyPart();
        html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
        mainPart.addBodyPart(html);
        mailMessage.setContent(mainPart);
        Transport.send(mailMessage);
        return true;
    }
}
