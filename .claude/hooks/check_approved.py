import sys
import os

if not os.path.exists(".claude/APPROVED"):
    print("❌ 승인 전 파일 수정 금지.")
    sys.exit(2)

sys.exit(0)