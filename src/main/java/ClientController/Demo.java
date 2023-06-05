package ClientController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class Demo {

    public static void main(String[] args) {
        // 生成256位的随机密钥
        byte[] keyBytes = generateRandomKey(32);

        // 将字节数组转换为SecretKey对象
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        // 输出生成的密钥
        System.out.println("Generated Key: " + secretKey);

        int userId = 10;

//        String secretKey = "secretKey----";
//        String token = Jwts.builder()
//                .setSubject(String.valueOf(userId))
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
        String token = "kakdkskd";

        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            String userIdStr = claims.getSubject();
//        int userId = Integer.parseInt(userIdStr);

            System.out.println(userIdStr);
        }
        catch (Exception e){
            System.out.println(e);
        }



    }

    private static byte[] generateRandomKey(int keyLength) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[keyLength];
        secureRandom.nextBytes(keyBytes);
        return keyBytes;
    }


}
