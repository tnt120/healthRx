# HealthRx
An Application for Health Monitoring and Medication Management

## Table of Contents
- [About](#about)
- [Technology Stack](#technology-stack)
- [Requirements](#requirements)
- [Installation](#installation)
- [System Actors](#system-actors)
- [ERD Diagrams](#erd-diagrams)
- [System architecture](#system-architecture)
- [Functionalities](#functionalities)
- [API Endpoints](#api-endpoints)
- [Screenshots](#screenshots)
- [Credits](#credits)

## About
An application for monitoring health and managing the user's medicine cabinet. It also allows monitoring the user's physical activities. It has a wide statistics module. It is also a platform that allows users to easily contact doctors.
It has a module for the administrator, who can manage the entire system.
The application also uses the [API provided by the Polish Ministry of Digital Affairs](https://dane.gov.pl/pl/dataset/397,rejestr-produktow-leczniczych) to download and update the database of medicines available for use in Poland on a daily basis.

## Technology Stack
<table align="center" style="border: 1px solid black; border-collapse: collapse; text-align: center; width: 80%;">
  <tr>
    <th style="border: 1px solid black; padding: 10px; vertical-align: middle;">Contenerization</th>
    <th style="border: 1px solid black; padding: 10px; vertical-align: middle;">Database management systems</th>
  </tr>
  <tr>
    <td style="border: 1px solid black; padding: 10px; vertical-align: middle;">
      <ul style="list-style-type: none; margin: 0; padding: 0;">
        <li><a href="https://www.docker.com/">Docker</a></li>
      </ul>
    </td>
    <td style="border: 1px solid black; padding: 10px; vertical-align: middle;">
      <ul style="list-style-type: none; margin: 0; padding: 0;">
        <li><a href="https://www.postgresql.org/docs/">PostgreSQL</a></li>
        <li><a href="https://redis.io/">Redis</a></li>
      </ul>
    </td>
  </tr>
  <tr>
    <th style="border: 1px solid black; padding: 10px; vertical-align: middle;">Backend</th>
    <th style="border: 1px solid black; padding: 10px; vertical-align: middle;">Frontend</th>
  </tr>
  <tr>
    <td style="border: 1px solid black; padding: 10px; vertical-align: middle;">
      <ul style="list-style-type: none; margin: 0; padding: 0;">
        <li><a href="https://docs.spring.io/spring-boot/index.html">Spring Boot 3</a></li>
        <li><a href="https://spring.io/projects/spring-batch">Spring Batch</a></li>
        <li><a href="https://docs.spring.io/spring-boot/reference/io/quartz.html">Quartz</a></li>
        <li><a href="https://kafka.apache.org/">Kafka</a></li>
      </ul>
    </td>
    <td style="border: 1px solid black; padding: 10px; vertical-align: middle;">
      <ul style="list-style-type: none; margin: 0; padding: 0;">
        <li><a href="https://angular.dev/">Angular 18</a></li>
        <li><a href="https://rxjs.dev/">RxJs</a></li>
        <li><a href="https://ngrx.io/">NgRx</a></li>
        <li><a href="https://www.chartjs.org/">Chart.js</a></li>
      </ul>
    </td>
  </tr>
</table>


## Requirements
- [Docker](https://www.docker.com/) installed on your machine.
- [JDK 17 or higher](https://adoptium.net/)
- [Maven](https://maven.apache.org/install.html)
- [npm](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm) installed on your machine.
- Node.js Versions Compatibility Table

<div align="center">

<table>
  <tr>
    <th>Node.js Version</th>
    <th>Compatibility</th>
  </tr>
  <tr>
    <td>^18.19.1</td>
    <td>Supported</td>
  </tr>
  <tr>
    <td>^20.11.1</td>
    <td>Supported</td>
  </tr>
  <tr>
    <td>^22.0.0</td>
    <td>Supported</td>
  </tr>
</table>

</div>

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
  <img width="1000px" src="https://github.com/user-attachments/assets/897b1e9a-fe82-4fb3-b3f7-efa73b27d21a" alt="User Module">
</div>

<div align="center">
  <p><b><i>Doctor and communication</i> module:</b></p>
  <img width="1000px" src="https://github.com/user-attachments/assets/caa58455-cd2e-4864-bfe3-9115d5c9c757" alt="Doctor and Communication Module">
</div>

<div align="center">
  <p><b><i>Statistics</i> module:</b></p>
  <img width="1000px" src="https://github.com/user-attachments/assets/c1ef57f1-4bd6-40f4-a6f4-82c72f2c304c" alt="Statistics Module">
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
<div align="center">

<table>
  <tr>
    <th>Functionality</th>
    <th>User</th>
    <th>Doctor</th>
    <th>Guest</th>
    <th>Admin</th>
  </tr>
  <tr>
    <td>Login</td>
    <td>✔️</td>
    <td>✔️</td>
    <td>❌</td>
    <td>✔️</td>
  </tr>
  <tr>
    <td>Registration</td>
    <td>❌</td>
    <td>❌</td>
    <td>✔️</td>
    <td>❌</td>
  </tr>
  <tr>
    <td>Account verification</td>
    <td>✔️</td>
    <td>✔️</td>
    <td>❌</td>
    <td>❌</td>
  </tr>
  <tr>
    <td>Managing health parameters and tracking their measurements</td>
    <td>✔️</td>
    <td>❌</td>
    <td>❌</td>
    <td>✔️</td>
  </tr>
  <tr>
    <td>Comparing health parameter measurements to health norms</td>
    <td>✔️</td>
    <td>❌</td>
    <td>❌</td>
    <td>✔️</td>
  </tr>
  <tr>
    <td>Managing the user's medicine cabinet</td>
    <td>✔️</td>
    <td>❌</td>
    <td>❌</td>
    <td>❌</td>
  </tr>
  <tr>
    <td>Tracking medication intake and stock</td>
    <td>✔️</td>
    <td>❌</td>
    <td>❌</td>
    <td>❌</td>
  </tr>
  <tr>
    <td>Tracking physical activities performed</td>
    <td>✔️</td>
    <td>❌</td>
    <td>❌</td>
    <td>✔️</td>
  </tr>
  <tr>
    <td>Email and push notifications for reminders</td>
    <td>✔️</td>
    <td>❌</td>
    <td>❌</td>
    <td>❌</td>
  </tr>
  <tr>
    <td>Health parameter statistics, medication, and activities</td>
    <td>✔️</td>
    <td>❌</td>
    <td>❌</td>
    <td>✔️</td>
  </tr>
  <tr>
    <td>User and doctor friendship system</td>
    <td>✔️</td>
    <td>✔️</td>
    <td>❌</td>
    <td>❌</td>
  </tr>
  <tr>
    <td>Online chat between users and doctors</td>
    <td>✔️</td>
    <td>✔️</td>
    <td>❌</td>
    <td>❌</td>
  </tr>
  <tr>
    <td>Generating user health reports</td>
    <td>❌</td>
    <td>✔️</td>
    <td>❌</td>
    <td>❌</td>
  </tr>
  <tr>
    <td>System statistics (number of users, doctors, etc.)</td>
    <td>❌</td>
    <td>❌</td>
    <td>❌</td>
    <td>✔️</td>
  </tr>
  <tr>
    <td>Managing users in the system</td>
    <td>❌</td>
    <td>❌</td>
    <td>❌</td>
    <td>✔️</td>
  </tr>
  <tr>
    <td>Managing parameters in the system</td>
    <td>❌</td>
    <td>❌</td>
    <td>❌</td>
    <td>✔️</td>
  </tr>
  <tr>
    <td>Managing activities in the system</td>
    <td>❌</td>
    <td>❌</td>
    <td>❌</td>
    <td>✔️</td>
  </tr>
  <tr>
    <td>Access to the latest medication database</td>
    <td>✔️</td>
    <td>✔️</td>
    <td>❌</td>
    <td>✔️</td>
  </tr>
</table>

</div>


## API Endpoints
The API endpoint specification was created using [Swagger](https://swagger.io/). Below are screenshots of the controllers and endpoints available in the application.

<div align="center">
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/55f77447-01d9-4142-b0ad-87d916a30673" />
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/7f293524-cc78-4893-991f-e24e7f66fed0" />
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/68c0fa2b-a7b4-45a2-958f-766f6770ccae" />
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/b5b2fa21-384f-4f2e-9bea-4052e7ab21cd" />
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/f6ae6f8c-e3a6-403e-a330-9e1d66234a30" />
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/e6cc3329-8ada-4cb8-9472-7cf954b0b5a4" />
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/76f5da9f-c02d-4470-82d9-d929927efcc6" />
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/939e6eaf-c991-4cff-9d69-13ba850d467d" />
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/78dae8a7-0965-4278-a285-dd87ec8b8f4f" />
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/1cec8e56-0580-42d7-b732-35b54a93831b" />
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/495d7f2b-bc04-424a-a758-0a980f939358" />
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/843661ab-ad82-4a02-b2e5-33c5d9a82feb" />
   <img width="655" alt="image" src="https://github.com/user-attachments/assets/c867982b-9871-4921-aa6f-34552a309d3c" />
</div>

## Screenshots


## Credits

This project was developed and designed by [Artur Pajor](https://github.com/)

Project uses [Polish Ministry of Digital Affairs API's](https://dane.gov.pl/pl/dataset/397,rejestr-produktow-leczniczych) for fetching medicines list.

Contact:
- [Linked-in](https://www.linkedin.com/in/artur-pajor-131334213/)
- Email: apaj04@gmail.com
