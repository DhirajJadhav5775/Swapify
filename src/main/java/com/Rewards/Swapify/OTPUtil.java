package com.Rewards.Swapify;

import java.util.Random;

public class OTPUtil
{
    public static String generateOTP()
    {
//        Random random = new Random();
//        int otp = 100000 + random.nextInt(899999);
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}
