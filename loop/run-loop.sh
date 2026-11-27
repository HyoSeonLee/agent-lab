#!/usr/bin/env bash
# 6회차: 상태 파일, 항목별 커밋·롤백, 누적 비용 한도를 붙인 TODO 루프
set -u
cd "$(dirname "$0")/.." || exit 1

MAX_ITER=5                  # 종료 조건: 최대 반복 횟수
MAX_FAIL=2                  # 종료 조건: 연속 실패 허용 횟수
MAX_COST=2.00               # 종료 조건: 누적 비용 한도(달러)
PROGRESS=loop/progress.md   # 진행 기록 (gitignore 대상이라 롤백해도 남음)
fail=0
mkdir -p build/loop

# 안전장치: loop/ 로 시작하는 작업 브랜치에서만 실행
branch=$(git rev-parse --abbrev-ref HEAD)
if [[ "$branch" != loop/* ]]; then
  echo "[loop] 작업 브랜치(loop/...)가 아님: $branch → 종료"; exit 3
fi

rollback() {   # 마지막 커밋 상태로 되돌림 (gitignore 대상은 남김)
  git reset -q --hard HEAD
  git clean -q -fd
}
log() {        # progress.md에 한 줄 기록
  echo "| $(date '+%m-%d %H:%M') | $1 | $2 | $3 | $4 | $5 |" >> "$PROGRESS"
}

# 이전 실행이 중간에 죽어 반쯤 고친 코드가 남아 있으면 버리고 시작
if [ -n "$(git status --porcelain)" ]; then
  echo "[loop] 미커밋 변경 발견 → 롤백 후 시작"; rollback
fi

if [ ! -f "$PROGRESS" ]; then
  printf '# progress\n\n| 시각 | 항목 | 결과 | 비용 | 누적 | 커밋 |\n|---|---|---|---|---|---|\n' > "$PROGRESS"
fi
total=$(awk -F'|' '/^\| [0-9]/ {s+=$5} END {printf "%.4f", s}' "$PROGRESS")
echo "[loop] 시작 (브랜치 $branch, 누적 비용 \$$total)"

for ((i=1; i<=MAX_ITER; i++)); do
  if awk -v t="$total" -v m="$MAX_COST" 'BEGIN {exit !(t >= m)}'; then
    echo "[loop] 누적 비용 \$$total ≥ 한도 \$$MAX_COST → 종료"; exit 4
  fi
  line=$(grep -m1 '^- \[ \] ' TODO.md)
  if [ -z "$line" ]; then
    echo "[loop] 남은 TODO 없음 → 종료"; exit 0
  fi
  id=$(echo "$line" | sed -E 's/^- \[ \] ([^:]+):.*/\1/')
  task=$(echo "$line" | sed -E 's/^- \[ \] [^:]+: *//')
  echo "[loop] #$i $id"

  cp loop/specs/"$id"/*.java src/test/java/com/hslee/agentlab/member/
  out="build/loop/$(date +%m%d-%H%M%S)-$id.json"

  claude -p "다음 TODO 항목을 구현해: $task
테스트 코드는 수정하지 말고, ./gradlew test가 통과하도록 main 코드만 고쳐." \
    --allowedTools "Read,Edit,Write,Glob,Grep,Bash(./gradlew test *)" \
    --max-turns 30 --max-budget-usd 1 \
    --output-format json < /dev/null > "$out"

  cost=$(grep -o '"total_cost_usd":[0-9.]*' "$out" | head -1 | cut -d: -f2)
  cost=${cost:-0}
  total=$(awk -v a="$total" -v b="$cost" 'BEGIN {printf "%.4f", a + b}')

  if ./gradlew test -q > "${out%.json}-test.log" 2>&1; then
    sed -i "s/^- \[ \] $id:/- [x] $id:/" TODO.md
    git add -A && git commit -q -m "loop: $id"
    log "$id" "통과" "$cost" "$total" "$(git rev-parse --short HEAD)"
    echo "[loop] $id 통과 → 커밋 (비용 \$$cost, 누적 \$$total)"; fail=0
  else
    rollback
    log "$id" "실패·롤백" "$cost" "$total" "-"
    fail=$((fail+1))
    echo "[loop] $id 실패 → 롤백 (연속 ${fail}회, 누적 \$$total)"
    if [ "$fail" -ge "$MAX_FAIL" ]; then
      echo "[loop] 연속 실패 ${MAX_FAIL}회 → 종료"; exit 1
    fi
  fi
done
echo "[loop] 최대 반복 ${MAX_ITER}회 도달 → 종료"; exit 2
