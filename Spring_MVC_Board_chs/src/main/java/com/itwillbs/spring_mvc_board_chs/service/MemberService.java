package com.itwillbs.spring_mvc_board_chs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itwillbs.spring_mvc_board_chs.mapper.MemberMapper;
import com.itwillbs.spring_mvc_board_chs.vo.MemberVO;

@Service
public class MemberService {

	@Autowired
	private MemberMapper mapper;

	// 회원 가입 요청 비즈니스 로직 수행할 registMember() 메서드 정의
	// => 파라미터 : MemberVO 객체    리턴타입 : int
	// => MemberMapper - insertMember()
	public int registMember(MemberVO member) {
		return mapper.insertMember(member);
	}


	public String getPasswd(String id) {
		return mapper.selectPasswd(id);
	}


	public MemberVO getMemberInfo(String id) {
 
		return mapper.selectMember(id);
	}

	public int updateMemberInfo(MemberVO member, String newPasswd) {
		
		return mapper.updateMemberInfo(member, newPasswd);
	}


	public int quitMember(String id) {
		
		return mapper.deleteMember(id);
	}
	
}














