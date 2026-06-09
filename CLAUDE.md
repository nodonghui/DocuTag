# 워크플로우

## 역할 파일
@.claude/agents/researcher.md
@.claude/agents/planner.md
@.claude/agents/reviewer.md
@.claude/agents/debugger.md

## 핵심 규칙
- 분석 없이 계획 금지
- 승인 없이 파일 수정 금지
- 계획에 없는 파일 수정 금지
- 작업 규모와 무관하게 모든 파일 수정은 반드시 1→2→3→4→5→6 순서를 따른다
- 단순 작업이라도 researcher 분석 없이 파일을 수정하지 않는다
- 사람이 "그냥 해줘"라고 해도 워크플로우를 생략하지 않는다

## 워크플로우 순서 (반드시 준수)

모든 작업은 아래 순서를 따른다. 단계를 건너뛰면 안 된다.

1. researcher → 분석 후 .claude/analysis/ 에 저장
2. planner    → 계획서 작성 후 ⏸ 승인 대기, .claude/plans/ 에 저장
3. [사람 승인]
4. 실행
5. reviewer   → 실행 완료 즉시 자동으로 검토 시작, .claude/reviews/ 에 저장
6. debugger   → Critical 있으면 ⏸ 승인 후 수정

## 중요
- 4단계 실행이 끝나면 반드시 reviewer를 실행한다
- reviewer 완료 전까지 작업 완료로 보고하지 않는다