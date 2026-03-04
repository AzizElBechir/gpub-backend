package com.example.gpub.controller;

import com.example.gpub.service.ExportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Export", description = "Export publications and stats in CSV, Excel, BibTeX")
@RestController
@CrossOrigin(origins = "*")
public class ExportController {

    @Autowired
    private ExportService exportService;

    // Public - Export all published publications
    @GetMapping("/api/publications/export")
    public ResponseEntity<?> exportPublications(
            @RequestParam(defaultValue = "csv") String format) {
        try {
            byte[] data = exportService.exportAllPublications(format);
            return buildResponse(data, format, "publications");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Protected - Export my own publications
    @GetMapping("/api/me/publications/export")
    public ResponseEntity<?> exportMyPublications(
            @RequestParam(defaultValue = "csv") String format,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            byte[] data = exportService.exportMyPublications(userId, format);
            return buildResponse(data, format, "my-publications");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    private ResponseEntity<byte[]> buildResponse(byte[] data, String format, String filename) {
        HttpHeaders headers = new HttpHeaders();

        switch (format.toLowerCase()) {
            case "csv" -> {
                headers.setContentType(MediaType.parseMediaType("text/csv"));
                headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + ".csv\"");
            }
            case "bibtex" -> {
                headers.setContentType(MediaType.parseMediaType("application/x-bibtex"));
                headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + ".bib\"");
            }
            case "xlsx" -> {
                headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + ".xlsx\"");
            }
        }

        return ResponseEntity.ok().headers(headers).body(data);
    }
    // Protected - Export my stats
@GetMapping("/api/me/stats/export")
public ResponseEntity<?> exportMyStats(
        @RequestParam(defaultValue = "csv") String format,
        @RequestParam(required = false) String dateDebut,
        @RequestParam(required = false) String dateFin,
        HttpServletRequest request) {
    try {
        Long userId = (Long) request.getAttribute("userId");

        LocalDate debut = dateDebut != null ? LocalDate.parse(dateDebut) : null;
        LocalDate fin = dateFin != null ? LocalDate.parse(dateFin) : null;

        byte[] data;
        String filename = "stats";

        switch (format.toLowerCase()) {
            case "xlsx" -> {
                data = exportService.exportMyStatsExcel(userId, debut, fin);
                return buildResponse(data, "xlsx", filename);
            }
            default -> {
                data = exportService.exportMyStatsCSV(userId, debut, fin);
                return buildResponse(data, "csv", filename);
            }
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", e.getMessage()));
    }
}
}