#!/usr/bin/env bash
# 5회차: TODO.md 항목을 하나씩 처리하는 루프
#   스펙 테스트 복사 → claude -p 구현 → 테스트 확인 → 완료 표시
set -u
cd "$(dirname "$0")/.." || exit 1

MAX_ITER=5   # 종료 조건: 최대 반복 횟수
MAX_FAIL=2   # 종료 조건: 연속 실패 허용 횟수
fail=0
mkdir -p build/loop

for ((i=1; i<=MAX_ITER; i++)); do
  line=$(grep -m1 '^- \[ \] ' TODO.md)
  if [ -z "$line" ]; then
    echo "[loop] 남은 TODO 없음 → 종료"; exit 0            # 종료 조건: 목표 달성
  fi
  id=$(echo "$line" | sed -E 's/^- \[ \] ([^:]+):.*/\1/')
  task=$(echo "$line" | sed -E 's/^- \[ \] [^:]+: *//')
  echo "[loop] #$i $id: $task"

  # 사람이 미리 쓴 스펙 테스트를 넣는다 (Claude는 guard 때문에 테스트를 못 고침)
  cp loop/specs/"$id"/*.java src/test/java/com/hslee/agentlab/member/

  claude -p "다음 TODO 항목을 구현해: $task
테스트 코드는 수정하지 말고, ./gradlew test가 통과하도록 main 코드만 고쳐." \
    --allowedTools "Read,Edit,Write,Glob,Grep,Bash(./gradlew test *)" \
    --max-turns 30 --max-budget-usd 1 \
    --output-format json < /dev/null > "build/loop/$i-$id.json"
  echo "[loop] claude 종료 코드: $?"

  # 완료 판단은 Claude의 응답이 아니라 테스트 결과로
  if ./gradlew test -q > "build/loop/$i-$id-test.log" 2>&1; then
    sed -i "s/^- \[ \] $id:/- [x] $id:/" TODO.md
    echo "[loop] $id 통과"; fail=0
  else
    fail=$((fail+1))
    echo "[loop] $id 실패 (연속 ${fail}회)"
    if [ "$fail" -ge "$MAX_FAIL" ]; then
      echo "[loop] 연속 실패 ${MAX_FAIL}회 → 종료"; exit 1  # 종료 조건: 연속 실패
    fi
  fi
done
echo "[loop] 최대 반복 ${MAX_ITER}회 도달 → 종료"; exit 2   # 종료 조건: 최대 반복
