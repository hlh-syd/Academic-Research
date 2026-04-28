package com.researchworkbench.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RelaxService {

    public Map<String, String> fortune() {
        DayOfWeek day = LocalDate.now().getDayOfWeek();
        String content = switch (day) {
            case MONDAY, TUESDAY -> "今日宜做检索归纳，先梳理结构，再补证据。";
            case WEDNESDAY, THURSDAY -> "今日适合推进写作与知识库总结，不宜并行太多任务。";
            default -> "今日适合复盘与轻量整理，把重要来源归档会更稳。";
        };
        return Map.of("type", "fortune", "content", content);
    }

    public Map<String, String> meal() {
        String[] options = {
            "番茄牛腩饭 + 无糖气泡水",
            "照烧鸡腿饭 + 味噌汤",
            "虾仁时蔬意面 + 冷泡茶",
            "咖喱牛肉饭 + 酸奶"
        };
        int index = LocalDate.now().getDayOfMonth() % options.length;
        return Map.of("type", "meal", "content", options[index]);
    }
}
