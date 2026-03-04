# GPUB - Plateforme Nationale des Publications Scientifiques

> Backend REST API for managing scientific publications of Mauritanian universities.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.x-blue)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow)
![Swagger](https://img.shields.io/badge/Docs-Swagger-85EA2D)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Endpoints](#endpoints)
- [Roles & Permissions](#roles--permissions)
- [Database Schema](#database-schema)
- [Project Structure](#project-structure)

---

## Overview

GPUB is a national platform for managing and publishing scientific research from Mauritanian universities. It allows researchers to publish their work, track statistics, collaborate with co-authors, and export their publications in multiple formats.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT |
| Database | MySQL 8.x |
| ORM | Spring Data JPA / Hibernate |
| Documentation | Springdoc OpenAPI (Swagger) |
| File Export | Apache POI (Excel), CSV, BibTeX |
| Build Tool | Maven |

---

## Features

### 🔐 Authentication & Security
- JWT-based authentication (24h token expiration)
- BCrypt password hashing
- Role-based access control (USER, ADMIN, SUPER_ADMIN)
- Account activation/deactivation
- Change password endpoint

### 📄 Publications
- Full CRUD with ownership validation
- Advanced search, filter and pagination
- Duplicate detection (title + year + author)
- Publication moderation (PUBLIE, EN_ATTENTE, RETIRE)
- Co-authors management

### 📁 Files
- PDF upload and download
- Profile photo upload
- Auto-delete old files on update

### 📊 Statistics
- View and download tracking per publication
- Daily stats breakdown
- Date range filtering
- Stats export (CSV, Excel)

### 🔔 Notifications
- In-app notification system
- Auto-triggered on: views milestone, downloads milestone, co-author added
- Mark as read / mark all as read
- Pagination support

### ❤️ Favorites
- Add/remove publications to favorites
- Check if a publication is favorited
- List all favorite publications

### 📤 Export
- Publications export: CSV, BibTeX, Excel
- Stats export: CSV, Excel
- Date range support for stats export

### 🏛️ Admin System
- **SUPER_ADMIN**: national dashboard, all users/publications, role assignment
- **ADMIN**: university-scoped dashboard, moderation of their university
- Publication moderation (approve, retire)
- Chercheur activation/deactivation
- Global and per-university statistics

### 🏢 University Structures
- Universite CRUD
- Faculte CRUD
- Unite de Recherche CRUD

---

## Getting Started

### Prerequisites

- Java 17+
- MySQL 8+
- Maven 3.x

### Installation

**1. Clone the repository:**
```bash
git clone https://github.com/YOUR_USERNAME/gpub-backend.git
cd gpub-backend
```

**2. Create the database:**
```sql
CREATE DATABASE gpub_bd_v2;
```

**3. Configure `application.properties`:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gpub_bd_v2
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD

# JWT
jwt.secret=YOUR_JWT_SECRET
jwt.expiration=86400000

# File uploads
file.upload.dir=uploads/

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.try-it-out-enabled=true
```

**4. Run the application:**
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Create First SUPER_ADMIN

Run this SQL after starting the app:
```sql
INSERT INTO chercheur (nom, email, hash_mdp, role, actif, date_creation)
VALUES (
    'Super Admin',
    'superadmin@ministere.mr',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'SUPER_ADMIN',
    TRUE,
    NOW()
);
-- Default password: admin123 (change immediately after first login)
```

---

## API Documentation

Once running, visit:
```
http://localhost:8080/swagger-ui.html
```

Full interactive API documentation with try-it-out support.

To test protected endpoints in Swagger:
1. Use `POST /api/auth/login` to get a token
2. Click the **Authorize** button (top right)
3. Enter: `Bearer YOUR_TOKEN`
4. All protected endpoints will now work

---

## Endpoints

### Authentication
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | Public | Register new researcher |
| POST | `/api/auth/login` | Public | Login and get JWT token |
| GET | `/api/auth/verify` | Protected | Verify JWT token |
| GET | `/api/auth/me` | Protected | Get current user profile |
| PUT | `/api/auth/me/password` | Protected | Change password |

### Publications
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/publications` | Public | Search and list publications |
| GET | `/api/publications/{id}` | Public | Get publication (tracks view) |
| POST | `/api/publications` | Protected | Create publication |
| PUT | `/api/publications/{id}` | Protected | Update publication (owner only) |
| DELETE | `/api/publications/{id}` | Protected | Delete publication (owner only) |
| GET | `/api/publications/export` | Public | Export publications (csv/bibtex/xlsx) |

### Co-Authors
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/publications/{id}/coauteurs` | Public | List co-authors |
| POST | `/api/publications/{id}/coauteurs/{chercheurId}` | Protected | Add co-author |
| DELETE | `/api/publications/{id}/coauteurs/{chercheurId}` | Protected | Remove co-author |

### Favorites
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/me/favoris` | Protected | List my favorites |
| POST | `/api/publications/{id}/favori` | Protected | Add to favorites |
| DELETE | `/api/publications/{id}/favori` | Protected | Remove from favorites |
| GET | `/api/publications/{id}/favori` | Protected | Check if favorited |

### Statistics
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/stats/publication/{id}` | Public | Publication stats |
| GET | `/api/stats/chercheur/{id}` | Public | Researcher stats |
| GET | `/api/stats/me` | Protected | My stats |
| GET | `/api/stats/me/detail` | Protected | Detailed stats with date range |

### Export
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/publications/export` | Public | Export publications |
| GET | `/api/me/publications/export` | Protected | Export my publications |
| GET | `/api/me/stats/export` | Protected | Export my stats |

### Notifications
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/me/notifications` | Protected | List notifications |
| GET | `/api/me/notifications/unread-count` | Protected | Get unread count |
| PATCH | `/api/me/notifications/{id}/read` | Protected | Mark as read |
| PATCH | `/api/me/notifications/read-all` | Protected | Mark all as read |
| DELETE | `/api/me/notifications/{id}` | Protected | Delete notification |

### Admin
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/admin/chercheurs` | ADMIN+ | List researchers |
| PATCH | `/api/admin/chercheurs/{id}/status` | ADMIN+ | Activate/deactivate |
| PATCH | `/api/admin/chercheurs/{id}/role` | SUPER_ADMIN | Assign role |
| GET | `/api/admin/publications` | ADMIN+ | List publications |
| PATCH | `/api/admin/publications/{id}/statut` | ADMIN+ | Moderate publication |
| GET | `/api/admin/stats/global` | ADMIN+ | Dashboard stats |

### University Structures
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET/POST/PUT/DELETE | `/api/universites` | GET public | Universities CRUD |
| GET/POST/PUT/DELETE | `/api/facultes` | GET public | Faculties CRUD |
| GET/POST/PUT/DELETE | `/api/unites` | GET public | Research units CRUD |

### Files
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/files/upload` | Protected | Upload PDF |
| POST | `/api/files/upload/photo/{id}` | Protected | Upload profile photo |
| GET | `/api/files/download/{filename}` | Public | Download file (tracks download) |
| DELETE | `/api/files/delete` | Protected | Delete file |

---

## Roles & Permissions

| Feature | USER | ADMIN | SUPER_ADMIN |
|---------|------|-------|-------------|
| Browse publications | ✅ | ✅ | ✅ |
| Create publications | ✅ | ✅ | ✅ |
| Edit own publications | ✅ | ✅ | ✅ |
| Moderate publications | ❌ | ✅ (own university) | ✅ (all) |
| Manage researchers | ❌ | ✅ (own university) | ✅ (all) |
| Assign roles | ❌ | ❌ | ✅ |
| View dashboard stats | ❌ | ✅ (own university) | ✅ (national) |
| University CRUD | ❌ | ❌ | ✅ |

---

## Database Schema

```
universite        (id, nom)
faculte           (id, nom, universite_id)
unite_recherche   (id, nom, faculte_id)
chercheur         (id, nom, email, hash_mdp, photo_url, bio, domaine,
                   role, actif, unite_id, admin_universite_id, date_creation)
publication       (id, titre, resume, mots_cles, domaine, date_publication,
                   pdf_url, affiliation_texte, statut, auteur_principal_id,
                   created_at, updated_at)
publication_coauteur (id, publication_id, chercheur_id)
stat_publication_jour (id, publication_id, jour, vues, telechargements)
favori            (id, chercheur_id, publication_id, created_at)
notification      (id, chercheur_id, type, titre, message, lien, lu, created_at)
```

---

## Project Structure

```
src/main/java/com/example/gpub/
├── config/
│   ├── SecurityConfig.java
│   ├── SwaggerConfig.java
│   └── JwtAuthenticationFilter.java
├── controller/
│   ├── AuthController.java
│   ├── PublicationController.java
│   ├── ChercheurController.java
│   ├── AdminController.java
│   ├── CoAuteurController.java
│   ├── AuthorController.java
│   ├── FavoriController.java
│   ├── NotificationController.java
│   ├── ExportController.java
│   ├── StatController.java
│   ├── FileUploadController.java
│   ├── UniversiteController.java
│   ├── FaculteController.java
│   └── UniteRechercheController.java
├── service/
│   ├── AuthService.java
│   ├── PublicationService.java
│   ├── ChercheurService.java
│   ├── CoAuteurService.java
│   ├── FavoriService.java
│   ├── NotificationService.java
│   ├── ExportService.java
│   ├── StatService.java
│   └── FileUploadService.java
├── repository/
│   ├── ChercheurRepository.java
│   ├── PublicationRepository.java
│   ├── PublicationCoauteurRepository.java
│   ├── FavoriRepository.java
│   ├── NotificationRepository.java
│   ├── StatPublicationJourRepository.java
│   ├── UniversiteRepository.java
│   ├── FaculteRepository.java
│   └── UniteRechercheRepository.java
├── entity/
│   ├── Chercheur.java
│   ├── Publication.java
│   ├── PublicationCoauteur.java
│   ├── Favori.java
│   ├── Notification.java
│   ├── StatPublicationJour.java
│   ├── Universite.java
│   ├── Faculte.java
│   └── UniteRecherche.java
├── dto/
│   ├── ChercheurDTO.java
│   ├── PublicationDTO.java
│   ├── LoginRequest.java
│   └── LoginResponse.java
└── util/
    └── JwtUtil.java
```

---

## License

This project is developed for the Ministry of Higher Education and Scientific Research of Mauritania.

---

*Built with ❤️ using Spring Boot*
