package com.project.humanresource.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.humanresource.dto.JwtUserData;
import com.project.humanresource.utility.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Component      // hocaya sor bunu component olmadan jwt token filter clasındaki jwtManager kırmızı oluyor.
public class JwtManager {
    @Value("aspfd23423jdıgho25590fsş")
    private String secretKey;

    private Long expirationDate=1000L*60*60*2;
    public String createToken(Long userId, String email, UserStatus role) {
        String token = "";
        Long now = System.currentTimeMillis();
        Date issureAt = new Date(now);
        Date expiration = new Date(now + expirationDate);
        Algorithm algorithm = Algorithm.HMAC512(secretKey);
        token = JWT.create()
                .withIssuedAt(issureAt)
                .withExpiresAt(expiration)
                .withClaim("userId", userId)
                .withClaim("email",email)
                .withClaim("role",role.name())
                .sign(algorithm);
        return token;
    }

    public Optional<JwtUserData> validateToken(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC512(secretKey);
            JWTVerifier verifier=JWT.require(algorithm).build();
            DecodedJWT decodedJWT=verifier.verify(token);
            if (Objects.isNull(decodedJWT))
                return Optional.empty();
            Long userId=decodedJWT.getClaim("userId").asLong();
            String email= decodedJWT.getClaim("email").asString();
            UserStatus role=UserStatus.valueOf(decodedJWT.getClaim("role").asString());
            return Optional.of(new JwtUserData(userId,email,role));
        }catch (Exception exception){
            return Optional.empty();
        }
    }


//    public String createToken(Long userId) {
//        String token = "";
//        Long now = System.currentTimeMillis();
//        Date issureAt = new Date(now);
//        Date expiration = new Date(now + expirationDate);
//        Algorithm algorithm = Algorithm.HMAC512(secretKey);
//        token = JWT.create()
//                .withIssuedAt(issureAt)
//                .withExpiresAt(expiration)
//                .withClaim("userId", userId)
//                .withClaim("log", "Time right now" + (new Date()))
//                .sign(algorithm);
//        return token;
//    }
//
//    public Optional<Long> validateToken(String token) {
//        try{
//            Algorithm algorithm = Algorithm.HMAC512(secretKey);
//            JWTVerifier verifier=JWT.require(algorithm).build();
//            DecodedJWT decodedJWT=verifier.verify(token);
//            if (Objects.isNull(decodedJWT))
//                return Optional.empty();
//            Long userId=decodedJWT.getClaim("userId").asLong();
//            return Optional.of(userId);
//        }catch (Exception exception){
//            return Optional.empty();
//        }
//    }

}
