package com.sps.nurul_ikhlas.services;

public interface EmailService {
    void sendAccountCredentials(String toEmail, String studentName, String username, String password);

    void sendActivationLink(String toEmail, String studentName, String username, String token);
}
