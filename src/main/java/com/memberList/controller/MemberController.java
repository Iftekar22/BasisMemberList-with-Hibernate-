package com.memberList.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import com.memberList.service.MemberService;

@Controller
public class MemberController {
	@Autowired
	private MemberService mServ;
	
	@EventListener(ApplicationReadyEvent.class)
	public void test() throws IOException {
		mServ.scrap();
	}	
}


