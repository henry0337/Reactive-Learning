package dev.quochung2003.reactiveLearning.utility;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dev.quochung2003.reactiveLearning.constant.JwtConstant;
import dev.quochung2003.reactiveLearning.entity.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@NoArgsConstructor
public final class JwtUtility {

    private static final String ISSUER = "Reactive Learning";
    private static final long TOKEN_EXPIRATION = 3600 * 1000; // 1 hour
    private static final long REFRESH_TOKEN_EXPIRATION = 86400 * 1000; // 24 hours

    public static String generateToken(final @NonNull User user) {
        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + TOKEN_EXPIRATION);

        final var claims = new JWTClaimsSet.Builder()
                .subject(user.getName())
                .issuer(ISSUER)
                .issueTime(now)
                .expirationTime(expiryDate)
                .claim("name", user.getName())
                .claim("email", user.getUsername())
                .claim("avatar", user.getAvatar())
                .claim("role", user.getRole().name())
                .build();

        return signToken(claims);
    }

    public static String generateRefreshToken(final @NonNull User user) {
        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);

        final var claims = new JWTClaimsSet.Builder()
                .subject(user.getName())
                .issuer(ISSUER)
                .issueTime(now)
                .expirationTime(expiryDate)
                .claim("email", user.getUsername())
                .build();

        return signToken(claims);
    }

    private static String signToken(JWTClaimsSet claims) {
        try {
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJWT.sign(new MACSigner(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8)));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error signing the JWT", e);
        }
    }
}
