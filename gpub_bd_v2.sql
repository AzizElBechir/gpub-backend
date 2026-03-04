-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 04, 2026 at 09:10 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gpub_bd_v2`
--

-- --------------------------------------------------------

--
-- Table structure for table `chercheur`
--

CREATE TABLE `chercheur` (
  `id` bigint(20) NOT NULL,
  `nom` varchar(150) NOT NULL,
  `email` varchar(150) NOT NULL,
  `hash_mdp` varchar(255) NOT NULL,
  `photo_url` text DEFAULT NULL,
  `bio` text DEFAULT NULL,
  `domaine` varchar(150) DEFAULT NULL,
  `role` varchar(20) DEFAULT 'USER',
  `unite_id` bigint(20) DEFAULT NULL,
  `date_creation` timestamp NOT NULL DEFAULT current_timestamp(),
  `admin_universite_id` bigint(20) DEFAULT NULL,
  `actif` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `chercheur`
--

INSERT INTO `chercheur` (`id`, `nom`, `email`, `hash_mdp`, `photo_url`, `bio`, `domaine`, `role`, `unite_id`, `date_creation`, `admin_universite_id`, `actif`) VALUES
(1, 'Khadija', 'khadija@gmail.com', '$2a$10$gabMDqlq8kiMeq8Ldv3liuc2kQVW4VOSfOQe96651p5ipYE/b3OXS', '/uploads/dca06c15-1848-4e03-aa55-55eedf51ce45.png', 'Chercheur en Cyber sécurité', 'Informatique', 'USER', NULL, '2026-03-03 23:16:55', NULL, 1),
(2, 'Test User', 'test@gmail.com', '$2a$10$5IhRRffXlZdzSDeA4p1dVu0iYD76z3VKNO5rPs3V6tc85gYaGouYm', NULL, 'Test bio', 'Informatique', 'ADMIN', NULL, '2026-03-03 23:25:04', 1, 1),
(3, 'Dr. Ahmed Mohamed', 'ahmed@example.com', '$2a$10$5IhRRffXlZdzSDeA4p1dVu0iYD76z3VKNO5rPs3V6tc85gYaGouYm', NULL, 'Expert en Intelligence Artificielle', 'Informatique', 'USER', NULL, '2026-03-03 23:41:02', NULL, 1),
(4, 'Dr. Fatima Hassan', 'fatima@example.com', '$2a$10$5IhRRffXlZdzSDeA4p1dVu0iYD76z3VKNO5rPs3V6tc85gYaGouYm', NULL, 'Spécialiste en Data Science', 'Informatique', 'USER', NULL, '2026-03-03 23:41:02', NULL, 1),
(5, 'Dr. Omar Sidi', 'omar@example.com', '$2a$10$5IhRRffXlZdzSDeA4p1dVu0iYD76z3VKNO5rPs3V6tc85gYaGouYm', NULL, 'Chercheur en Cybersécurité', 'Informatique', 'USER', NULL, '2026-03-03 23:41:02', NULL, 1),
(6, 'Super Admin', 'superadmin@ministere.mr', '$2a$10$jdgnUYpYin0duQzADNeVHeXF6HfAvH6nakHFbtDY5r5G9xOt0/gYa', NULL, NULL, NULL, 'SUPER_ADMIN', NULL, '2026-03-04 03:24:41', NULL, 1),
(7, 'Aziz', '23042@supnum.mr', '$2a$10$8Ob9mg0CJhry.nrJMa6RSuiboqKjmSv51GaWygABtbLYbIk/ZL996', NULL, NULL, 'Informatique', 'USER', NULL, '2026-03-04 07:13:48', NULL, 1),
(8, 'knayn', 'knayn34@gmail.com', '$2a$10$YA9aEYupW/3OwugzBJgv8OXSlh361OSiET6.K2ijbARUzOc3jjiFy', NULL, NULL, 'Informatique', 'USER', NULL, '2026-03-04 07:28:22', NULL, 1),
(9, 'Test Clean', 'testclean@example.com', '$2a$10$6mwI06zrRsW9RSsD4HXxRenlHG6q4m/0mOqtw7/EAiv8DmPdXShmq', NULL, NULL, 'Informatique', 'USER', NULL, '2026-03-04 07:50:28', NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `faculte`
--

CREATE TABLE `faculte` (
  `id` bigint(20) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `universite_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `faculte`
--

INSERT INTO `faculte` (`id`, `nom`, `universite_id`) VALUES
(12, 'Faculté des Sciences et Techniques', 1),
(13, 'Faculté des Lettres et Sciences Humaines', 1),
(14, 'Faculté de Médecine', 1),
(15, 'Faculté des Sciences Juridiques', 5),
(16, 'Updated Faculty Name', 1),
(17, 'Faculté des Sciences', 1);

-- --------------------------------------------------------

--
-- Table structure for table `favori`
--

CREATE TABLE `favori` (
  `id` bigint(20) NOT NULL,
  `chercheur_id` bigint(20) NOT NULL,
  `publication_id` bigint(20) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notification`
--

CREATE TABLE `notification` (
  `id` bigint(20) NOT NULL,
  `chercheur_id` bigint(20) NOT NULL,
  `type` varchar(50) NOT NULL,
  `titre` varchar(255) NOT NULL,
  `message` text DEFAULT NULL,
  `lien` varchar(500) DEFAULT NULL,
  `lu` tinyint(1) DEFAULT 0,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `publication`
--

CREATE TABLE `publication` (
  `id` bigint(20) NOT NULL,
  `titre` varchar(255) NOT NULL,
  `resume` text DEFAULT NULL,
  `mots_cles` text DEFAULT NULL,
  `domaine` varchar(255) DEFAULT NULL,
  `date_publication` date DEFAULT NULL,
  `pdf_url` varchar(500) DEFAULT NULL,
  `affiliation_texte` varchar(255) DEFAULT NULL,
  `auteur_principal_id` bigint(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `statut` varchar(20) DEFAULT 'EN_ATTENTE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `publication`
--

INSERT INTO `publication` (`id`, `titre`, `resume`, `mots_cles`, `domaine`, `date_publication`, `pdf_url`, `affiliation_texte`, `auteur_principal_id`, `created_at`, `updated_at`, `statut`) VALUES
(1, 'Machine Learning dans l\'Agriculture', 'Cette étude explore l\'application du machine learning dans l\'agriculture moderne.', 'Machine Learning, Agriculture, IA', 'Informatique', '2024-01-15', '/uploads/ml-agriculture.pdf', 'Université de Nouakchott', 1, '2026-03-03 23:41:02', '2026-03-04 03:56:56', 'PUBLIE'),
(2, 'Sécurité des Réseaux IoT', 'Analyse des vulnérabilités des réseaux IoT et propositions de solutions.', 'IoT, Sécurité, Réseaux', 'Informatique', '2024-02-20', '/uploads/iot-security.pdf', 'Université de Nouakchott', 3, '2026-03-03 23:41:02', '2026-03-04 03:56:56', 'PUBLIE'),
(3, 'Big Data Analytics', 'Méthodes d\'analyse de grandes quantités de données.', 'Big Data, Analytics, Python', 'Informatique', '2024-03-10', '/uploads/bigdata.pdf', 'Université de Nouakchott', 2, '2026-03-03 23:41:02', '2026-03-04 03:56:56', 'PUBLIE'),
(4, 'Deep Learning pour la Vision', 'Application des CNN pour la reconnaissance d\'images.', 'Deep Learning, Vision, CNN', 'Informatique', '2024-06-05', '/uploads/vision.pdf', 'Université de Nouakchott', 1, '2026-03-03 23:41:02', '2026-03-04 03:56:56', 'PUBLIE'),
(5, 'Machine Learning dans l\'Agriculture - Part 2', 'New research', NULL, 'Informatique', '2024-03-01', NULL, NULL, 1, '2026-03-04 02:39:31', '2026-03-04 03:56:56', 'PUBLIE');

-- --------------------------------------------------------

--
-- Table structure for table `publication_coauteur`
--

CREATE TABLE `publication_coauteur` (
  `id` bigint(20) NOT NULL,
  `publication_id` bigint(20) NOT NULL,
  `chercheur_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `publication_coauteur`
--

INSERT INTO `publication_coauteur` (`id`, `publication_id`, `chercheur_id`) VALUES
(1, 1, 2),
(7, 1, 3),
(8, 1, 4),
(2, 2, 1),
(3, 3, 1),
(4, 3, 3),
(5, 4, 2);

-- --------------------------------------------------------

--
-- Table structure for table `stat_publication_jour`
--

CREATE TABLE `stat_publication_jour` (
  `id` bigint(20) NOT NULL,
  `publication_id` bigint(20) NOT NULL,
  `jour` date NOT NULL,
  `vues` int(11) DEFAULT 0,
  `telechargements` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `stat_publication_jour`
--

INSERT INTO `stat_publication_jour` (`id`, `publication_id`, `jour`, `vues`, `telechargements`) VALUES
(1, 1, '2026-03-03', 26, 8),
(2, 1, '2026-03-02', 30, 10),
(3, 2, '2026-03-03', 40, 15),
(4, 2, '2026-03-02', 35, 12),
(5, 3, '2026-03-03', 28, 9),
(6, 4, '2026-03-03', 51, 20),
(7, 1, '2026-03-04', 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `unite_recherche`
--

CREATE TABLE `unite_recherche` (
  `id` bigint(20) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `faculte_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `unite_recherche`
--

INSERT INTO `unite_recherche` (`id`, `nom`, `faculte_id`) VALUES
(9, 'Laboratoire de Linguistique', 13);

-- --------------------------------------------------------

--
-- Table structure for table `universite`
--

CREATE TABLE `universite` (
  `id` bigint(20) NOT NULL,
  `nom` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `universite`
--

INSERT INTO `universite` (`id`, `nom`) VALUES
(5, 'Université de Nouadhibou'),
(1, 'Université de Nouakchott'),
(6, 'Université de Rosso');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chercheur`
--
ALTER TABLE `chercheur`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `unite_id` (`unite_id`),
  ADD KEY `fk_admin_universite` (`admin_universite_id`);

--
-- Indexes for table `faculte`
--
ALTER TABLE `faculte`
  ADD PRIMARY KEY (`id`),
  ADD KEY `universite_id` (`universite_id`);

--
-- Indexes for table `favori`
--
ALTER TABLE `favori`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_favori` (`chercheur_id`,`publication_id`),
  ADD KEY `publication_id` (`publication_id`);

--
-- Indexes for table `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`id`),
  ADD KEY `chercheur_id` (`chercheur_id`);

--
-- Indexes for table `publication`
--
ALTER TABLE `publication`
  ADD PRIMARY KEY (`id`),
  ADD KEY `auteur_principal_id` (`auteur_principal_id`);

--
-- Indexes for table `publication_coauteur`
--
ALTER TABLE `publication_coauteur`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_pub_chercheur` (`publication_id`,`chercheur_id`),
  ADD KEY `chercheur_id` (`chercheur_id`);

--
-- Indexes for table `stat_publication_jour`
--
ALTER TABLE `stat_publication_jour`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_pub_jour` (`publication_id`,`jour`);

--
-- Indexes for table `unite_recherche`
--
ALTER TABLE `unite_recherche`
  ADD PRIMARY KEY (`id`),
  ADD KEY `faculte_id` (`faculte_id`);

--
-- Indexes for table `universite`
--
ALTER TABLE `universite`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nom` (`nom`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `chercheur`
--
ALTER TABLE `chercheur`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `faculte`
--
ALTER TABLE `faculte`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `favori`
--
ALTER TABLE `favori`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `notification`
--
ALTER TABLE `notification`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `publication`
--
ALTER TABLE `publication`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `publication_coauteur`
--
ALTER TABLE `publication_coauteur`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `stat_publication_jour`
--
ALTER TABLE `stat_publication_jour`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `unite_recherche`
--
ALTER TABLE `unite_recherche`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `universite`
--
ALTER TABLE `universite`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `chercheur`
--
ALTER TABLE `chercheur`
  ADD CONSTRAINT `chercheur_ibfk_1` FOREIGN KEY (`unite_id`) REFERENCES `unite_recherche` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_admin_universite` FOREIGN KEY (`admin_universite_id`) REFERENCES `universite` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `faculte`
--
ALTER TABLE `faculte`
  ADD CONSTRAINT `faculte_ibfk_1` FOREIGN KEY (`universite_id`) REFERENCES `universite` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `favori`
--
ALTER TABLE `favori`
  ADD CONSTRAINT `favori_ibfk_1` FOREIGN KEY (`chercheur_id`) REFERENCES `chercheur` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `favori_ibfk_2` FOREIGN KEY (`publication_id`) REFERENCES `publication` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `notification`
--
ALTER TABLE `notification`
  ADD CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`chercheur_id`) REFERENCES `chercheur` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `publication`
--
ALTER TABLE `publication`
  ADD CONSTRAINT `publication_ibfk_1` FOREIGN KEY (`auteur_principal_id`) REFERENCES `chercheur` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `publication_coauteur`
--
ALTER TABLE `publication_coauteur`
  ADD CONSTRAINT `publication_coauteur_ibfk_1` FOREIGN KEY (`publication_id`) REFERENCES `publication` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `publication_coauteur_ibfk_2` FOREIGN KEY (`chercheur_id`) REFERENCES `chercheur` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `stat_publication_jour`
--
ALTER TABLE `stat_publication_jour`
  ADD CONSTRAINT `stat_publication_jour_ibfk_1` FOREIGN KEY (`publication_id`) REFERENCES `publication` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `unite_recherche`
--
ALTER TABLE `unite_recherche`
  ADD CONSTRAINT `unite_recherche_ibfk_1` FOREIGN KEY (`faculte_id`) REFERENCES `faculte` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
