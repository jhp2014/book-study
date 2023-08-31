package com.project.bookstudy.security.oauth;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.exception.MemberNotFound;
import com.project.bookstudy.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class KakaoOAuth2MemberService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final DefaultOAuth2UserService userService;  //상속 대신 합성 사용


    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //기존 DefaultOAuth2UserService 로직
        OAuth2User oAuth2User = userService.loadUser(userRequest);

        //Save or Update at DB
        Map<String, Object> memberInfo = oAuth2User.getAttribute("kakao_account");
        String email = (String) memberInfo.get("email");
        String name = (String) ((Map) memberInfo.get("profile")).get("nickname");

        Member member = saveOrUpdate(email, name);

        //Success handler needs Member entity to create access token.
        MemberOAuth2User memberOAuth2User = new MemberOAuth2User(member, oAuth2User);
        return memberOAuth2User;
    }

    private Member saveOrUpdate(String email, String name) {

        Optional<Member> memberOptional = memberRepository.findByEmail(email);

        if (memberOptional.isEmpty()) {
            Member member = memberRepository.save(Member.builder().email(email).name(name).build());
            return member;
        }

        return memberOptional.get();
    }
}
