package com.hoppinzq.service.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Properties;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailSenderInfo {
    private String mailServerHost;
    private String mailServerPort = "25";
    private String fromAddress;
    private String toAddress;
    private String userName;
    private String password;
    private boolean validate = false;
    private String subject;
    private String content;
    private String[] attachFileNames;

    /**
     * 获得邮件会话属性
     */
    public Properties getProperties() {
        Properties p = new Properties();
        p.put("mail.smtp.host", this.mailServerHost);
        p.put("mail.smtp.port", this.mailServerPort);
        p.put("mail.smtp.auth", validate ? "true" : "false");
        return p;
    }
}
