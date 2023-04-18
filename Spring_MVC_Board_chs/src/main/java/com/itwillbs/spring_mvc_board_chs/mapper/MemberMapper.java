package com.itwillbs.spring_mvc_board_chs.mapper;

import org.apache.ibatis.annotations.Param;

import com.itwillbs.spring_mvc_board_chs.vo.MemberVO;

public interface MemberMapper {
	
	// 회원 가입
	int insertMember(MemberVO member);
	
	String selectPasswd(String id);
	
	MemberVO selectMember(String id);

//	int updateMemberInfo(MemberVO member, @Param String newPasswd);
	int updateMemberInfo(@Param("member") MemberVO member, @Param("newPasswd") String newPasswd);
	
	int deleteMember(String id);

}
