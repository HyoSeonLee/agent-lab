package com.hslee.agentlab.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberServiceTest {

	private MemberRepository memberRepository;
	private MemberService memberService;

	@BeforeEach
	void setUp() {
		memberRepository = new MemberRepository();
		memberService = new MemberService(memberRepository);
	}

	@Test
	void getMemberReturnsSavedMember() {
		Member saved = memberRepository.save(new Member(1L, "홍길동", "hong@example.com"));

		assertThat(memberService.getMember(1L)).isEqualTo(saved);
	}

	@Test
	void getMemberThrowsWhenNotFound() {
		assertThatThrownBy(() -> memberService.getMember(999L))
			.isInstanceOf(MemberNotFoundException.class)
			.hasMessageContaining("id=999");
	}

}
