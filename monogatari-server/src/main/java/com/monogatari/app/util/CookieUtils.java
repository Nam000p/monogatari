package com.monogatari.app.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;

@Component
public class CookieUtils {
	@Value("${jwt.refresh-token.expiration}")
    private long refreshTokenDurationMs;

	public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
		return ResponseCookie.from("refreshToken", refreshToken)
				.path("/")
				.maxAge(refreshTokenDurationMs / 1000)
				.httpOnly(true)
				.secure(true)
				.sameSite("None")
				.build();
	}

	public ResponseCookie getCleanJwtRefreshCookie() {
		return ResponseCookie.from("refreshToken", "")
				.path("/")
				.maxAge(0)
				.httpOnly(true)
				.secure(true)
				.sameSite("None")
				.build();
	}

	public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return Optional.of(cookie);
				}
			}
		}
		return Optional.empty();
	}

	public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					cookie.setValue("");
					cookie.setPath("/");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
	}

	public String serialize(Object object) {
		return Base64.getUrlEncoder()
				.encodeToString(SerializationUtils.serialize(object));
	}

	@SuppressWarnings("deprecation")
	public <T> T deserialize(Cookie cookie, Class<T> cls) {
		return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
	}
}