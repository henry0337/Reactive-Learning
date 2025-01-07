package dev.quochung2003.reactiveLearning.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dev.quochung2003.reactiveLearning.constant.JwtConstant;
import dev.quochung2003.reactiveLearning.dto.request.ChangePasswordRequest;
import dev.quochung2003.reactiveLearning.dto.request.LoginRequest;
import dev.quochung2003.reactiveLearning.dto.response.LoginResponse;
import dev.quochung2003.reactiveLearning.dto.request.RegisterRequest;
import dev.quochung2003.reactiveLearning.entity.Role;
import dev.quochung2003.reactiveLearning.entity.User;
import dev.quochung2003.reactiveLearning.helper.MailHelper;
import dev.quochung2003.reactiveLearning.mapper.UserMapper;
import dev.quochung2003.reactiveLearning.repository.UserRepository;
import dev.quochung2003.reactiveLearning.utility.JwtUtility;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository repository;
    UserMapper mapper;
    PasswordEncoder encoder;
    ReactiveAuthenticationManager manager;
    MailService mailService;
    MailHelper helper;

    /**
     * Registers a new account on the server.
     *
     * @param request the {@link RegisterRequest} containing the user's credentials to be registered.
     * @return a {@link Mono} emitting the saved {@link User} object if the registration is successful,
     *         or an error {@link Mono} emitting an {@link IllegalStateException} if the email already exists.
     */

    public Mono<?> register(final RegisterRequest request) {
        User user = mapper.requestToModel(request);
        user.setPassword(encoder.encode(request.password()));

        return repository.findByEmail(request.email())
                .flatMap(existingUser -> Mono.error(new IllegalStateException("Email đã tồn tại")))
                .switchIfEmpty(repository.save(user));
    }

    /**
     * Authenticate user credentials with server.
     *
     * @param request User credentials used for authenticating. <br> Can't be {@code null}.
     * @return A successful {@link Mono} that emits {@code token} and {@code refreshToken} in {@link LoginRequest} if success.<br>
     * Otherwise, (maybe) return failed {@link Mono} that emits {@link Exception}.
     */
    public Mono<?> login(final @NonNull LoginRequest request) {
        final var authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        return manager.authenticate(authenticationToken)
                .flatMap(auth -> repository.findByEmail(request.email())
                        .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                        .flatMap(user -> {
                            final String accessToken = JwtUtility.generateToken(user);
                            final String refreshToken = JwtUtility.generateRefreshToken(user);
                            return Mono.just(new LoginResponse(accessToken, refreshToken));
                        })
                );
    }

    /**
     * Changes the user's password using a provided OTP for authentication.
     *
     * <p>This method follows the process below:</p>
     * <ol>
     *   <li>Generates a one-time password (OTP) and sends it to the user's email.</li>
     *   <li>Validates the OTP provided by the user.</li>
     *   <li>Updates the user's password in the database if the OTP is valid.</li>
     *   <li>Returns a {@link LoginResponse} containing a new access token and refresh token.</li>
     * </ol>
     *
     * @param request the {@link ChangePasswordRequest} containing user email, OTP, and new password
     * @return a {@link Mono} emitting a {@link LoginResponse} containing access and refresh tokens
     *         if the password change is successful
     * @throws IllegalStateException if:
     *         <ul>
     *           <li>The email does not exist in the database</li>
     *           <li>The provided OTP is invalid</li>
     *         </ul>
     */
    public Mono<LoginResponse> changePassword(final @NonNull ChangePasswordRequest request) {
        final String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        return repository.findByEmail(request.email())
                .switchIfEmpty(Mono.error(new IllegalStateException("Email không tồn tại")))
                .flatMap(user -> {
                    final String template = """
                            Xin chào người dùng %s!, đây là mã OTP bạn sẽ cần: %d.
                            """.stripIndent();

                    final String subject = "OTP Verification";
                    final String body = String.format(template, request.email(), otp);

                    mailService.sendMail(new String[]{request.email()}, null, null, subject, body, null);
                    return Mono.just("Mã OTP đã được gửi tới email: " + request.email() + " của bạn.");
                })
                .then(Mono.defer(() -> {
                    // Xác thực OTP
                    String typedOtp = request.authString();
                    return helper.validateOtp(otp, typedOtp)
                            .flatMap(isValid -> {
                                if (!isValid) {
                                    return Mono.error(new IllegalStateException("Mã OTP không hợp lệ"));
                                }

                                final String encodedPassword = encoder.encode(request.newPassword());

                                // Cập nhật mật khẩu trong database
                                return repository.findByEmail(request.email())
                                        .flatMap(user -> {
                                            user.setPassword(encodedPassword);
                                            return repository.save(user);
                                        })
                                        .map(updatedUser -> {
                                            final String token = JwtUtility.generateToken(updatedUser);
                                            final String refreshToken = JwtUtility.generateRefreshToken(updatedUser);
                                            return new LoginResponse(token, refreshToken);
                                        });
                            });
                }));
    }

    /**
     * Retrieves user credentials from a token.
     *
     * @param token The token string containing user information, used to extract registered information with the server.
     * @return A {@link Mono} containing a {@link User} object with the user's credential information.
     *
     * @throws IllegalArgumentException If the token is invalid or user information cannot be extracted.
     */
    public Mono<User> obtainUserCredential(final String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            JWSVerifier verifier = new MACVerifier(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            if (!signedJWT.verify(verifier)) {
                throw new IllegalArgumentException("Invalid JWT signature");
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            User user = new User();
            user.setName(claimsSet.getStringClaim("name"));
            user.setEmail(claimsSet.getStringClaim("email"));
            user.setAvatar(claimsSet.getStringClaim("avatar"));
            user.setRole(Role.valueOf(claimsSet.getStringClaim("role")));

            return Mono.just(user);
        } catch (ParseException | JOSEException e) {
            throw new IllegalArgumentException("Error parsing or verifying the JWT", e);
        }
    }
}
