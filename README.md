# Energy Management System

## Description
This project is designed to manage users, devices, and their energy consumption.  
Users can have one of two roles: 
- admin: an admin can perform CRUD operations on clients and devices, as well as assign devices to clients.  
- client: a client can view their assigned devices and receive real-time notifications if a device exceeds its preset maximum hourly energy consumption.  

The system uses RabbitMQ for asynchronous communication, where energy measurements are sent via queues and processed in real-time.

## Architecture

The backend consists of 3 microservices:  
1. Device Management Microservice: manages devices and their assigned users
2. User Management Microservice: manages users, authentication and authorization.  
3. Monitoring & Communication Microservice: listens to energy measurements, stores the hourly consumption, and triggers alerts if necessary.  

### Backend Layers:
- Controllers: handle HTTP requests (GET, POST, PUT, DELETE) and forward them to services.  
- Services: implement business logic and interact with repositories.  
- Repositories: interface with the database using JPA for CRUD operations.  
- Configuration: handles RabbitMQ integration for asynchronous messaging.  
- Database: stores data for devices, users, and energy measurements.  

## Asynchronous Communication

The system uses RabbitMQ for event-driven communication:  
- A Message Producer (written in Python) reads energy consumption values from a CSV file (`sensor.csv`) and publishes them to a queue (`masuratori`)
- The Monitoring Microservice consumes these messages, stores the read hourly energy consumption, and checks if the device exceeds its maximum allowed consumption.  
- If the limit is exceeded, a WebSocket notification is sent to the client in real time.
- An extra queue (`device_changes`) is used to send an alert from the Device Microservice to the Monitoring Microservice to inform it about any change made on a device (`EVENT TYPE=CREATE, UPDATE, DELETE`)

Example JSON message sent to the queue:
```json
{
  "timestamp": 1570654800000,
  "device_id": "1",
  "measurement_value": 0.1
}
````
where the `device_id` is set in `config.py` in the `producer` folder.

Example Docker log when a device gets assigned a user:
```
[OK] mesaj primit de la coada 'device_changes_queue': "{\"id\":1,\"description\":\"Smart Meter 123\",\"address\":\"Str. Independentei 102\",\"maxHEC\":120.12,\"userId\":2,\"eventType\":\"UPDATE\"}"
[OK] mesaj deserializat cu succes: DeviceMessageDTO(id=1, description=Smart Meter 123, address=Str. Independentei 102, maxHEC=120.12, userId=2, eventType=UPDATE)
```

## Frontend
The frontend is built with React and interacts with the backend via REST APIs and WebSockets.
- accessible at: http://localhost:3000
- real-time notifications: uses WebSockets to receive alerts when a device exceeds its energy threshold.
- role-based access:
    - /admin-dashboard: admin-only access for managing users and devices.
    - /home: client-only accessed dashboard, displaying assigned devices and notifications.
 
![image](https://github.com/user-attachments/assets/109a4432-49f0-441d-89fa-f476f799b841)
![image](https://github.com/user-attachments/assets/8d6ecf7b-a1e8-4a25-89bc-dffc5b79a6bc)


## Deployment with Docker & Traefik
The project uses Docker for containerized deployment, with Traefik as a reverse proxy and load balancer. 
The following containers are included:
- NGINX (Frontend): Serves the React application on port 80.
- Tomcat Servers (Backend): Each microservice runs in its own container on ports 8080 (User), 8081 (Device), 8082 (Monitoring).
- MySQL (Databases): Stores data for devices and users on port 3306.
- RabbitMQ: Manages the message queues for asynchronous communication.
- Traefik (Reverse Proxy & Load Balancer): Handles dynamic service discovery and routes traffic to backend services, mapped on 8085.
Persistent Docker volumes are used to store database data even after container restarts.
