# TODO
- [x] 01-member-list: GET /api/members 로 전체 회원 목록을 조회한다 (MemberService.getMembers())
- [x] 02-member-count: GET /api/members/count 로 회원 수를 {"count": n} 형태로 조회한다 (MemberService.countMembers())
- [x] 03-member-delete: DELETE /api/members/{id} 로 회원을 삭제한다. 성공 204, 없으면 MemberNotFoundException으로 404 (MemberService.deleteMember(id))
- [ ] 05-member-register: POST /api/members 로 회원을 등록한다. 요청 본문 {"name","email"}, 성공 201과 등록된 회원 반환 (MemberService.register(String name, String email))
- [ ] 06-member-search: GET /api/members/search?email=... 로 이메일 검색. 있으면 200과 회원, 없으면 404 MEMBER_NOT_FOUND (MemberService.findByEmail(String email)이 Optional<Member> 반환)
- [ ] 07-member-rename: PATCH /api/members/{id}/name 로 이름 변경. 요청 본문 {"name"}, 성공 200과 변경된 회원, 없으면 MemberNotFoundException으로 404 (MemberService.rename(Long id, String name))
