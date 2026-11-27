package com.hslee.agentlab.member;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
class MemberRenameControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MemberService memberService;

	@Test
	void renameReturnsUpdatedMember() throws Exception {
		given(memberService.rename(1L, "lee")).willReturn(new Member(1L, "lee", "kim@example.com"));

		mockMvc.perform(patch("/api/members/1/name")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"lee\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("lee"));
	}

	@Test
	void renameReturns404WhenNotFound() throws Exception {
		given(memberService.rename(999L, "lee")).willThrow(new MemberNotFoundException(999L));

		mockMvc.perform(patch("/api/members/999/name")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"lee\"}"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("MEMBER_NOT_FOUND"));
	}

}
