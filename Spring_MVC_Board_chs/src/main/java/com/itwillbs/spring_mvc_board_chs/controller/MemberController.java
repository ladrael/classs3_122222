package com.itwillbs.spring_mvc_board_chs.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwillbs.spring_mvc_board_chs.service.MemberService;
import com.itwillbs.spring_mvc_board_chs.vo.MemberVO;

@Controller
public class MemberController {
	
	@Autowired
	private MemberService service;
	
	// "/MemberJoinForm.me" 요청에 대해 "member/member_join_form.jsp" 페이지 포워딩
	// => GET 방식 요청, Dispatch 방식 포워딩
	@GetMapping(value = "/MemberJoinForm.me")
	public String joinForm() {
		return "member/member_join_form";
	}
	
	// "/MemberJoinPro.me" 요청에 대해 MemberService 객체 비즈니스 로직 수행
	// => POST 방식 요청, Redirect 방식
	// => 폼 파라미터로 전달되는 가입 정보를 파라미터로 전달받기
	// => 가입 완료 후 이동할 페이지 : 메인페이지(index.jsp)
	// => 가입 실패 시 오류 페이지(fail_back)를 통해 "회원 가입 실패!" 출력 후 이전페이지로 돌아가기
	@PostMapping(value = "/MemberJoinPro.me")
	public String joinPro(MemberVO member, Model model) {
//		System.out.println(member);
		
		// ------------ BCryptPasswordEncoder 객체 활용한 패스워드 암호화(해싱) -------------
		// 입력받은 패스워드는 암호화(해싱) 필요 => 해싱 후 MemberVO 객체 패스워드에 덮어쓰기
		// => 스프링에서 암호화는 org.springframework.security.crypto.bcrypt 패키지의
		//    BCryptPasswordEncoder 클래스 활용(spring-security-web 라이브러리 추가 필요)
		// => BCryptPasswordEncoder 클래스 활용하여 해싱할 경우 Salting(솔팅) 기능을 통해
		//    동일한 평문(원본 암호)이라도 매번 다른 결과값을 갖는 해싱이 가능하다!
		// 1. BCryptPasswordEncoder 객체 생성
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		// 2. BCryptPasswordEncoder 객체의 encode() 메서드를 호출하여 해싱 후 결과값 리턴
		// => 파라미터 : 평문 암호    리턴타입 : String(해싱된 암호)
		String securePasswd = passwordEncoder.encode(member.getPasswd());
//		System.out.println("평문 : " + member.getPasswd()); // 평문 암호 출력
//		System.out.println("암호문 : " + securePasswd); // 해싱된 암호 출력
		// 3. MemberVO 객체의 패스워드에 암호화 된 패스워드 저장(덮어쓰기)
		member.setPasswd(securePasswd);
		// ----------------------------------------------------------------------------------
		// MemberService - registMember() 메서드 호출을 통해 회원 가입 작업 요청
		// => 파라미터 : MemberVO 객체    리턴타입 : int
		int insertCount = service.registMember(member);
		
		if(insertCount > 0) { // 가입 성공
			// 리다이렉트 방식으로 "/" 요청(HomeController - index.jsp)
			return "redirect:/";
		} else { // 가입 실패
			// Model 객체의 "msg" 속성으로 "회원 가입 실패!" 문자열 저장 후 fail_back.jsp 포워딩
			model.addAttribute("msg", "회원 가입 실패!");
			return "fail_back";
		}
		
	}
	@GetMapping(value = "/MemberLoginForm.me")
	public String  MemberLoginForm() {
		return "member/member_login_form";
	}
	
	@PostMapping(value = "/MemberLoginPro.me")
	public String  MemberLoginPro(MemberVO member, Model model, HttpSession session) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		
		String passwd = service.getPasswd(member.getId());
		
//		System.out.println(passwordEncoder.encode(passwd));
		
		
		if(passwd == null || !passwordEncoder.matches(member.getPasswd(), passwd)) {
			model.addAttribute("msg", "로그인 실패");
			return "fail_back";
		}else {
			session.setAttribute("sId", member.getId());
			return "redirect:/";
		}
	}
	@GetMapping(value = "/MemberLogout.me")
	public String  MemberLogout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	
	@GetMapping(value="/MemberInfo.me")
	public String MemberInfo(HttpSession session, Model model) {
		String id = (String)session.getAttribute("sId");
		
		
		if(id == null) {
			model.addAttribute("msg", "잘못된 접근입니다.");
			return "fail_back";
		}
		MemberVO member = new MemberVO();
//		System.out.println(member.getEmail().split("@"));
		member = service.getMemberInfo(id);
		model.addAttribute("member", member);
		
		return "member/member_info";
	}
	
	@PostMapping(value="/MemberUpdate.me")
	public String MemberUpdate(@RequestParam String newPasswd, MemberVO member, HttpSession session, Model model) {
		String id = (String)session.getAttribute("sId");
		if(id == null) {
			model.addAttribute("msg", "잘못된 접근입니다.");
			return "fail_back";
		}
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		
		String passwd = service.getPasswd(id);
		if(passwd == null || !passwordEncoder.matches(member.getPasswd(), passwd)) {
			model.addAttribute("msg", "수정 권한 없음");
			return "fail_back";
		}
		
		if(!newPasswd.equals("")) {
			newPasswd = passwordEncoder.encode(newPasswd);
		}
		
		
		int updateCount = service.updateMemberInfo(member, newPasswd);
		if(updateCount>0) {
			model.addAttribute("msg", "회원 정보 수정 성공!");
			model.addAttribute("target", "MemberInfo.me");
			return "success";
		}else {
			model.addAttribute("msg", "회원 정보 수정 실패!");
			return "fail_back";
		}
		
	}
	// 회원 탈퇴
	
	@GetMapping("/MemberQiutForm.me")
	public String MemberQuit() {
		
		return "member/member_quit_form";
	}
	
	
	
	@PostMapping("/MemberQuitPro.me")
	public String MemberQuitPro(HttpSession session, Model model, @RequestParam String passwd) {
		String id = (String)session.getAttribute("sId");
		
		
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		
		
//		String dbPasswd = passwordEncoder.encode(service.getPasswd(id));
		String dbPasswd = service.getPasswd(id);
		System.out.println(id);
		System.out.println(passwordEncoder.matches(passwd, dbPasswd));
		if(passwordEncoder.matches(passwd, dbPasswd)) {
			int deleteCount = service.quitMember(id);
			
			if(deleteCount > 0 ) {
				session.invalidate();
				model.addAttribute("msg", "탈퇴가 완료되었습니다.");
				model.addAttribute("target", "./");
				// 에러나는 이유 target을 받기때문
				return "success";
			}else {
				model.addAttribute("msg", "비밀번호를 확인해주세요");
				return "fail_back";
			}
			
		}else {
			model.addAttribute("msg", "권한이 없습니다.");
			return "fail_back";
		}
		
	}
	
	
	
}















