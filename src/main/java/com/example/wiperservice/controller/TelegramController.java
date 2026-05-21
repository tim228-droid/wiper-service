package com.example.wiperservice.controller;

import com.example.wiperservice.service.WiperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/telegram")
public class TelegramController {
    private static final Logger log = LoggerFactory.getLogger(TelegramController.class);
    private static final String TOKEN = "ТВОЙ_ТОКЕН";
    private final WiperService srv;

    public TelegramController(WiperService srv) { this.srv = srv; }

    @PostMapping("/report")
    public Map<String, String> report(@RequestParam String chatId) {
        log.info("Telegram отчёт в чат {}", chatId);
        var all = srv.all();
        long total = all.size();
        long bad = all.stream().filter(w -> !"NO_ANOMALY".equals(w.getAnomalyType().name())).count();
        String text = "📊 ОТЧЕТ\nВсего: " + total + "\nАномалий: " + bad;
        try {
            URL url = new URL("https://api.telegram.org/bot" + TOKEN + "/sendMessage");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("POST"); c.setRequestProperty("Content-Type", "application/json"); c.setDoOutput(true);
            String json = String.format("{\"chat_id\":\"%s\",\"text\":\"%s\"}", chatId, text);
            OutputStream os = c.getOutputStream(); os.write(json.getBytes(StandardCharsets.UTF_8)); os.close();
            c.getResponseCode();
            return Map.of("status", "✅ Отправлено");
        } catch (Exception e) {
            return Map.of("status", "❌ " + e.getMessage());
        }
    }
}