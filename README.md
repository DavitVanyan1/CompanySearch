# Company Search

[![License](https://img.shields.io/badge/license-GNU General-Public-License-v3-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-29-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-🐳-blue.svg)](https://www.docker.com/)

> **One-line pitch:** Company Search automatically fetches UK company data from the
> Companies House public register – combining web scraping with the official
> `find-and-update.company-information.service.gov.uk` API – and stores the results
> in a PostgreSQL database.

## 📖 Table of Contents

- [About the Project](#about-the-project)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
  - [1. Clone & Configure](#1-clone--configure)
  - [2. Start with Docker Compose](#2-start-with-docker-compose)
- [Usage](#usage)
  - [API Endpoints](#api-endpoints)
  - [Configuration Options](#configuration-options)
- [Project Structure](#project-structure)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)
- [Acknowledgements](#acknowledgements)

---

## About the Project

Company Search was built to solve **the task of fetching information about companies.** .
It combines two complementary data‑gathering strategies:

1. **Official Companies House REST API**
   – The application call is made
   [`find-and-update.company-information.service.gov.uk`](https://find-and-update.company-information.service.gov.uk/)
   API to retrieve structured, authoritative company profiles, officers, filings,
   and more.

2. **Web Scraping (public pages)**
   – Based on the received data, for each received company, the list of officers, workers with significant control and information about the company is collected.

All fetched data is normalised and persisted in a **PostgreSQL** database running
inside a **Docker** container, making the project portable and easy to set up.

## Features

- ✅ **Data collection** – Efficiently fetching the data using REST API calls.
- ✅ **Spring Boot backend** – Robust, production‑grade Java application.
- ✅ **PostgreSQL persistence** – Reliable storage with Flyway
  migrations.
- ✅ **Fully containerised** – One‑command startup with Docker Compose.
- ✅ **REST API for your own apps** – Expose fetched company data through clean
  HTTP endpoints.
- ✅ **Configurable scheduling** – Run data collection on demand or at regular
  intervals.
- ✅ **Error handling & retry** – Graceful handling of API rate limits and
  network issues.

## Tech Stack

| Layer          | Technology                              |
| -------------- | --------------------------------------- |
| Language       | Java 21                        		   |
| Framework      | Spring Boot                  		   |
| Database       | PostgreSQL (via Docker)      		   |
| Containerisation | Docker & Docker Compose               |
| API Client     | REST     							   |
| Scraping       | Jsoup						           |
| Migrations     | Flyway			                       |


Each of the libraries were chosen based on their compatability with the project, and based on community standards.

## Prerequisites

- **JDK** 21 or later
- **Docker** & **Docker Compose** (v2+ recommended)
- **Maven** (wrapper is already included)

## Getting Started

### 1. Clone & Configure

```bash
git clone https://github.com/[USERNAME]/[REPO].git
cd [REPO]
```bash

### 2. Run Docker

Run with the following command:

```bash
docker-compose up -d
```bash

### 3. Run the application
Execute the following command:

```bash
./mvnw spring-boot:app
```bash



## Usage

### API Endpoints

| Method | Endpoint                        | Description                        |
| ------ | ------------------------------- | ---------------------------------- |
| `GET`  | `/api/`| Retrieve company data based on a search word      |

> 📘 The `GET` endpoint returns the list of companies in JSON format, and a maximum of 100 companies are fetched, stored and returned. If same request is made twice, the second time is fetched from the database.



## Project Structure


companysearch/
├── docker-compose.yml
├── pom.xml
└── src/
    └── main/
        ├── java/com/davitvanyan/companysearch/
        │   ├── CompanysearchApplication.java
        │   ├── model/entity/
        │   │   ├── SearchQuery.java
        │   │   ├── Company.java
        │   │   ├── Officer.java
        │   │   └── PersonWithSignificantControl.java
        │   └── repository/
        │       ├── SearchQueryRepository.java
        │       └── CompanyRepository.java
        └── resources/
            ├── application.properties
            └── db/migration/
                └── V1__init.sql



## Roadmap

- [ ] Include several additional features.

## Contributing

Contributions are welcome! I would love to hear any suggestions.

## License

Distributed under the GNU v3 License. See `LICENSE` for more information.

## Contact

Davit Vanyan – [david.vanyan1@gmail.com](mailto:david.vanyan1@gmail.com)

Project Link: [https://github.com/DavitVanyan1/CompanySearch](https://github.com/DavitVanyan1/CompanySearch)

## Acknowledgements

- [Companies House Developer Hub](https://developer.company-information.service.gov.uk/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Docker](https://www.docker.com/)
