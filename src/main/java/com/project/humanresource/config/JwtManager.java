
package com.project.humanresource.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JwtManager {

    @Value("${my-jwt.secret-key}")
    private String secretKey;
    private String issuer = "MuhammetHOCA";
    private Long expirationDate = 1000L * 60 * 60 * 5;
    public String createToken(Long userId, List<String> roles) {
        Long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + expirationDate);
        Algorithm algorithm = Algorithm.HMAC512(secretKey);

        return JWT.create()
                .withAudience()
                .withIssuer(issuer)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiration)
                .withClaim("userId", userId)
                .withClaim("roles", roles) // ✅ burada roles kullanılabilir çünkü parametrede var
                .withClaim("ETicaret", "Yeni bir uygulama yazdık")
                .withClaim("log", "date and hour " + new Date())
                .sign(algorithm);
    }



    public Optional<Long> validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC512(secretKey);
            JWTVerifier verifier =  JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token); // aslında hem token bize mi ait hemde süresi doldu mu?
            if(Objects.isNull(decodedJWT)) // eğer decotedjwt boş ise
                return Optional.empty();
            Long userId = decodedJWT.getClaim("userId").asLong(); // ilgili claim nesnesini long olarak al
            return Optional.of(userId); // değeri optional olarak döndür.
        }catch (Exception exception){
            return Optional.empty();
        }
    }

    public Optional<DecodedJWT> decodeToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return Optional.of(verifier.verify(token));
        } catch (Exception e) {
            return Optional.empty();
        }
    }


}
