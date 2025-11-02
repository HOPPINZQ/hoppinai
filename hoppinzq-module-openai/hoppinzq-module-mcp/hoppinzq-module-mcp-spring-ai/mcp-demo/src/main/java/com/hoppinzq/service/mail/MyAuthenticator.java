package com.hoppinzq.service.mail;

import lombok.AllArgsConstructor;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

@AllArgsConstructor
public class MyAuthenticator extends Authenticator {
    String userName = null;
    String password = null;

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}
