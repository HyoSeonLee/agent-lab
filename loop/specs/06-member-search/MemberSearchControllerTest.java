package com.hslee.agentlab.member;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
class MemberSearchControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MemberService memberService;

	@Test
	void searchByEmailReturnsMember() throws Exception {
		given(memberService.findByEmail("kim@example.com"))
			.willReturn(Optional.of(new Member(2L, "kim", "kim@example.com")));

		mockMvc.perform(get("/api/members/search").param("email", "kim@example.com"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(2));
	}

	@Test
	void searchByEmailReturns404WhenNotFound() throws Exception {
		given(memberService.findByEmail("none@example.com")).willReturn(Optional.empty());

		mockMvc.perform(get("/api/members/search").param("email", "none@example.com"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("MEMBER_NOT_FOUND"));
	}

}
