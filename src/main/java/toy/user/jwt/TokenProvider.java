package toy.user.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * token의 생성 및 token을 이용한 동작을 수행 (auth 객체 생성, 유효성 검사 등)
 * */
@Component
public class TokenProvider implements InitializingBean {

   private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
   private static final String AUTHORITIES_KEY = "auth";
   private final String secret;
   private final long tokenValidityInMilliseconds;
   private Key key;

   public TokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
      this.secret = secret;
      this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
   }

   /**
    * BeanFactory에 의해 모든 property 가 설정되고 난 뒤 실행되는 메소드
    * 주로 실행시점의 custom 초기화 로직이 필요하거나 주입받은 property 를 확인하는 용도로 사용
    * 여기서는 주입받은 secret 값을 decode 한 뒤 key에 할당하기 위해 사용
    */
   @Override
   public void afterPropertiesSet() {
      byte[] keyBytes = Decoders.BASE64.decode(secret);
      this.key = Keys.hmacShaKeyFor(keyBytes);
   }

   /**
    * 사용자의 권한, application.yml에서 설정한 만료 기간 등을 포함한 token을 생성하여 반환
    */
   public String createToken(Authentication authentication) {
      String authorities = authentication.getAuthorities().stream()
         .map(GrantedAuthority::getAuthority)
         .collect(Collectors.joining(","));

      long now = (new Date()).getTime();
      Date validity = new Date(now + this.tokenValidityInMilliseconds);

      return Jwts.builder()
         .setSubject(authentication.getName())
         .claim(AUTHORITIES_KEY, authorities)
         .signWith(key, SignatureAlgorithm.HS512)
         .setExpiration(validity)
         .compact();
   }

   /**
    * 토큰 정보를 이용해 Authentication을 생성하여 리턴
    */
   public Authentication getAuthentication(String token) {
      // 1.token으로 claims 객체 생성
      Claims claims = Jwts
              .parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token)
              .getBody();

      // 2.claims에 있는 권한 정보를 추출
      Collection<? extends GrantedAuthority> authorities =
         Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

      // 3.추출한 권한 정보를 이용해 유저객체 생성
      User principal = new User(claims.getSubject(), "", authorities);

      // 4.유저객체, token, 권한 정보를 포함한 Authentication 객체를 리턴 (Authentication 를 상속함. 인증이 완료 된 후 SecurityContext 에 등록되는 Authentication 객체로 사용됨 )
      return new UsernamePasswordAuthenticationToken(principal, token, authorities);
   }

   /**
    * token 값을 받아서 유효성 검삭 진행
    */
   public boolean validateToken(String token) {
      try {
         // token 값을 parsing 하고, 정상 token이면 return true, 발생하는 에러에 따라 에러 출력 후 return false
         Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
         return true;
      } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
         logger.info("잘못된 JWT 서명입니다.");
      } catch (ExpiredJwtException e) {
         logger.info("만료된 JWT 토큰입니다.");
      } catch (UnsupportedJwtException e) {
         logger.info("지원되지 않는 JWT 토큰입니다.");
      } catch (IllegalArgumentException e) {
         logger.info("JWT 토큰이 잘못되었습니다.");
      }
      return false;
   }
}