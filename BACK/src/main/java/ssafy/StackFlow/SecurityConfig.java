package ssafy.StackFlow;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // CORS 설정 적용
                .csrf(csrf->csrf.disable()) // CSRF 비활성화 (API 사용 시 필수)
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/api/rt/submit").permitAll()
                        .requestMatchers("/user/signup/**").permitAll()  // 회원가입 URL 허용
                        .requestMatchers("/api/user/signup/**").permitAll()  // API 회원가입 URL 허용

                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // ADMIN 역할만 접근 가능
                        .requestMatchers("/notice/create").hasRole("ADMIN")  // ADMIN 역할만 공지사항 글쓰기 가능
                        .requestMatchers("/notice/api").permitAll() // 공지사항 목록 조회
                        .requestMatchers("/notice/api/**").permitAll() // 공지사항 상세 조회
                        .requestMatchers("/notice/api/create").permitAll() // 공지사항 생성 API 로그인 없이 접근 허용

                        .requestMatchers("/api/user/login/**").permitAll()  // API 로그인 URL 허용

                        .requestMatchers("/admin/**", "/store/**").hasRole("ADMIN") // admin과 store 관련 URL은 ADMIN 권한 필요
                        .requestMatchers("/admin/registerStore", "/admin/registerStore/**").hasRole("ADMIN") // 매장 등록 URL 명시적 허용
                        .requestMatchers("/api/admin/category/**").hasRole("ADMIN") // 카테고리 등록
                        .requestMatchers("/chat/**").permitAll()
                        .anyRequest().authenticated()) // 나머지 요청은 인증 필요

//                .csrf((csrf) -> csrf
//                        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"),
//                                new AntPathRequestMatcher("/api/rt/submit")))
//                .headers((headers) -> headers
//                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
//                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                // 로그인 설정
                .formLogin((formLogin) -> formLogin
                        .loginPage("/user/login") // 로그인 페이지 URL
                        .successHandler(successHandler()) // 성공 핸들러 설정
                        .permitAll())
                // 로그아웃 설정
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)); // CORS 설정 추가
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) {
                response.sendRedirect("/admin"); // 관리자 페이지로 리다이렉트
            } else {
                response.sendRedirect("/"); // 일반 사용자 페이지로 리다이렉트
            }
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}