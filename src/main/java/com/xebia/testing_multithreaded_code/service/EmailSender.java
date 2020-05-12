package com.xebia.testing_multithreaded_code.service;

public class EmailSender {

    public void sendEmail(
            String subject, String body) {
        System.out.println("Successfully Sent mail !! with " + subject + " and body " + body);
    }

}
