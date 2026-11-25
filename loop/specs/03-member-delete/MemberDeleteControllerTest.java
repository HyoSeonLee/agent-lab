package com.hslee.agentlab.member;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
class MemberDeleteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MemberService memberService;

	@Test
	void deleteMemberReturns204() throws Exception {
		mockMvc.perform(delete("/api/members/1"))
			.andExpect(status().isNoContent());

		then(memberService).should().deleteMember(1L);
	}

	@Test
	void deleteMemberReturns404WhenNotFound() throws Exception {
		willThrow(new MemberNotFoundException(999L)).given(memberService).deleteMember(999L);

		mockMvc.perform(delete("/api/members/999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("MEMBER_NOT_FOUND"));
	}

}
