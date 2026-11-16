#!/usr/bin/env bash
# PreToolUse hook: 도구 종류와 상관없이
#   1) 비밀 파일 접근을 막고
#   2) 테스트 코드와 하네스 설정(.claude/) 수정을 막는다.

input=$(cat)    # Claude Code가 보내는 JSON(stdin): session_id, transcript_path, cwd, tool_name, tool_input 등

# 검사 대상에서 하네스가 붙이는 경로 필드(transcript_path, cwd)는 뺀다.
# transcript_path는 항상 ~/.claude/ 아래라서, 빼지 않으면 모든 호출이 .claude 규칙에 걸린다.
target=$(echo "$input" | sed -E 's/"[a-z_]*(transcript_path|cwd)" *: *"([^"\\]|\\.)*"//g')

# 1) 비밀 파일: .env, certs/ 폴더, *.key, *.pem 을 언급하는 모든 도구 호출 차단
if echo "$target" | grep -qE '\.env\b|certs[/\\]|\.key\b|\.pem\b'; then
  echo "[guard] 비밀 파일(.env, 인증서·개인키)에는 접근할 수 없습니다." >&2
  exit 2
fi

# 2) 보호 경로: src/test 아래, .claude 아래 (경로 구분자 / 와 \ 모두 처리)
if echo "$target" | grep -qE 'src[/\\]+test|\.claude[/\\]'; then
  tool=$(echo "$input" | grep -oE '"tool_name" *: *"[^"]*"' | head -1 | sed -E 's/.*"([^"]*)"$/\1/')
  case "$tool" in
    Edit|Write|MultiEdit)
      echo "[guard] 테스트 코드와 하네스 설정(.claude/)은 수정할 수 없습니다. 테스트가 아니라 코드를 고치세요." >&2
      exit 2 ;;
    Bash)
      if echo "$target" | grep -qE '>|sed -i|tee |cp |mv |rm '; then
        echo "[guard] 셸 명령으로 테스트 코드나 하네스 설정(.claude/)을 바꿀 수 없습니다." >&2
        exit 2
      fi ;;
  esac
fi

exit 0
