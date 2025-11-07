# SmartLogi API Documentation

## Overview

SmartLogi is a comprehensive logistics management system API that handles package (colis) delivery operations with automated email notifications. The system manages senders, receivers, delivery personnel (livreur), products, and provides real-time tracking with complete delivery history.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
    - [Colis Management](#colis-management)
    - [Livreur Management](#livreur-management)
    - [Product Management](#product-management)
    - [Receiver Management](#receiver-management)
    - [Sender Management](#sender-management)
    - [Search](#search)
    - [Statistics](#statistics)
- [Data Models](#data-models)
- [Email Notifications](#email-notifications)
- [Business Rules](#business-rules)
- [Error Handling](#error-handling)
- [Response Format](#response-format)

## Features

- **Package Management**: Complete CRUD operations with automatic weight calculation from products
- **User Management**: Manage senders, receivers, and delivery personnel
- **Assignment System**: Intelligent assignment of packages to delivery personnel by zone
- **Status Tracking**: Real-time status updates with automatic history logging
- **Email Notifications**: Automated HTML email notifications for package events
- **Filtering & Pagination**: Advanced filtering by status, zone, ville, and priority
- **Global Search**: Search across all entities (colis, senders, receivers, livreurs)
- **Statistics Dashboard**: Aggregated delivery statistics by livreur and zone
- **Complete History**: Full audit trail for every package status change

## Technology Stack

- **Framework**: Spring Boot 3.3.4
- **Language**: Java 17+
- **API Documentation**: Swagger/OpenAPI 3
- **Validation**: Jakarta Validation
- **Email**: Spring Mail (JavaMailSender)
- **Database**: Entity: Liquibase / ORM Management: JPA/Hibernate
- **Pagination**: Spring Data

## Architecture

### Service Layer
- **ColisService**: Core business logic for package management
- **LivreurService**: Delivery personnel management
- **ProductService**: Product catalog management
- **ReceiverService**: Receiver management
- **SenderService**: Sender management
- **SearchService**: Global search functionality
- **EmailService**: Email notification service

### Exception Handling
- **ResourceNotFoundException**: Resource not found (404)
- **OperationNotAllowedException**: Invalid operation (405)
- **AccessDeniedException**: Unauthorized access (401)
- **GlobalExceptionHandler**: Centralized exception handling with consistent API responses

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Spring Boot 3.x
- SMTP server credentials for email notifications

### Configuration

Add to your `application.properties`:

```properties
# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/data_base_name
spring.datasource.username=your-database-username
spring.datasource.password=your-database-password
```

### Installation

1. Clone the repository
```bash
git clone https://github.com/WissamDouskary/Smart-Delivery-v2.git
cd smartlogi
```

2. Configure email settings in `application.properties`

3. Build the project
```bash
mvn clean install
```

4. Run the application
```bash
mvn spring-boot:run
```

5. Access Swagger UI
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Colis Management

Base URL: `/api/colis`

#### Create a New Colis
```http
POST /api/colis
```

**Request Body:**
```json
{
  "description": "colis description",
  "vileDistination": "city name",
  "receiver": {
    "prenom": "sender_first_name",
    "nom": "receiver_last_name",
    "telephone": "receiver_phone_number",
    "email": "receiver_email",
    "adresse": "receiver_adress"
  },
  "sender": {
    "nom": "sender_last_name",
    "prenom": "sender_first_name",
    "adresse": "sender_adress",
    "email": "sender_email",
    "telephone": "sender_phone_number"
  },
  "products": [
    {
      "nom": "product_name",
      "quantity": 0,
      "category": "product_category",
      "poids": 0,
      "price": 0
    },
    {
      "id": "exist_product_id",
      "quantity": "product_quantity"
    }
  ],
  "city": {
    "id": "zone_id"
  },
  "priority": "colis_priority"
}
```

**Features:**
- Automatically calculates total weight from products
- Creates initial history entry
- Sends email notification to sender
- You can enter infos or id of receiver and sender if we have a new sender or receiver
- Create or add products from database.

**Response:**
```json
{
  "message": "Colis ajouté avec succès",
  "data": {
    "id": "colis-id",
    "description": "Package description",
    "poids": 15.5,
    "status": "CREATED",
    "priority": "URGENT",
    "vileDistination": "Casablanca",
    "sender": {...},
    "receiver": {...},
    "city": {...},
    "products": [...],
    "historiqueLivraisonList": [...]
  }
}
```

#### List All Colis with Filters
```http
GET /api/colis?status={status}&zone={zone}&ville={ville}&priority={priority}&page={page}&size={size}
```

**Query Parameters:**
- `status` (optional): `CREATED`, `COLLECTED`, `IN_STOCK`, `LIVRED`
- `zone` (optional): Zone name
- `ville` (optional): City name
- `priority` (optional): `URGENT`, `NORMALE`, `NON_URGENT`
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)

**Response:** Paginated list of packages matching filters

#### Get Colis Summary
```http
GET /api/colis/summary
```

Returns aggregated statistics:
```json
{
  "message": "Résumé des colis récupéré avec succès",
  "data": {
    "groupByZone": {
      "Casablanca": 45,
      "Rabat": 32
    },
    "groupByStatus": {
      "CREATED": 20,
      "COLLECTED": 15,
      "IN_STOCK": 30,
      "LIVRED": 12
    },
    "groupByPriority": {
      "URGENT": 25,
      "NORMALE": 40,
      "NON_URGENT": 12
    }
  }
}
```

#### Get Colis for Client (Sender)
```http
GET /api/colis/client/{sender_id}
```

Returns all packages sent by a specific sender.

#### Get Colis for Receiver
```http
GET /api/colis/receiver/{receiver_id}
```

Returns simplified package information for receiver:
```json
{
  "message": "Colis du destinataire récupérés",
  "data": [
    {
      "sender": {...},
      "status": "IN_STOCK"
    }
  ]
}
```

#### Get Colis for Livreur
```http
GET /api/colis/livreur/{livreur_id}
```

Returns all packages assigned to a delivery person.

#### Update Colis Status (by Livreur)
```http
PATCH /api/colis/{colis_id}/livreur/{livreur_id}
Content-Type: application/json

"IN_STOCK"
```

**Business Rules:**
- Livreur can only update packages assigned to them
- Automatically logs status change in history
- Sends email notification to sender on status change

**Email Notification:** Sent when status changes

#### Assign Colis to Livreur
```http
PATCH /api/colis/affect/{colis_id}/livreur/{livreur_id}
```

**Business Rules:**
- Livreur must be in the same zone as the package
- Cannot reassign to the same livreur
- Creates history entry
- Sends detailed email notification with livreur information

**Email Notification:** Includes livreur details and contact information

#### Update Colis
```http
PUT /api/colis/{id}
```

**Request Body:**
```json
{
  "description": "Updated description",
  "poids": 20.5,
  "receiverId": "new-receiver-id",
  "senderId": "new-sender-id",
  "livreurId": "new-livreur-id",
  "cityId": "zone-id",
  "productsIds": ["product-1", "product-2"],
  "status": "COLLECTED",
  "priority": "NORMALE"
}
```

**Business Rules:**
- Cannot change to a different zone
- All fields are optional
- Creates history entry

#### Delete Colis
```http
DELETE /api/colis/{id}
```

#### Get Colis History
```http
GET /api/colis/{id}/historique
```

Returns complete package information including full delivery history.

### Livreur Management

Base URL: `/api/livreur`

#### Create a New Livreur
```http
POST /api/livreur
```

**Request Body:**
```json
{
  "nom": "Alami",
  "prenom": "Mohammed",
  "telephone": "+212612345678",
  "vehicle": "Moto",
  "city": {
    "id": "zone-id"
  }
}
```

**Response:**
```json
{
  "message": "Livreur ajouter avec succes",
  "data": {
    "id": "livreur-id",
    "nom": "Alami",
    "prenom": "Mohammed",
    "telephone": "+212612345678",
    "vehicle": "Moto",
    "city": {
      "id": "zone-id",
      "nom": "Casablanca",
      "codePostal": 20000
    }
  }
}
```

### Product Management

Base URL: `/api/product`

#### Create a New Product
```http
POST /api/product
```

**Request Body:**
```json
{
  "nom": "Laptop Dell",
  "category": "Electronics",
  "poids": 2.5,
  "price": 15000.00
}
```

**Validation:**
- All fields are required
- Weight must be positive
- Price must be positive

### Receiver Management

Base URL: `/api/receiver`

#### Create a New Receiver
```http
POST /api/receiver
```

**Request Body:**
```json
{
  "nom": "Benali",
  "prenom": "Fatima",
  "email": "fatima.benali@email.com",
  "telephone": "+212698765432",
  "adresse": "123 Rue Mohammed V, Casablanca"
}
```

### Sender Management

Base URL: `/api/sender`

#### Create a New Sender
```http
POST /api/sender
```

**Request Body:**
```json
{
  "nom": "Tazi",
  "prenom": "Hassan",
  "email": "hassan.tazi@email.com",
  "telephone": "+212687654321",
  "adresse": "456 Boulevard Zerktouni, Casablanca"
}
```

#### Find Sender by ID
```http
GET /api/sender/{id}
```

### Search

Base URL: `/api/search`

#### Global Search
```http
GET /api/search?keyword={keyword}
```

**Search Scope:**
- **Colis**: description, destination city
- **Senders**: nom, prenom, email
- **Receivers**: nom, prenom, email
- **Livreurs**: nom, prenom, telephone, city name

**Response:**
```json
{
  "message": "Search fait avec success",
  "data": {
    "colis": [...],
    "senders": [...],
    "receivers": [...],
    "livreurs": [...]
  }
}
```

### Statistics

#### Get Delivery Statistics
```http
GET /api/statistiques/livreurs-zones
```

Returns delivery statistics grouped by livreur and zone:

```json
{
  "message": "get Stats avec succes",
  "data": [
    {
      "livreurNom": "Alami Mohammed",
      "zoneNom": "Casablanca",
      "nombreColis": 45,
      "poidsTotal": 523.5
    }
  ]
}
```

## Data Models

### Status Enum
```java
public enum Status {
    CREATED,      // Package created
    COLLECTED,    // Picked up by livreur
    IN_STOCK,     // In warehouse
    LIVRED        // Delivered
}
```

### Priority Enum
```java
public enum Priority {
    URGENT,       // High priority
    NORMALE,      // Normal priority
    NON_URGENT    // Low priority
}
```

### ColisResponseDTO
```json
{
  "id": "string",
  "description": "string",
  "poids": 0.0,
  "vileDistination": "string",
  "status": "CREATED",
  "priority": "NORMALE",
  "receiver": { ReceiverResponseDTO },
  "sender": { SenderResponseDTO },
  "livreur": { LivreurResponseDTO },
  "city": { ZoneResponseDTO },
  "productsList": [ ProductResponseDTO ],
  "historiqueLivraisonList": [ HistoriqueLivraisonResponseDTO ]
}
```

### HistoriqueLivraisonResponseDTO
```json
{
  "id": "string",
  "status": "CREATED",
  "changementDate": "2025-11-04T10:30:00Z",
  "comment": "Colis créé par le client"
}
```

### ApiResponse Structure
```json
{
  "message": "Operation result message",
  "data": { /* Response data or null */ }
}
```

## Email Notifications

The system sends automated HTML email notifications for the following events:

### 1. Colis Creation
**Trigger:** When a new package is created  
**Recipient:** Sender  
**Subject:** `📦 Your Colis Has Been Successfully Created — [Tracking ID: xxx]`  
**Content:** Package details, tracking ID, destination, receiver info

### 2. Livreur Assignment
**Trigger:** When a package is assigned to a delivery person  
**Recipient:** Sender  
**Subject:** `🚚 Your Colis Has Been Assigned to a Delivery Agent — [Tracking ID: xxx]`  
**Content:** Package details, livreur name and contact, delivery zone

### 3. Status Update
**Trigger:** When package status changes  
**Recipient:** Sender  
**Subject:** `🔄 Colis Status Updated — [Tracking ID: xxx]`  
**Content:** Previous status, new status, livreur information

All emails are sent in HTML format with professional styling and include relevant package tracking information.

## Business Rules

### Colis Assignment Rules
1. **Zone Matching**: Livreur must be assigned to the same zone as the package destination
2. **No Duplicate Assignment**: Cannot assign the same livreur to a package already assigned to them
3. **Status Update Authorization**: Only the assigned livreur can update the package status

### Colis Update Rules
1. **Zone Consistency**: Cannot change package to a different zone after creation
2. **Weight Calculation**: Total weight is automatically calculated from products
3. **History Tracking**: All changes are logged in the delivery history

### Validation Rules
1. **Description**: 2-100 characters
2. **Weight**: Must be positive
3. **Email Format**: Valid email addresses required
4. **Required Fields**: Sender, receiver, and destination city are mandatory for package creation

## Error Handling

### Exception Types

#### ResourceNotFoundException (404)
Thrown when a requested resource doesn't exist:
```json
{
  "message": "Aucun colis avec id: xxx",
  "data": null
}
```

#### OperationNotAllowedException (405)
Thrown for invalid business operations:
```json
{
  "message": "livreur est deja affecter sur ce Colis",
  "data": null
}
```

#### AccessDeniedException (401)
Thrown for unauthorized operations:
```json
{
  "message": "You can't change statut for colis not assigned to you!",
  "data": null
}
```

#### Validation Errors (400)
Thrown for invalid input data:
```json
{
  "message": "Validation échouée",
  "data": {
    "description": "La description doit être entre 2 et 100 caractères",
    "poids": "le poids doit etre positive"
  }
}
```

#### Internal Server Error (500)
Generic server errors:
```json
{
  "message": "Internal server error",
  "data": null
}
```

## Response Format

All API responses follow a consistent structure:

### Success Response
```json
{
  "message": "Descriptive success message",
  "data": { /* Response object or array */ }
}
```

### Error Response
```json
{
  "message": "Error description",
  "data": null
}
```

### Paginated Response
```json
{
  "message": "Success message",
  "data": {
    "content": [ /* Array of items */ ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalPages": 5,
    "totalElements": 100,
    "size": 20,
    "number": 0
  }
}
```

## Pagination

Endpoints supporting pagination accept standard Spring Data parameters:

- `page`: Page number (starting from 0)
- `size`: Number of items per page (default: 20)
- `sort`: Sort field and direction

Example:
```
GET /api/colis?page=0&size=20&sort=createdAt,desc&status=CREATED
```

## Common Scenarios

### Create and Track a Package

1. **Create Sender**
```http
POST /api/sender
```

2. **Create Receiver**
```http
POST /api/receiver
```

3. **Create Products**
```http
POST /api/product
```

4. **Create Package**
```http
POST /api/colis
{
  "description": "Electronics package",
  "sender": {"id": "sender-id"},
  "receiver": {"id": "receiver-id"},
  "city": {"id": "zone-id"},
  "productIds": ["product-1", "product-2"],
  "priority": "URGENT"
}
```
→ Sender receives email confirmation

5. **Assign to Livreur**
```http
PATCH /api/colis/affect/{colis-id}/livreur/{livreur-id}
```
→ Sender receives assignment notification

6. **Update Status**
```http
PATCH /api/colis/{colis-id}/livreur/{livreur-id}
"COLLECTED"
```
→ Sender receives status update notification

7. **Track History**
```http
GET /api/colis/{colis-id}/historique
```

## Best Practices

1. **Always validate input** - Use the validation annotations
2. **Handle exceptions gracefully** - All endpoints return consistent error formats
3. **Check business rules** - Respect zone matching and assignment rules
4. **Monitor email delivery** - Ensure SMTP configuration is correct
5. **Use pagination** - For listing endpoints with large datasets
6. **Search efficiently** - Use the global search for quick lookups
7. **Track history** - Leverage the automatic history logging for audit trails

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

For questions or support:
- Email: [douskary.wissam@gmail.com]
- Documentation: `/swagger-ui.html`

---

**Version:** 1.0.0  
**Last Updated:** November 2025