package toy.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import toy.user.model.Member;
import toy.user.model.Response;
import toy.user.service.AuthService;

@RestController
@RequestMapping("/user")
public class MemberRestController {

    @Autowired
    private AuthService authService;

    @PostMapping("/test")
    public Response test(){
        Response response = new Response();

        System.out.println("member init");

        try{
            response.setResponse("success");
            response.setMessage("test");
        }
        catch(Exception e){
            response.setResponse("failed");
            response.setMessage("회원가입을 하는 도중 오류가 발생했습니다.");
            response.setData(e.toString());
        }

        return response;
    }
    

    @PostMapping("/signup")
    public Response signUpUser(@RequestBody Member member){
        Response response = new Response();

        System.out.println("member : " + member.toString());

        try{
            authService.signUpUser(member);
            response.setResponse("success");
            response.setMessage("회원가입을 성공적으로 완료했습니다.");
        }
        catch(Exception e){
            response.setResponse("failed");
            response.setMessage("회원가입을 하는 도중 오류가 발생했습니다.");
            response.setData(e.toString());
        }

        return response;
    }
}