# agent-lab

Claude Code로 개발 흐름을 연습하기 위한 Spring Boot 실습 프로젝트입니다.
작은 REST API를 하나씩 추가하면서 프로젝트 규칙(`CLAUDE.md`)과 스킬(`.claude/skills`)로 에이전트를 다루는 방법을 익힙니다.

## 기술 스택

- Java 21
- Spring Boot 4.1 (Spring Web MVC)
- Gradle
- JUnit 5

## 실행 및 테스트

```bash
# 애플리케이션 실행 (기본 포트 8080)
./gradlew bootRun

# 테스트
./gradlew test
```

## API

| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| GET | `/api/health` | 서버 상태 확인. `{"status": "UP"}` 반환 |
| GET | `/api/members/{id}` | 회원 단건 조회 |

회원 데이터는 메모리(`ConcurrentHashMap`)에 저장되며, 아직 초기 데이터나 등록 API는 없습니다.

### 에러 응답

에러는 `@RestControllerAdvice`(`GlobalExceptionHandler`)에서 공통 형식으로 응답합니다.

```json
{
  "code": "MEMBER_NOT_FOUND",
  "message": "회원을 찾을 수 없습니다. id=1"
}
```

## 패키지 구조

기능별로 패키지를 나눕니다: `com.hslee.agentlab.<기능명>`

```
com.hslee.agentlab
├── common/error   # 공통 에러 응답, 전역 예외 처리
├── health         # 헬스 체크 API
└── member         # 회원 조회 (Controller / Service / Repository)
```

## Claude Code 설정

- `CLAUDE.md` — 빌드·테스트 명령, 패키지 구조, API·테스트 규칙, 금지 사항(의존성 추가 금지 등)
- `.claude/skills/api-test` — 컨트롤러 테스트 작성 시 200 / 404 / 400 케이스를 모두 포함하도록 하는 스킬
