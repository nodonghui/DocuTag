package docuTag.global.config;

import java.util.ArrayList;
import java.util.List;

public class SqlCountLogger {

    private int count = 0;
    private final List<String> sqlList = new ArrayList<>();

    public void record(String sql) {
        count++;
        sqlList.add(sql.trim());
    }

    public int getCount() { return count; }

    public void printResult(String title, long elapsedMs) {
        System.out.println("\n===== " + title + " =====");
        System.out.println("소요 시간      : " + elapsedMs + "ms");
        System.out.println("총 SQL 실행 수 : " + count);
        for (int i = 0; i < sqlList.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + sqlList.get(i));
        }
    }

    public void clear() {
        count = 0;
        sqlList.clear();
    }
}