# HealthRx
An Application for Health Monitoring and Medication Management

## Table of Contents
- [About](#about)
- [Technology Stack](#technology-stack)
- [Requirements](#requirements)
- [Installation](#installation)
- [System Actors](#system-actors)
- [ERD Diagrams](#erd-diagram)
- [System architecture](#system-architecture)
- [Functionalities](#functionalities)
- [API Endpoints](#api-endpoints)
- [Screenshots](#screenshots)
- [Credits](#credits)

## About
An application for monitoring health and managing the user's medicine cabinet. It also allows monitoring the user's physical activities. It has a wide statistics module. It is also a platform that allows users to easily contact doctors.
It has a module for the administrator, who can manage the entire system.

## Technology Stack
### Contenerization
- [Dockter](https://www.docker.com/)
### Database management systems
- [PostreSQL](https://www.postgresql.org/docs/)
- [Redis](https://redis.io/)
### Backend
- [Spring Boot 3](https://docs.spring.io/spring-boot/index.html)
- [Spring Batch](https://spring.io/projects/spring-batch)
- [Quartz](https://docs.spring.io/spring-boot/reference/io/quartz.html)
- [Kafka](https://kafka.apache.org/)
### Frontend
- [Angular 18](https://angular.dev/)
- [RxJs](https://rxjs.dev/)
- [NgRx](https://ngrx.io/)
- [Chart.js](https://www.chartjs.org/)

## Requirements
- [Docker](https://www.docker.com/) installed on your machine.
- [JDK 17 or higher](https://adoptium.net/)
- [Maven](https://maven.apache.org/install.html)
- [npm](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm) installed on your machine.
- Node.js Versions Compatibility Table

| **Node.js Version** | **Compatibility**     |
|----------------------|-----------------------|
| `^18.19.1`           | Supported            |
| `^20.11.1`           | Supported            |
| `^22.0.0`            | Supported            |

## Installation
1. Open a terminal in the main project folder.
2. Open a terminal in ```/backend``` folder and run below command for create and run docker containers:
   ```bash
   docker-compose up
   ```
3. Run below command for run backend appliaction:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
4. Access the backend application in at `localhost:8080`.
5. Open a terminal in the ```/frontend``` folder.
6. Run commands
   ```bash
   npm i
   npm run start
   ```
7. Open web application at `localhost:4200`

## System Actors
- **Guest:** an unauthenticated user who does not have access to the application's functionalities.
- **User:** an authenticated user with the role of User and access to modules for parameters, first aid kits, activities, doctors, and statistics.
- **Doctor:** an authenticated user with the role of Doctor. Initially, after creating an account, they are marked as unverified, waiting for verification by an administrator. After verification, they gain access to the module for their patients and the chat.
- **Administrator:** an authenticated user with the role of _Administrator_ or _Main Administrator_. Every administrator has access to manage users, parameters, and activities in the system. However, only an administrator with the _Main Administrator_ role has the ability to manage other users with the role of _Administrator_.

## ERD Diagrams
ERD diagrams are divided into three modules: _user_, _doctor and communication_, and _statistics_.

<div align="center">
  <p><b><i>User</i> module:</b></p>
  <img src="https://github.com/user-attachments/assets/897b1e9a-fe82-4fb3-b3f7-efa73b27d21a" alt="User Module">
</div>

<div align="center">
  <p><b><i>_Doctor and communication_</i> module:</b></p>
  <img src="https://github.com/user-attachments/assets/caa58455-cd2e-4864-bfe3-9115d5c9c757" alt="Doctor and Communication Module">
</div>

<div align="center">
  <p><b><i>_Statistics_</i> module:</b></p>
  <img src="https://github.com/user-attachments/assets/c1ef57f1-4bd6-40f4-a6f4-82c72f2c304c" alt="Statistics Module">
</div>

## System architecture
The system was created using a three-layer architecture consisting of:

- Data Layer – database management systems that store all system data (primarily _PostgreSQL_, additionally _Redis_).
- Business Logic Layer – a backend application based on _REST API_ in _Spring Boot_. It performs data operations through database queries, implements business logic, and provides endpoints for the frontend application. It also supports endpoints for _WebSocket_ using the _SockJS_ library.
- Presentation Layer – a frontend application (_SPA_) built using the _Angular_ framework. It creates the user interface, handles events, and presents data. It communicates with the backend application via the _REST API_ interface, by sending HTTP requests and using _WebSocket_ for chat and notification functionalities.

<div align="center">
  <p><b>Container diagram:</b></p>
  <img src="https://github.com/user-attachments/assets/f8bcf472-3fa9-48f8-bda7-93e619d2bf91" alt="Container diagram">
</div>

## Functionalities

## API Endpoints

## Screenshots

## Credits

This project was developed and designed by [Artur Pajor](https://github.com/)

Contact:
- [Linked-in](https://www.linkedin.com/in/artur-pajor-131334213/)
- Email: apaj04@gmail.com
