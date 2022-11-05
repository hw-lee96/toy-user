package toy.user.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.user.dto.LoginDto;
import toy.user.dto.TokenDto;
import toy.user.jwt.JwtFilter;
import toy.user.jwt.TokenProvider;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    /**
     * 로그인 진행 api
     * */
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        // username 과 password로 u~어쩌구 auth 객체 생성
        // 메소드 설명: 사용자이름과 비밀번호의 간단한 표시 위함
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // 생성한 u어쩌구 객체로 실질적인 인증 객체를 생성
        // authenticate() 가 실행이 될 때 CustomUserDetailsService의 override 된 loadUserByUsername가 실행이 됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // DB 조회 후 생성한 auth객체를 securityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // json web token 생성
        String jwt = tokenProvider.createToken(authentication);

        // token을 header에 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        // token 값으로 TokenDto를 생성 후 response body에 포함하여 return
        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }
}