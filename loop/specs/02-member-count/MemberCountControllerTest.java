package com.hslee.agentlab.member;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
class MemberCountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MemberService memberService;

	@Test
	void countMembersReturnsCount() throws Exception {
		given(memberService.countMembers()).willReturn(2L);

		mockMvc.perform(get("/api/members/count"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.count").value(2));
	}

}
