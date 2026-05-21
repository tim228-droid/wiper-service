package com.example.wiperservice.controller;

import com.example.wiperservice.service.WiperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/wipers")
public class WiperController {
    private static final Logger log = LoggerFactory.getLogger(WiperController.class);
    private final WiperService srv;

    public WiperController(WiperService srv) { this.srv = srv; }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Object add(@RequestBody Map<String, Object> r) {
        log.info("Создание: {}", r.get("name"));
        return srv.add((String)r.get("name"), (String)r.get("manufacturer"),
            Double.valueOf(r.get("price").toString()),
            r.get("wiperLength") != null ? Integer.valueOf(r.get("wiperLength").toString()) : null);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public Object all() { log.info("Список всех"); return srv.all(); }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public Object get(@PathVariable Long id) { return srv.getById(id); }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Object update(@PathVariable Long id, @RequestBody Map<String, Object> r) {
        return srv.update(id, (String)r.get("name"), (String)r.get("manufacturer"),
            Double.valueOf(r.get("price").toString()),
            r.get("wiperLength") != null ? Integer.valueOf(r.get("wiperLength").toString()) : null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Object delete(@PathVariable Long id) { srv.delete(id); return Map.of("ok", true); }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/anomalies")
    public Object anomalies() { return srv.anomalies(); }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/fix")
    public Object fix(@PathVariable Long id, @RequestParam Double repairCost) { return srv.fix(id, repairCost); }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/export/excel")
    public Object excel() {
        try { return Map.of("path", srv.excel()); }
        catch (Exception e) { return Map.of("error", e.getMessage()); }
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}/check")
    public Object check(@PathVariable Long id) {
        try { return Map.of("path", srv.check(id)); }
        catch (Exception e) { return Map.of("error", e.getMessage()); }
    }
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/upload")
public Object upload(@RequestParam("file") MultipartFile file) {
    try {
        String fileName = file.getOriginalFilename().toLowerCase();
        int count = 0;
        
        if (fileName.endsWith(".csv")) {
            String content = new String(file.getBytes());
            String[] lines = content.split("\n");
            for (int i = 1; i < lines.length; i++) {
                String[] parts = lines[i].split(",");
                if (parts.length >= 3) {
                    srv.add(parts[0].trim(), parts[1].trim(), 
                        Double.parseDouble(parts[2].trim()),
                        parts.length > 3 ? Integer.parseInt(parts[3].trim()) : null);
                    count++;
                }
            }
        } else if (fileName.endsWith(".xlsx")) {
            try (org.apache.poi.ss.usermodel.Workbook wb = 
                    org.apache.poi.ss.usermodel.WorkbookFactory.create(file.getInputStream())) {
                org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheetAt(0);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                    if (row != null) {
                        String name = getCellValue(row.getCell(0));
                        String manufacturer = getCellValue(row.getCell(1));
                        String priceStr = getCellValue(row.getCell(2));
                        String lengthStr = getCellValue(row.getCell(3));
                        
                        if (name != null && !name.isEmpty() && priceStr != null && !priceStr.isEmpty()) {
                            double price = Double.parseDouble(priceStr);
                            Integer length = (lengthStr != null && !lengthStr.isEmpty()) ? 
                                Integer.parseInt(lengthStr) : null;
                            srv.add(name, manufacturer != null ? manufacturer : "Unknown", price, length);
                            count++;
                        }
                    }
                }
            }
        } else {
            return Map.of("error", "Только CSV и XLSX файлы");
        }
        
        log.info("Загружено {} записей", count);
        return Map.of("message", "Загружено: " + count + " дворников");
    } catch (Exception e) {
        log.error("Ошибка: {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }
}

// Вспомогательный метод для чтения любой ячейки
private String getCellValue(org.apache.poi.ss.usermodel.Cell cell) {
    if (cell == null) return null;
    switch (cell.getCellType()) {
        case STRING: return cell.getStringCellValue();
        case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
        case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
        default: return null;
    }
}
}