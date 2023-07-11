package com.fullstack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fullstack.dto.PageRequestDTO;
import com.fullstack.service.BoardService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class HomeController {
	private final BoardService boardService;
    //기본 페이지 요청 메서드
    @GetMapping("/")
    public String index(PageRequestDTO pageRequestDTO, Model model) {
    	model.addAttribute("result", boardService.getList(pageRequestDTO));
        return "member/index";

    }
    
		
}
