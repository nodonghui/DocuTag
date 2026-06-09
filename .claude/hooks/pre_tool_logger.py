import sys, json, os
from datetime import datetime

WRITE_TOOLS = {"Write", "Edit", "MultiEdit"}

def detect_agent(tool_input: dict) -> str:
    path = str(tool_input.get("file_path", "") or tool_input.get("command", ""))
    if "analysis" in path:   return "researcher"
    if "plans" in path:      return "planner"
    if "reviews" in path:    return "reviewer"
    if "debugger" in path:   return "debugger"
    return "unknown"

def capture_before_snapshot(session_id: str, tool_input: dict, timestamp: str):
    """Write/Edit 전 파일 원본 저장"""
    file_path = tool_input.get("file_path", "")
    if not file_path or not os.path.exists(file_path):
        return None

    with open(file_path, "r", encoding="utf-8", errors="replace") as f:
        content = f.read()

    diff_dir = f".claude/logs/sessions/{session_id}/diffs"
    os.makedirs(diff_dir, exist_ok=True)

    safe_name = file_path.replace("/", "_").replace("\\", "_").replace(":", "_")
    snapshot_path = f"{diff_dir}/{timestamp}_BEFORE_{safe_name}.md"

    with open(snapshot_path, "w", encoding="utf-8") as f:
        f.write(f"# BEFORE snapshot\n")
        f.write(f"- file: {file_path}\n")
        f.write(f"- timestamp: {timestamp}\n\n")
        f.write("```\n")
        f.write(content)
        f.write("\n```\n")

    return snapshot_path  # 로그엔 경로만 기록

def main():
    raw = sys.stdin.read()
    event = json.loads(raw)

    session_id = event.get("session_id", "unknown")
    tool_name  = event.get("tool_name", "")
    tool_input = event.get("tool_input", {})
    timestamp  = datetime.now().strftime("%Y%m%d_%H%M%S_%f")

    snapshot_ref = None
    if tool_name in WRITE_TOOLS:
        snapshot_ref = capture_before_snapshot(session_id, tool_input, timestamp)

    log_entry = {
        "timestamp": datetime.now().isoformat(),
        "phase":     "PRE_TOOL",
        "session_id": session_id,
        "agent":     detect_agent(tool_input),
        "tool_name": tool_name,
        "tool_input": tool_input,
        "before_snapshot": snapshot_ref,  # 파일 경로만
    }

    write_log(session_id, log_entry)

def write_log(session_id: str, entry: dict):
    log_dir = f".claude/logs/sessions/{session_id}"
    os.makedirs(log_dir, exist_ok=True)
    with open(f"{log_dir}/session.jsonl", "a", encoding="utf-8") as f:
        f.write(json.dumps(entry, ensure_ascii=False) + "\n")

if __name__ == "__main__":
    main()