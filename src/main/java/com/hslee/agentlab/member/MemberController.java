package com.hslee.agentlab.member;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping
	public List<Member> getMembers() {
		return memberService.getMembers();
	}

	@GetMapping("/count")
	public MemberCountResponse countMembers() {
		return new MemberCountResponse(memberService.countMembers());
	}

	@GetMapping("/search")
	public Member searchByEmail(@RequestParam String email) {
		return memberService.findByEmail(email)
			.orElseThrow(() -> MemberNotFoundException.byEmail(email));
	}

	@GetMapping("/{id}")
	public Member getMember(@PathVariable Long id) {
		return memberService.getMember(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Member registerMember(@RequestBody MemberRegisterRequest request) {
		return memberService.register(request.name(), request.email());
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMember(@PathVariable Long id) {
		memberService.deleteMember(id);
	}

}
