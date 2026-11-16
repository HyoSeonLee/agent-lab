#!/usr/bin/env bash
# Stop hook: 테스트가 통과해야만 Claude가 작업을 끝낼 수 있게 한다.

input=$(cat)                              # Claude Code가 보내는 JSON(stdin)
cd "$(dirname "$0")/../.." || exit 0      # 프로젝트 루트로 이동
mkdir -p build

if ./gradlew test -q > build/test-gate.log 2>&1; then
  exit 0                                  # 통과 → 정상 종료 허용
fi

# 이미 한 번 되돌려 보낸 상태면, 무한 반복을 막기 위해 사람에게 넘긴다.
if echo "$input" | grep -q '"stop_hook_active": *true'; then
  echo "[test-gate] 재시도 후에도 테스트 실패. 사람이 확인하세요. 로그: build/test-gate.log" >&2
  exit 1                                  # 차단하지 않고 사용자에게 경고만
fi

echo "[test-gate] ./gradlew test 실패. 실패 원인을 고친 뒤 테스트가 통과해야 끝낼 수 있습니다." >&2
tail -n 30 build/test-gate.log >&2
exit 2                                    # 차단 → Claude가 작업을 계속함
