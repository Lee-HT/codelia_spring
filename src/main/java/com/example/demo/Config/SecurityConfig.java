package com.example.demo.Config;

import com.example.demo.Config.Cookie.CookieProvider;
import com.example.demo.Config.Jwt.JwtAuthenticationFilter;
import com.example.demo.Config.Jwt.TokenProvider;
import com.example.demo.Config.Oauth2.OAuth2Service;
import com.example.demo.Config.Oauth2.OAuth2SuccessHandler;
import com.example.demo.Config.Oauth2.OAuth2FailureHandler;
import com.example.demo.Config.Oauth2.OAuth2LogoutHandler;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2Service oAuth2Service;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final OAuth2LogoutHandler oAuth2LogoutHandler;
    private final TokenProvider tokenProvider;
    private final CookieProvider cookieProvider;

    public SecurityConfig(OAuth2Service oAuth2Service, OAuth2SuccessHandler oAuth2SuccessHandler,
            OAuth2FailureHandler oAuth2FailureHandler, OAuth2LogoutHandler oAuth2LogoutHandler,
            TokenProvider tokenProvider, CookieProvider cookieProvider) {
        this.oAuth2Service = oAuth2Service;
        this.oAuth2FailureHandler = oAuth2FailureHandler;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.oAuth2LogoutHandler = oAuth2LogoutHandler;
        this.tokenProvider = tokenProvider;
        this.cookieProvider = cookieProvider;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                // 정적 리소스 시큐리티 예외 처리
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                // 권한 필요 없는 path
                .requestMatchers("/");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // SpringSecurity 6.1.2
        // https://docs.spring.io/spring-security/reference/migration-7/configuration.html
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);

        // 기존, 신규 세션 모두 사용 안함
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 권한 설정
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login/oauth2/test/*").permitAll()
                .requestMatchers("/oauth2/authorization/*").permitAll()
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated()
        );

        // spring security 의 authorization uri 생성 uri
        // http://localhost:6550/oauth2/authorization/{registration_id}
        http.oauth2Login(oauth2 -> oauth2
                // oauth2 로그인 요청 uri
                .authorizationEndpoint(end -> end
                        .baseUri("/oauth2/authorization"))
                // redirect uri
                .redirectionEndpoint(end -> end
                        .baseUri("/login/oauth2/code/*"))
                //.loginPage("/login")
                // defaultSuccessUrl 과 동시 사용 x
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
                .userInfoEndpoint(userIEP -> userIEP.userService(oAuth2Service)));
        http.logout(logout -> logout
                .logoutSuccessHandler(oAuth2LogoutHandler));

        http.addFilterBefore(new JwtAuthenticationFilter(tokenProvider, cookieProvider),
                UsernamePasswordAuthenticationFilter.class);

        // 스레드 별로 SecurityContext 저장 (Default Mode)
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);

        return http.build();
    }

}
