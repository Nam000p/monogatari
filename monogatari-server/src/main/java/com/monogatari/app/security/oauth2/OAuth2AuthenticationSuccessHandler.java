package com.monogatari.app.security.oauth2;

import com.monogatari.app.entity.RefreshToken;
import com.monogatari.app.entity.User;
import com.monogatari.app.enums.AuthProvider;
import com.monogatari.app.enums.SystemRole;
import com.monogatari.app.enums.UserStatus;
import com.monogatari.app.repository.UserRepository;
import com.monogatari.app.security.CustomUserDetails;
import com.monogatari.app.security.JwtTokenProvider;
import com.monogatari.app.service.RefreshTokenService;
import com.monogatari.app.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

import static com.monogatari.app.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenService refreshTokenService;

    private final CookieUtils cookieUtils;

    @Value("${app.android.scheme}")
    private String androidScheme;

    @Value("${app.android.host}")
    private String androidHost;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String avatarUrl = oAuth2User.getAttribute("picture");

            if (email == null) {
                throw new IllegalArgumentException("Email not found from Google OAuth2 provider");
            }

            User user = userRepository.findByEmail(email)
                    .map(existingUser -> {
                        if (existingUser.getAuthProvider() == AuthProvider.LOCAL) {
                            existingUser.setAuthProvider(AuthProvider.GOOGLE);
                            return userRepository.save(existingUser);
                        }
                        return existingUser;
                    })
                    .orElseGet(() -> registerNewGoogleUser(email, name, avatarUrl));

            CustomUserDetails userDetails = new CustomUserDetails(user);
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String accessToken = jwtTokenProvider.generateToken(auth);

            refreshTokenService.deleteByUser(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            String targetUrl = cookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                    .map(Cookie::getValue)
                    .orElse(androidScheme + "://" + androidHost + "/oauth2/redirect");

            targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("token", accessToken)
                    .queryParam("refreshToken", refreshToken.getToken())
                    .build().toUriString();

            clearAuthenticationAttributes(request, response);

            log.info("Google Login Success. Redirecting to Android: {}", targetUrl);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            log.error("Google OAuth2 Error: ", e);
            String errorUrl = UriComponentsBuilder.fromUriString(androidScheme + "://" + androidHost + "/oauth2/redirect")
                    .queryParam("error", e.getLocalizedMessage())
                    .build().toUriString();
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        cookieUtils.deleteCookie(request, response, HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        cookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
    }

    private User registerNewGoogleUser(String email, String name, String avatarUrl) {
        User user = User.builder()
                .email(email)
                .username(name != null && name.length() > 50 ? name.substring(0, 47) + "..." : name)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .avatarUrl(avatarUrl)
                .role(SystemRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .authProvider(AuthProvider.GOOGLE)
                .build();
        return userRepository.save(user);
    }
}