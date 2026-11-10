package com.hslee.agentlab.member;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

	private final Map<Long, Member> store = new ConcurrentHashMap<>();

	public Member save(Member member) {
		store.put(member.id(), member);
		return member;
	}

	public Optional<Member> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}

}
