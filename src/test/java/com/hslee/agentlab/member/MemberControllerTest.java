package com.hslee.agentlab.member;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MemberService memberService;

	@Test
	void getMemberReturnsMember() throws Exception {
		given(memberService.getMember(1L)).willReturn(new Member(1L, "홍길동", "hong@example.com"));

		mockMvc.perform(get("/api/members/1"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.name").value("홍길동"))
			.andExpect(jsonPath("$.email").value("hong@example.com"));
	}

	@Test
	void getMemberReturns404WhenNotFound() throws Exception {
		given(memberService.getMember(999L)).willThrow(new MemberNotFoundException(999L));

		mockMvc.perform(get("/api/members/999"))
			.andExpect(status().isNotFound())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value("MEMBER_NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("회원을 찾을 수 없습니다. id=999"));
	}

	@Test
	void getMemberReturns400WhenIdIsNotNumber() throws Exception {
		mockMvc.perform(get("/api/members/abc"))
			.andExpect(status().isBadRequest());

		then(memberService).shouldHaveNoInteractions();
	}

}
