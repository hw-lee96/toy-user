package toy.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;
import toy.user.jwt.JwtAccessDeniedHandler;
import toy.user.jwt.JwtAuthenticationEntryPoint;
import toy.user.jwt.JwtSecurityConfig;
import toy.user.jwt.TokenProvider;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
            TokenProvider tokenProvider,
            CorsFilter corsFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/h2-console/**"
                , "/favicon.ico"
                , "/error");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                /*
                    Cross site Request forgery(위조 요청).
                    정상적인 사용자가 의도치 않은 위조요청을 보내는 것에 대한 보호 설정으로
                    GET요청을 제외한 상태를 변화시킬 수 있는 POST, PUT, DELETE 요청으로부터 보호한다.

                    현재 restApi로 사용할 목적이며, client 에서 요청 시에는 인증 정보(oauth2, jwt등)를 무조건 포함해야하기 때문에
                    불피요한 동작이 될 csrf를 disable
                */
                .csrf().disable()

//                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()    // 예외처리를 핸들링하게 함
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패에 대한 처리로 custom entryPoint를 등록
                .accessDeniedHandler(jwtAccessDeniedHandler) // 인가되지 않은(권한이 없는) 요청에 대한 처리

                // enable h2-console
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()       // 동일한 origin에서의 요청을 허용

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 생성도, 기존것도 사용하지 않음

                // 요청에 대한 제한을 설정
                .and()
                    .authorizeRequests()
                        .antMatchers("/api/hello").permitAll()
                        .antMatchers("/api/authenticate").permitAll()
                        .antMatchers("/api/signup").permitAll()

                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfig(tokenProvider));

        return httpSecurity.build();
    }
}