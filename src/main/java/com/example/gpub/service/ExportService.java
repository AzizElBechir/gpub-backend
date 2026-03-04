package com.example.gpub.service;

import com.example.gpub.entity.Publication;
import com.example.gpub.entity.PublicationCoauteur;
import com.example.gpub.repository.PublicationCoauteurRepository;
import com.example.gpub.repository.PublicationRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExportService {

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PublicationCoauteurRepository coauteurRepository;

    @Autowired
    private StatService statService;

    // ===== GET PUBLICATIONS TO EXPORT =====

    private List<Publication> getPublishedPublications() {
        return publicationRepository.findAll().stream()
            .filter(p -> "PUBLIE".equals(p.getStatut()))
            .collect(Collectors.toList());
    }

    private List<Publication> getPublicationsByAuteur(Long chercheurId) {
        return publicationRepository.findByAuteurPrincipalId(chercheurId).stream()
            .filter(p -> "PUBLIE".equals(p.getStatut()))
            .collect(Collectors.toList());
    }

    private String getCoAuteursString(Long publicationId) {
        List<PublicationCoauteur> coauteurs = coauteurRepository.findByPublicationId(publicationId);
        return coauteurs.stream()
            .map(c -> c.getChercheur().getNom())
            .collect(Collectors.joining(", "));
    }

    // ===== CSV EXPORT =====

    public byte[] exportPublicationsCSV(List<Publication> publications) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Titre,Auteur Principal,Co-auteurs,Domaine,Annee,Mots-cles,URL PDF,Resume\n");

        for (Publication p : publications) {
            String auteur = p.getAuteurPrincipal() != null ? p.getAuteurPrincipal().getNom() : "";
            String coauteurs = getCoAuteursString(p.getId());
            String annee = p.getDatePublication() != null ? String.valueOf(p.getDatePublication().getYear()) : "";
            String motsCles = p.getMotsCles() != null ? p.getMotsCles().replace(",", ";") : "";
            String resume = p.getResume() != null ? p.getResume().replace(",", " ").replace("\n", " ") : "";
            String pdfUrl = p.getPdfUrl() != null ? p.getPdfUrl() : "";

            sb.append(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                p.getId(),
                p.getTitre() != null ? p.getTitre() : "",
                auteur, coauteurs,
                p.getDomaine() != null ? p.getDomaine() : "",
                annee, motsCles, pdfUrl, resume
            ));
        }
        return sb.toString().getBytes();
    }

    // ===== BIBTEX EXPORT =====

    public byte[] exportPublicationsBibTeX(List<Publication> publications) {
        StringBuilder sb = new StringBuilder();

        for (Publication p : publications) {
            String auteur = p.getAuteurPrincipal() != null ? p.getAuteurPrincipal().getNom() : "Unknown";
            String coauteurs = getCoAuteursString(p.getId());
            String allAuthors = coauteurs.isEmpty() ? auteur : auteur + " and " + coauteurs;
            String annee = p.getDatePublication() != null ? String.valueOf(p.getDatePublication().getYear()) : "0000";
            String key = auteur.replaceAll("\\s+", "") + annee + "_" + p.getId();

            sb.append("@article{").append(key).append(",\n");
            sb.append("  title     = {").append(p.getTitre() != null ? p.getTitre() : "").append("},\n");
            sb.append("  author    = {").append(allAuthors).append("},\n");
            sb.append("  year      = {").append(annee).append("},\n");
            sb.append("  keywords  = {").append(p.getMotsCles() != null ? p.getMotsCles() : "").append("},\n");
            sb.append("  abstract  = {").append(p.getResume() != null ? p.getResume() : "").append("},\n");
            sb.append("  url       = {").append(p.getPdfUrl() != null ? p.getPdfUrl() : "").append("},\n");
            sb.append("  domain    = {").append(p.getDomaine() != null ? p.getDomaine() : "").append("},\n");
            sb.append("}\n\n");
        }
        return sb.toString().getBytes();
    }

    // ===== EXCEL EXPORT =====

    public byte[] exportPublicationsExcel(List<Publication> publications) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Publications");

        CellStyle headerStyle = createHeaderStyle(workbook);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Titre", "Auteur Principal", "Co-auteurs", "Domaine", "Année", "Mots-clés", "URL PDF", "Résumé"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Publication p : publications) {
            Row row = sheet.createRow(rowNum++);
            String auteur = p.getAuteurPrincipal() != null ? p.getAuteurPrincipal().getNom() : "";
            String coauteurs = getCoAuteursString(p.getId());
            String annee = p.getDatePublication() != null ? String.valueOf(p.getDatePublication().getYear()) : "";

            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getTitre() != null ? p.getTitre() : "");
            row.createCell(2).setCellValue(auteur);
            row.createCell(3).setCellValue(coauteurs);
            row.createCell(4).setCellValue(p.getDomaine() != null ? p.getDomaine() : "");
            row.createCell(5).setCellValue(annee);
            row.createCell(6).setCellValue(p.getMotsCles() != null ? p.getMotsCles() : "");
            row.createCell(7).setCellValue(p.getPdfUrl() != null ? p.getPdfUrl() : "");
            row.createCell(8).setCellValue(p.getResume() != null ? p.getResume() : "");
        }

        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    // ===== PUBLIC EXPORT METHODS =====

    public byte[] exportAllPublications(String format) throws IOException {
        List<Publication> publications = getPublishedPublications();
        return exportByFormat(publications, format);
    }

    public byte[] exportMyPublications(Long chercheurId, String format) throws IOException {
        List<Publication> publications = getPublicationsByAuteur(chercheurId);
        return exportByFormat(publications, format);
    }

    private byte[] exportByFormat(List<Publication> publications, String format) throws IOException {
        return switch (format.toLowerCase()) {
            case "csv" -> exportPublicationsCSV(publications);
            case "bibtex" -> exportPublicationsBibTeX(publications);
            case "xlsx" -> exportPublicationsExcel(publications);
            default -> throw new RuntimeException("Invalid format. Use csv, bibtex or xlsx");
        };
    }

    // ===== STATS EXPORT =====

    public byte[] exportMyStatsCSV(Long chercheurId, LocalDate dateDebut, LocalDate dateFin) {
        Map<String, Object> stats = statService.getChercheurDetailedStats(chercheurId, dateDebut, dateFin);

        StringBuilder sb = new StringBuilder();

        // Summary
        sb.append("Rapport de statistiques\n");
        sb.append("Période:,").append(stats.get("dateDebut")).append(" à ").append(stats.get("dateFin")).append("\n");
        sb.append("Total Vues:,").append(stats.get("totalVues")).append("\n");
        sb.append("Total Téléchargements:,").append(stats.get("totalTelechargements")).append("\n\n");

        // Daily stats
        sb.append("Statistiques par jour\n");
        sb.append("Date,Vues,Téléchargements\n");
        List<Map<String, Object>> dailyStats = (List<Map<String, Object>>) stats.get("dailyStats");
        for (Map<String, Object> day : dailyStats) {
            sb.append(day.get("jour")).append(",")
              .append(day.get("vues")).append(",")
              .append(day.get("telechargements")).append("\n");
        }

        sb.append("\n");

        // Per publication
        sb.append("Statistiques par publication\n");
        sb.append("ID,Titre,Vues,Téléchargements\n");
        List<Map<String, Object>> byPublication = (List<Map<String, Object>>) stats.get("byPublication");
        for (Map<String, Object> pub : byPublication) {
            sb.append(String.format("%s,\"%s\",%s,%s\n",
                pub.get("publicationId"),
                pub.get("titre"),
                pub.get("vues"),
                pub.get("telechargements")
            ));
        }

        return sb.toString().getBytes();
    }

    public byte[] exportMyStatsExcel(Long chercheurId, LocalDate dateDebut, LocalDate dateFin) throws IOException {
        Map<String, Object> stats = statService.getChercheurDetailedStats(chercheurId, dateDebut, dateFin);

        Workbook workbook = new XSSFWorkbook();
        CellStyle headerStyle = createHeaderStyle(workbook);

        // ===== Sheet 1: Summary =====
        Sheet summarySheet = workbook.createSheet("Résumé");
        String[][] summaryData = {
            {"Période", stats.get("dateDebut") + " à " + stats.get("dateFin")},
            {"Total Vues", stats.get("totalVues").toString()},
            {"Total Téléchargements", stats.get("totalTelechargements").toString()}
        };
        for (int i = 0; i < summaryData.length; i++) {
            Row row = summarySheet.createRow(i);
            Cell keyCell = row.createCell(0);
            keyCell.setCellValue(summaryData[i][0]);
            keyCell.setCellStyle(headerStyle);
            row.createCell(1).setCellValue(summaryData[i][1]);
        }
        summarySheet.autoSizeColumn(0);
        summarySheet.autoSizeColumn(1);

        // ===== Sheet 2: Daily Stats =====
        Sheet dailySheet = workbook.createSheet("Par Jour");
        Row dailyHeader = dailySheet.createRow(0);
        String[] dailyHeaders = {"Date", "Vues", "Téléchargements"};
        for (int i = 0; i < dailyHeaders.length; i++) {
            Cell cell = dailyHeader.createCell(i);
            cell.setCellValue(dailyHeaders[i]);
            cell.setCellStyle(headerStyle);
        }
        List<Map<String, Object>> dailyStats = (List<Map<String, Object>>) stats.get("dailyStats");
        int rowNum = 1;
        for (Map<String, Object> day : dailyStats) {
            Row row = dailySheet.createRow(rowNum++);
            row.createCell(0).setCellValue(day.get("jour").toString());
            row.createCell(1).setCellValue((int) day.get("vues"));
            row.createCell(2).setCellValue((int) day.get("telechargements"));
        }
        for (int i = 0; i < dailyHeaders.length; i++) dailySheet.autoSizeColumn(i);

        // ===== Sheet 3: Per Publication =====
        Sheet pubSheet = workbook.createSheet("Par Publication");
        Row pubHeader = pubSheet.createRow(0);
        String[] pubHeaders = {"ID", "Titre", "Vues", "Téléchargements"};
        for (int i = 0; i < pubHeaders.length; i++) {
            Cell cell = pubHeader.createCell(i);
            cell.setCellValue(pubHeaders[i]);
            cell.setCellStyle(headerStyle);
        }
        List<Map<String, Object>> byPublication = (List<Map<String, Object>>) stats.get("byPublication");
        rowNum = 1;
        for (Map<String, Object> pub : byPublication) {
            Row row = pubSheet.createRow(rowNum++);
            row.createCell(0).setCellValue(pub.get("publicationId").toString());
            row.createCell(1).setCellValue(pub.get("titre").toString());
            row.createCell(2).setCellValue((int) pub.get("vues"));
            row.createCell(3).setCellValue((int) pub.get("telechargements"));
        }
        for (int i = 0; i < pubHeaders.length; i++) pubSheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    // ===== HELPER =====

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }
}