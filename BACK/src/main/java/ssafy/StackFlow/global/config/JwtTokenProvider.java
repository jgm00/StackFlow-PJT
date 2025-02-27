package ssafy.StackFlow.global.config;

import io.jsonwebtoken.*;
import javax.crypto.SecretKey;
import java.util.ArrayList;

import io.jsonwebtoken.security.Keys;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtTokenProvider{
    private final SecretKey secretKey;
    private final long validityInMilliseconds = TimeUnit.HOURS.toMillis(10); // 10시간 유효기간

    // SecretKey를 애플리케이션 시작 시 한 번만 생성
    public JwtTokenProvider() {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // 로그인할 때마다 새로운 JWT 토큰 발급 (username 사용)
    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username); // 사용자 ID 저장

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 발급 시간
                .setExpiration(validity) // 만료 시간 설정
                .signWith(secretKey)  // secretKey를 사용하여 서명
                .compact();
    }

    // JWT 토큰에서 username(사용자 ID) 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT 토큰이 만료되었습니다.");
        } catch (MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // JWT 토큰에서 인증 객체 추출 (username 사용)
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject(); //username 사용

        UserDetails userDetails = new User(username, "", new ArrayList<>());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


}


//@Component
//Spring이 자동으로 빈 등록
//@Autowired 또는 @RequiredArgsConstructor로 어디서든 주입해서 사용 가능