package com.hslee.agentlab.member;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

	private final Map<Long, Member> store = new ConcurrentHashMap<>();

	private final AtomicLong sequence = new AtomicLong();

	public Member save(Member member) {
		Member toSave = member.id() == null
			? new Member(sequence.incrementAndGet(), member.name(), member.email())
			: member;
		store.put(toSave.id(), toSave);
		return toSave;
	}

	public Optional<Member> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}

	public List<Member> findAll() {
		return store.values().stream()
			.sorted(Comparator.comparing(Member::id))
			.toList();
	}

	public long count() {
		return store.size();
	}

	public boolean deleteById(Long id) {
		return store.remove(id) != null;
	}

}
