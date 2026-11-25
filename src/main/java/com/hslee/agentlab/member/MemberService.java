package com.hslee.agentlab.member;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	public Member getMember(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new MemberNotFoundException(id));
	}

	public List<Member> getMembers() {
		return memberRepository.findAll();
	}

	public long countMembers() {
		return memberRepository.count();
	}

	public void deleteMember(Long id) {
		if (!memberRepository.deleteById(id)) {
			throw new MemberNotFoundException(id);
		}
	}

}
