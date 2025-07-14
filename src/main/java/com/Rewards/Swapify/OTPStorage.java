package com.Rewards.Swapify;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OTPStorage
{
    private Map<String, String> otpMap = new HashMap<>();

    public void saveOTP(String email, String otp)
    {
        otpMap.put(email,otp);
    }

    public boolean verifyOTP(String email, String otp)
    {
        return otp.equals(otpMap.get(email));
    }

    public void clearOTP(String email)
    {
        otpMap.remove(email);
    }
}
