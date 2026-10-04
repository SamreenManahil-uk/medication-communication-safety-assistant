# Medication Communication Safety Assistant

A full-stack **university project delivered for a Fiverr client**, designed to improve the clarity and safety of medication instructions using AI-assisted text analysis.

The system converts medication instructions into structured information, identifies potentially ambiguous wording, calculates confidence levels, and flags results that may require further review.

## Key Features

- Medication instruction analysis and structured information extraction
- Extracts medication name, strength, dosage, frequency and route
- Displays common medication use for recognised medicines
- Hybrid **rule-based + NLP** analysis
- Confidence scoring and ambiguity detection
- OCR processing for medication-label images using **Tesseract and OpenCV**
- Secure user registration and login
- **JWT authentication and role-based access control**
- PostgreSQL storage for medication analyses
- Responsive medical-focused frontend
- REST API integration between frontend, Java backend and Python AI service

## Technology Stack

**Frontend:** React, TypeScript, Vite, CSS  
**Backend:** Java 17, Spring Boot, Spring Security, Spring Data JPA, Hibernate  
**AI Service:** Python, FastAPI, NLP, Hugging Face Transformers  
**OCR:** OpenCV, Tesseract, Pillow  
**Database:** PostgreSQL  
**Security:** JWT, BCrypt, RBAC  
**Testing:** JUnit, Spring Boot Test, Pytest

## Key Milestones

I developed the project from the backend foundation through to full-stack integration.

- Built a layered **Spring Boot REST API** with PostgreSQL persistence.
- Implemented secure **JWT authentication, BCrypt password hashing and USER/ADMIN authorization**.
- Developed an independent **FastAPI AI microservice** for medication analysis.
- Implemented **NLP, OCR, confidence scoring and ambiguity detection**.
- Connected the React frontend, Java backend, AI service and PostgreSQL database.
- Added validation, structured exception handling and secure environment-based configuration.
- Achieved **23 passing backend automated tests with 0 failures and 0 errors**.

## Project Outcome

The completed system demonstrates practical experience with **full-stack development, enterprise Java, REST APIs, microservices, AI/NLP integration, OCR, PostgreSQL, application security and automated testing**.

The project was developed to meet a real client's university project requirements while following a structured, production-style software engineering approach.

## Disclaimer

This application is a medication communication support tool and **does not provide medical diagnosis, prescribe medication, change medication doses, or replace professional medical advice**.

## Developer

**Samreen Manahil**
