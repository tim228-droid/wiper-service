package com.example.wiperservice.service;

import com.example.wiperservice.model.Wiper;
import com.example.wiperservice.model.AnomalyType;
import com.example.wiperservice.repository.WiperRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class WiperService {
    private final WiperRepository repo;

    public WiperService(WiperRepository repo) {
        this.repo = repo;
    }

    public Wiper add(String name, String manufacturer, Double price, Integer length) {
        Wiper w = new Wiper();
        w.setName(name);
        w.setManufacturer(manufacturer);
        w.setPrice(price);
        w.setWiperLength(length);
        return repo.save(w);
    }

    public List<Wiper> all() {
        return repo.findAll();
    }

    public Wiper getById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public List<Wiper> anomalies() {
        return repo.findByAnomalyTypeNot(AnomalyType.NO_ANOMALY);
    }

    public Wiper update(Long id, String name, String manufacturer, Double price, Integer length) {
        Wiper w = repo.findById(id).orElseThrow();
        w.setName(name);
        w.setManufacturer(manufacturer);
        w.setPrice(price);
        w.setWiperLength(length);
        return repo.save(w);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Wiper fix(Long id, Double cost) {
        Wiper w = repo.findById(id).orElseThrow();
        w.setIsFixed(true);
        w.setAnomalyType(AnomalyType.NO_ANOMALY);
        w.setRepairCost(cost);
        w.setRepairDate(LocalDateTime.now());
        return repo.save(w);
    }

    public String excel() throws IOException {
        List<Wiper> all = repo.findAll();
        String path = "C:\\Projects\\report_" + System.currentTimeMillis() + ".xlsx";
        Workbook wb = new XSSFWorkbook();
        Sheet s = wb.createSheet("Дворники");
        Row h = s.createRow(0);
        String[] cols = {"ID","Название","Производитель","Цена","Длина","Аномалия","Исправлено","Стоимость ремонта"};
        for (int i = 0; i < cols.length; i++) h.createCell(i).setCellValue(cols[i]);
        int r = 1;
        for (Wiper w : all) {
            Row row = s.createRow(r++);
            row.createCell(0).setCellValue(w.getId());
            row.createCell(1).setCellValue(w.getName());
            row.createCell(2).setCellValue(w.getManufacturer());
            row.createCell(3).setCellValue(w.getPrice());
            row.createCell(4).setCellValue(w.getWiperLength() != null ? w.getWiperLength() : 0);
            row.createCell(5).setCellValue(w.getAnomalyType().getDescription());
            row.createCell(6).setCellValue(w.getIsFixed() ? "Да" : "Нет");
            row.createCell(7).setCellValue(w.getRepairCost() != null ? w.getRepairCost() : 0);
        }
        wb.write(new FileOutputStream(path));
        wb.close();
        return path;
    }

    public String check(Long id) throws IOException {
        Wiper w = repo.findById(id).orElseThrow();
        BufferedImage img = new BufferedImage(450, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE); g.fillRect(0, 0, 450, 300);
        g.setColor(new Color(255, 215, 0)); g.fillRect(0, 0, 450, 40);
        g.setColor(Color.BLACK); g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("ЧЕК ЗА РЕМОНТ", 120, 30);
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        g.drawString("Дворник: " + w.getName(), 30, 80);
        g.drawString("Производитель: " + w.getManufacturer(), 30, 110);
        g.drawString("Аномалия: " + w.getAnomalyType().getDescription(), 30, 140);
        g.setFont(new Font("Arial", Font.BOLD, 17));
        g.drawString("Стоимость: " + String.format("%.0f руб.", w.getRepairCost() != null ? w.getRepairCost() : 0), 30, 180);
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.drawString(LocalDateTime.now().toString(), 30, 220);
        g.setColor(new Color(0, 150, 0)); g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("✅ ИСПРАВЛЕНО", 30, 260);
        g.dispose();
        String path = "C:\\Projects\\check_" + System.currentTimeMillis() + ".png";
        ImageIO.write(img, "png", new File(path));
        return path;
    }
    // Чтение из Excel/CSV и импорт
public Map<String, Object> readFromFile(String filePath) throws IOException {
    File file = new File(filePath);
    if (!file.exists()) throw new RuntimeException("Файл не найден: " + filePath);
    
    int count = 0;
    String fileName = file.getName().toLowerCase();
    
    if (fileName.endsWith(".csv")) {
        // Чтение CSV
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Пропускаем заголовок
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    add(parts[0].trim(), parts[1].trim(), 
                        Double.parseDouble(parts[2].trim()),
                        parts.length > 3 ? Integer.parseInt(parts[3].trim()) : null);
                    count++;
                }
            }
        }
    } else if (fileName.endsWith(".xlsx")) {
        // Чтение Excel
        try (Workbook wb = WorkbookFactory.create(file)) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null) {
                    String name = row.getCell(0).getStringCellValue();
                    String manufacturer = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "Unknown";
                    double price = row.getCell(2) != null ? row.getCell(2).getNumericCellValue() : 0;
                    int length = row.getCell(3) != null ? (int) row.getCell(3).getNumericCellValue() : 0;
                    add(name, manufacturer, price, length > 0 ? length : null);
                    count++;
                }
            }
        }
    } else {
        throw new RuntimeException("Поддерживаются только CSV и XLSX файлы");
    }
    
    return Map.of("message", "Импортировано: " + count + " записей", "count", count);
}
}