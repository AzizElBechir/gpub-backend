package com.example.gpub.service;

import com.example.gpub.entity.Publication;
import com.example.gpub.entity.StatPublicationJour;
import com.example.gpub.repository.PublicationRepository;
import com.example.gpub.repository.StatPublicationJourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StatService {

    @Autowired
    private StatPublicationJourRepository statRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private NotificationService notificationService;

    public void incrementVues(Long publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
            .orElseThrow(() -> new RuntimeException("Publication not found"));

        LocalDate today = LocalDate.now();
        StatPublicationJour stat = statRepository
            .findByPublicationIdAndJour(publicationId, today)
            .orElse(null);

        if (stat == null) {
            stat = new StatPublicationJour();
            stat.setPublication(publication);
            stat.setJour(today);
            stat.setVues(1);
            stat.setTelechargements(0);
        } else {
            stat.setVues(stat.getVues() + 1);
        }
        statRepository.save(stat);

        // Notify author every 10 views
        int totalVues = statRepository.findByPublicationId(publicationId)
            .stream().mapToInt(StatPublicationJour::getVues).sum();

        if (totalVues % 10 == 0 && publication.getAuteurPrincipal() != null) {
            notificationService.createNotification(
                publication.getAuteurPrincipal().getId(),
                "PUBLICATION_VUE",
                "Votre publication a atteint " + totalVues + " vues",
                "\"" + publication.getTitre() + "\" a été vue " + totalVues + " fois",
                "/publications/" + publicationId
            );
        }
    }

    public void incrementTelechargements(Long publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
            .orElseThrow(() -> new RuntimeException("Publication not found"));

        LocalDate today = LocalDate.now();
        StatPublicationJour stat = statRepository
            .findByPublicationIdAndJour(publicationId, today)
            .orElse(null);

        if (stat == null) {
            stat = new StatPublicationJour();
            stat.setPublication(publication);
            stat.setJour(today);
            stat.setVues(0);
            stat.setTelechargements(1);
        } else {
            stat.setTelechargements(stat.getTelechargements() + 1);
        }
        statRepository.save(stat);

        // Notify author every 5 downloads
        int totalTel = statRepository.findByPublicationId(publicationId)
            .stream().mapToInt(StatPublicationJour::getTelechargements).sum();

        if (totalTel % 5 == 0 && publication.getAuteurPrincipal() != null) {
            notificationService.createNotification(
                publication.getAuteurPrincipal().getId(),
                "PUBLICATION_TELECHARGEE",
                "Votre publication a atteint " + totalTel + " téléchargements",
                "\"" + publication.getTitre() + "\" a été téléchargée " + totalTel + " fois",
                "/publications/" + publicationId
            );
        }
    }

    public Map<String, Object> getPublicationStats(Long publicationId) {
        List<StatPublicationJour> stats = statRepository.findByPublicationId(publicationId);

        int totalVues = stats.stream().mapToInt(StatPublicationJour::getVues).sum();
        int totalTelechargements = stats.stream().mapToInt(StatPublicationJour::getTelechargements).sum();

        List<Map<String, Object>> dailyStats = new ArrayList<>();
        for (StatPublicationJour s : stats) {
            Map<String, Object> day = new LinkedHashMap<>();
            day.put("id", s.getId());
            day.put("jour", s.getJour().toString());
            day.put("vues", s.getVues());
            day.put("telechargements", s.getTelechargements());
            dailyStats.add(day);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("publicationId", publicationId);
        result.put("totalVues", totalVues);
        result.put("totalTelechargements", totalTelechargements);
        result.put("dailyStats", dailyStats);

        return result;
    }

    public Map<String, Object> getChercheurStats(Long chercheurId) {
        List<Publication> publications = publicationRepository.findByAuteurPrincipalId(chercheurId);

        int totalVues = 0;
        int totalTelechargements = 0;
        List<Map<String, Object>> publicationStats = new ArrayList<>();

        for (Publication pub : publications) {
            List<StatPublicationJour> stats = statRepository.findByPublicationId(pub.getId());

            int vues = stats.stream().mapToInt(StatPublicationJour::getVues).sum();
            int tel = stats.stream().mapToInt(StatPublicationJour::getTelechargements).sum();

            totalVues += vues;
            totalTelechargements += tel;

            Map<String, Object> pubStat = new LinkedHashMap<>();
            pubStat.put("publicationId", pub.getId());
            pubStat.put("titre", pub.getTitre());
            pubStat.put("vues", vues);
            pubStat.put("telechargements", tel);
            publicationStats.add(pubStat);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("chercheurId", chercheurId);
        result.put("totalVues", totalVues);
        result.put("totalTelechargements", totalTelechargements);
        result.put("publications", publicationStats);

        return result;
    }

    public Map<String, Object> getGlobalStats() {
        List<Publication> allPublications = publicationRepository.findAll();
        List<StatPublicationJour> allStats = statRepository.findAll();
    
        long totalPublications = allPublications.stream()
            .filter(p -> "PUBLIE".equals(p.getStatut()))
            .count();
    
        int totalVues = allStats.stream().mapToInt(StatPublicationJour::getVues).sum();
        int totalTelechargements = allStats.stream().mapToInt(StatPublicationJour::getTelechargements).sum();
    
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalPublications", totalPublications);
        result.put("totalVues", totalVues);
        result.put("totalTelechargements", totalTelechargements);
    
        return result;
    }

    // ✅ New: Detailed stats with date range
    public Map<String, Object> getChercheurDetailedStats(
            Long chercheurId,
            LocalDate dateDebut,
            LocalDate dateFin) {

        // Default date range: last 30 days
        if (dateFin == null) dateFin = LocalDate.now();
        if (dateDebut == null) dateDebut = dateFin.minusDays(30);

        List<Publication> publications = publicationRepository.findByAuteurPrincipalId(chercheurId);

        List<Long> publicationIds = publications.stream()
            .map(Publication::getId)
            .collect(Collectors.toList());

        // Get stats within date range
        List<StatPublicationJour> allStats = publicationIds.isEmpty()
            ? new ArrayList<>()
            : statRepository.findByPublicationIdInAndJourBetween(publicationIds, dateDebut, dateFin);

        // Totals in period
        int totalVues = allStats.stream().mapToInt(StatPublicationJour::getVues).sum();
        int totalTelechargements = allStats.stream().mapToInt(StatPublicationJour::getTelechargements).sum();

        // Daily breakdown
        Map<String, int[]> dailyMap = new TreeMap<>();
        for (StatPublicationJour s : allStats) {
            String jour = s.getJour().toString();
            dailyMap.merge(jour,
                new int[]{s.getVues(), s.getTelechargements()},
                (a, b) -> new int[]{a[0] + b[0], a[1] + b[1]}
            );
        }

        List<Map<String, Object>> dailyStats = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : dailyMap.entrySet()) {
            Map<String, Object> day = new LinkedHashMap<>();
            day.put("jour", entry.getKey());
            day.put("vues", entry.getValue()[0]);
            day.put("telechargements", entry.getValue()[1]);
            dailyStats.add(day);
        }

        // Per publication breakdown
        List<Map<String, Object>> byPublication = new ArrayList<>();
        for (Publication pub : publications) {
            List<StatPublicationJour> pubStats = allStats.stream()
                .filter(s -> s.getPublication().getId().equals(pub.getId()))
                .collect(Collectors.toList());

            int vues = pubStats.stream().mapToInt(StatPublicationJour::getVues).sum();
            int tel = pubStats.stream().mapToInt(StatPublicationJour::getTelechargements).sum();

            Map<String, Object> pubStat = new LinkedHashMap<>();
            pubStat.put("publicationId", pub.getId());
            pubStat.put("titre", pub.getTitre());
            pubStat.put("vues", vues);
            pubStat.put("telechargements", tel);
            byPublication.add(pubStat);
        }

        // Sort by views descending
        byPublication.sort((a, b) ->
            Integer.compare((int) b.get("vues"), (int) a.get("vues"))
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("chercheurId", chercheurId);
        result.put("dateDebut", dateDebut.toString());
        result.put("dateFin", dateFin.toString());
        result.put("totalVues", totalVues);
        result.put("totalTelechargements", totalTelechargements);
        result.put("dailyStats", dailyStats);
        result.put("byPublication", byPublication);

        return result;
    }
}