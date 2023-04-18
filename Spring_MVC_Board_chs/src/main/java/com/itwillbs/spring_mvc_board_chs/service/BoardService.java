package com.itwillbs.spring_mvc_board_chs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itwillbs.spring_mvc_board_chs.mapper.BoardMapper;
import com.itwillbs.spring_mvc_board_chs.vo.BoardVO;

@Service
public class BoardService {

	@Autowired
	private BoardMapper mapper;
	
	public int registBoard(BoardVO board) {
		return mapper.insertBoard(board);
//		return 0;
	}
	
}
