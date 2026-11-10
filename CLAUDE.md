# agent-lab

## 빌드·테스트
- 테스트: ./gradlew test

## 패키지 구조
- 기능별 패키지: com.hslee.agentlab.<기능명> (예: member)

## API 규칙
- 에러 응답 형식: (예: {"code": "...", "message": "..."})
- 404 처리 방식: (예: 전용 예외 + @RestControllerAdvice)

## 테스트 규칙
- 컨트롤러 테스트: (예: @WebMvcTest)

## 금지
- build.gradle에 의존성 추가 금지 (필요하면 먼저 물어볼 것)