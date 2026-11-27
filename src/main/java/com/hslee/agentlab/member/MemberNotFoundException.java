package com.hslee.agentlab.member;

public class MemberNotFoundException extends RuntimeException {

	public MemberNotFoundException(Long id) {
		super("회원을 찾을 수 없습니다. id=" + id);
	}

	private MemberNotFoundException(String message) {
		super(message);
	}

	public static MemberNotFoundException byEmail(String email) {
		return new MemberNotFoundException("회원을 찾을 수 없습니다. email=" + email);
	}

}
