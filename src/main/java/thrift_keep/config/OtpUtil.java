package thrift_keep.config;

import java.security.SecureRandom;

public class OtpUtil {
    public static String generateOtp() {
        int otp = 100000 + new SecureRandom().nextInt(900000);
        return String.valueOf(otp);
    }
}
