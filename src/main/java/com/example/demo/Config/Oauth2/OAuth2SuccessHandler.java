package com.example.demo.Config.Oauth2;

import com.example.demo.Config.Cookie.CookieProvider;
import com.example.demo.Config.Jwt.JwtProperties;
import com.example.demo.Config.Jwt.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CookieProvider cookieProvider;
    private final TokenProvider tokenProvider;
    private final int REFRESH_TOKEN_EXPIRE = (int) (JwtProperties.refreshTime / 1000);
    private final int ACCESS_TOKEN_EXPIRE = (int) (JwtProperties.accessTime / 1000);

    @Autowired
    public OAuth2SuccessHandler(CookieProvider cookieProvider, TokenProvider tokenProvider) {
        this.cookieProvider = cookieProvider;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        log.info("get Id : " + ((DefaultOAuth2User) authentication.getPrincipal()).getName());
        Map<String,Object> attributes = ((DefaultOAuth2User) authentication.getPrincipal()).getAttributes();
        log.info("SuccessHandler attribute : " + attributes.toString());
        String username = (String) attributes.get("username");
        String provider = (String) attributes.get("provider");
        String accessToken = tokenProvider.getAccessToken(username,provider);
        String refreshToken = tokenProvider.getRefreshToken(username,provider);

        Cookie access = cookieProvider.getCookie(JwtProperties.accessTokenName, accessToken,
                ACCESS_TOKEN_EXPIRE);
        Cookie refresh = cookieProvider.getCookie(JwtProperties.refreshTokenName, refreshToken,
                REFRESH_TOKEN_EXPIRE);
        response.addCookie(access);
        response.addCookie(refresh);

        String targetUrl = getTargetUrl("/");
        log.info(targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String getTargetUrl(String url) {
        return UriComponentsBuilder.fromUriString(url)
                .build().toString();
    }
}
