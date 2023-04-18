package com.itwillbs.spring_mvc_board_chs.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.itwillbs.spring_mvc_board_chs.service.BoardService;
import com.itwillbs.spring_mvc_board_chs.vo.BoardVO;

@Controller
public class BoardController {
	
	@Autowired
	private BoardService service;
	
	
	@GetMapping("BoardWriteForm.bo")
	public String writeForm(HttpSession session, Model model) {
		
		if(session.getAttribute("sId") == null) {
			model.addAttribute("msg", "로그인 필수!");
			model.addAttribute("target", "MemberLoginForm.me");
			return "success";
		}
		
		return "board/board_write_form";
	}
	
//	@PostMapping("BoardWritePro.bo")
//	public String writePro(BoardVO board) {
//		System.out.println(board);
//		System.out.println("업로드 파일명 : " + board.getFile().getOriginalFilename());
//		
//		return "";
	// multipart/form-data 폼 파라미터 처리할 경우
	// 각 파라미터를 처리하면서 업로드 파일을 매핑 메서드에 MultipartFile 타입으로 직접 처리
	
	//2)
//	@PostMapping("BoardWritePro.bo")
//	public String writePro(String board_name, String board_subject, String board_content, MultipartFile file) {
//		System.out.println(board_name + ", " + board_subject + ", " + board_content);
//		System.out.println("업로드 파일명 : " + file.getOriginalFilename());
//	
//		return "";
//		
//	}
//	Map 객체 쓸 경우에 @requestParam 처리 해줘야한다
//	@PostMapping("BoardWritePro.bo")
//	public String writePro(
//			@RequestParam Map<String, String> map, MultipartFile file) {
//		System.out.println(map.get("board_name"));
//		System.out.println("업로드 파일명 : " + file.getOriginalFilename());
//		
//		return "";
//		
//	}
	
	//3) MultipartFile 타입 멤버번수를 포함하는 BoardVO 타입으로 모든 파라미터를 한꺼번에 처리
	// BoardVO 클래스에 MultipartFile 타입 멤버변수 선언 시
	// 반드시 <input type = "file">
	// Getter/Setter 정의 해줘야함
	
	@PostMapping("BoardWritePro.bo")
	public String writePro(BoardVO board, HttpSession session, Model model) {
		//	이클립스 프로젝트 상에 업로드 폴더 (upload) 생성 필요 - resources 폴더에 생성
		//  request 또는 session으로 가능 // request 보다는 session을 주로 사용함
		// 	getServletContext 메서드를 호출하여 서블릿 컨텍스트 객체를 얻어낸 후 다시 getRealPath()메서드를 호출하여 실제 업로드 경로 알아내기
		// D:\workspace_sts\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Spring_MVC_Board\resources\ upload
		String uploadDir = "/resources/upload";
		String saveDir= session.getServletContext().getRealPath(uploadDir);
		
		
		
//		String saveDir= request.getRealPath(uploadDir); // drpricate 처리됨
//		System.out.println("실제 파일 업로드 경로 : " + saveDir);
		
		// 실제 경로가 없을 수 도 있어서
		// java.nio.file.Paths 클래스의 get() 메서드를 호출하여 
		// 실제 경로를 관리하는 java.nio.file.Path 타입 객체를 리턴 받기 ( 파라미터 : 실제 업로드 경로 ) 
		try {
			// ------------------------------------------------------------------------------------------
			// 업로드 디렉토리를 날짜별 디렉토리로 분류하기
			// 하나의 디렉토리에 너무 많은 파일이 존재하면 로딩 시간 길어짐
			// Data 클래스 활용 java.util
			Date date = new Date();
			// 디렉토리 경로를 그대로 나타내기 위해 / 를 사용하는데
			SimpleDateFormat sdf =  new SimpleDateFormat("yyyy/MM/dd");		// 여기에 '-'가 아닌 '/'로 쓰면 경로구분에 사용됨
			board.setBoard_file_path("/" + sdf.format(date));
			
			saveDir = saveDir + board.getBoard_file_path();
			//---------------------------------------------------------------------------------------------
			Path path = Paths.get(saveDir);
			//Files 클래스의 createDirectories() 메서드를 호출 directories를 해야 없는 경로를 알아서 생성
			Files.createDirectories(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// BoardVO 객체에 전달된 객체 꺼내기
		MultipartFile mFile = board.getFile();	//단일 파일
//		MultipartFile mFiles = board.getFiles();	//복수 
		// 중복에 대한 처리 파일명 앞에 uuid 붙여주기
		// 랜덤 ID 생성 java.util.UUID 클래스 활용(범용 고유 식별자)
		String originalFileName = mFile.getOriginalFilename();
		System.out.println("원본 파일명 : " + originalFileName);
		
		String uuid = UUID.randomUUID().toString();
		
//		System.out.println(uuid);
		board.setBoard_file(uuid.substring(0, 8) + "_" +originalFileName);
		System.out.println("실제 업로드될 파일 이름 : " + board.getBoard_file());
		
		
		// ----------------------------------------------------------------------------------
		
		// => 파라미터 : BoardVO 객체 리턴타입 : int(insetCount)
		int insertCount = service.registBoard(board);
		if(insertCount > 0) {
			// 업로드 된 파일은 MultiPartFile 객체에 의해 임시 폴더에 저장되어 있음
			// 작업 성공시 실제 폴더로 옮기는 작업 필요함 
			try {
				mFile.transferTo(new File(saveDir, board.getBoard_file()));
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return "redirect:/BoardList.bo";
		}
			model.addAttribute("msg", "글 쓰기 실패!");
			return "fail_back";
		
	}
}
