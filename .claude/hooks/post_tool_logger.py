import sys, json, os, difflib
from datetime import datetime

WRITE_TOOLS = {"Write", "Edit", "MultiEdit"}

def capture_after_and_diff(session_id: str, tool_input: dict, timestamp: str):
    """Write/Edit 후 파일 저장 + diff 파일 생성"""
    file_path = tool_input.get("file_path", "")
    if not file_path or not os.path.exists(file_path):
        return None, None

    with open(file_path, "r", encoding="utf-8", errors="replace") as f:
        after_content = f.read()

    diff_dir = f".claude/logs/sessions/{session_id}/diffs"
    os.makedirs(diff_dir, exist_ok=True)
    safe_name = file_path.replace("/", "_").replace("\\", "_").replace(":", "_")

    # AFTER 스냅샷
    after_path = f"{diff_dir}/{timestamp}_AFTER_{safe_name}.md"
    with open(after_path, "w", encoding="utf-8") as f:
        f.write(f"# AFTER snapshot\n")
        f.write(f"- file: {file_path}\n")
        f.write(f"- timestamp: {timestamp}\n\n")
        f.write("```\n")
        f.write(after_content)
        f.write("\n```\n")

    # BEFORE 파일 찾기 (같은 세션, 같은 파일)
    before_content = ""
    before_files = sorted([
        x for x in os.listdir(diff_dir)
        if f"BEFORE_{safe_name}" in x
    ])
    if before_files:
        with open(f"{diff_dir}/{before_files[-1]}", "r",encoding="utf-8") as f:
            lines = f.readlines()
            # ```와 ``` 사이 내용만 추출
            in_block = False
            raw = []
            for line in lines:
                if line.strip() == "```" and not in_block:
                    in_block = True
                    continue
                if line.strip() == "```" and in_block:
                    break
                if in_block:
                    raw.append(line)
            before_content = "".join(raw)

    # Unified diff 생성
    diff = list(difflib.unified_diff(
        before_content.splitlines(keepends=True),
        after_content.splitlines(keepends=True),
        fromfile=f"BEFORE {file_path}",
        tofile=f"AFTER  {file_path}",
    ))

    diff_path = f"{diff_dir}/{timestamp}_DIFF_{safe_name}.md"
    with open(diff_path, "w", encoding="utf-8") as f:
        f.write(f"# Diff — {file_path}\n")
        f.write(f"- timestamp: {timestamp}\n\n")
        f.write("```diff\n")
        f.writelines(diff if diff else ["(변경 없음)\n"])
        f.write("\n```\n")

    return after_path, diff_path

def main():
    raw = sys.stdin.read()
    event = json.loads(raw)

    session_id  = event.get("session_id", "unknown")
    tool_name   = event.get("tool_name", "")
    tool_input  = event.get("tool_input", {})
    tool_response = event.get("tool_response")
    is_error    = event.get("is_error", False)
    timestamp   = datetime.now().strftime("%Y%m%d_%H%M%S_%f")

    after_ref, diff_ref = None, None
    if tool_name in WRITE_TOOLS and not is_error:
        after_ref, diff_ref = capture_after_and_diff(session_id, tool_input, timestamp)

    log_entry = {
        "timestamp":    datetime.now().isoformat(),
        "phase":        "POST_TOOL",
        "session_id":   session_id,
        "tool_name":    tool_name,
        "tool_input":   tool_input,
        "is_error":     is_error,
        "tool_response": str(tool_response)[:300] if tool_response else None,  # 너무 길면 truncate
        "after_snapshot": after_ref,
        "diff_file":    diff_ref,
    }

    write_log(session_id, log_entry)

def write_log(session_id: str, entry: dict):
    log_dir = f".claude/logs/sessions/{session_id}"
    os.makedirs(log_dir, exist_ok=True)
    with open(f"{log_dir}/session.jsonl", "a", encoding="utf-8") as f:
        f.write(json.dumps(entry, ensure_ascii=False) + "\n")

if __name__ == "__main__":
    main()