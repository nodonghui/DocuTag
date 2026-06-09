import sys, json, os
from datetime import datetime

def main():
    raw = sys.stdin.read()
    event = json.loads(raw)

    session_id = event.get("session_id", "unknown")
    prompt = event.get("prompt", "")          # ← 프롬프트 내용
    cwd = event.get("cwd", "")
    transcript_path = event.get("transcript_path", "")

    log_entry = {
        "timestamp":       datetime.now().isoformat(),
        "phase":           "USER_PROMPT",
        "session_id":      session_id,
        "prompt":          prompt,
        "cwd":             cwd,
        "transcript_path": transcript_path,
    }

    log_dir = f".claude/logs/sessions/{session_id}"
    os.makedirs(log_dir, exist_ok=True)
    with open(f"{log_dir}/session.jsonl", "a", encoding="utf-8") as f:
        f.write(json.dumps(log_entry, ensure_ascii=False) + "\n")

if __name__ == "__main__":
    main()