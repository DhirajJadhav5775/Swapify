package com.Rewards.Swapify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{
    @Autowired
    private JavaMailSender mailSender;

    public void sendOTPEmail(String toEmail, String otp)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
//        message.setSubject("OTP Verification For Swapify");
        message.setSubject("OTP Verification For Swapify");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
    }
}
