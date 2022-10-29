package toy.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import toy.user.model.Member;
import toy.user.repository.MemberRepository;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void signUpUser(Member member) {
        // String password = member.getPassword();
        // String salt = saltUtil.getSalt();
        // member.setSalt(new Salt(salt));
        // member.setPassword(saltUtil.encodePassword(salt, password));
        memberRepository.save(member);
    }

    @Override
    public Member loginUser(String id, String password) {
        // TODO Auto-generated method stub
        return null;
    }

    // @Override
    // public Member loginUser(String id, String password) throws Exception {
    //     Member member = memberRepository.findByUsername(id);
    //     if (member == null)
    //         throw new Exception("멤버가 조회되지 않음");
    //     // String salt = member.getSalt().getSalt();
    //     // password = saltUtil.encodePassword(salt, password);
    //     if (!member.getPassword().equals(password))
    //         throw new Exception("비밀번호가 틀립니다.");
    //     return member;
    // }

}