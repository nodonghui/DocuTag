import sys
import os

if not os.path.exists(".claude/APPROVED"):
    print("❌ 승인 전 파일 수정 금지. planner 계획 먼저.")
    sys.exit(1)

sys.exit(0)