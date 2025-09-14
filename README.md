# Mastermind Tools

A toolkit of utilities and services for integrations with cloud providers, messengers, and the Web3 ecosystem. The project is organized as a multi-module set of components (Ai, AWS, GCP, Telegram, Utilities, Web3, etc.), built with Maven and focusing on Java 11.

## Table of Contents
- About
- Modules
- Tech Stack
- Requirements
- Quick Start
- Repository Structure
- Development
- Testing & Quality
- Contributing
- License

## About
Mastermind Tools is a collection of utilities and services for:
- cloud integrations (AWS, GCP);
- Telegram automation and integrations;
- AI/ML tasks and supportive operations;
- Web3 tooling (including optional integrations);
- general-purpose utilities and reusable libraries.

The project adopts modern Java 11, Spring, and Jakarta practices with MongoDB for data storage.

## Modules
- Ai — components and services for AI/ML and data processing.
- AWS — integrations and utilities for Amazon Web Services.
- GCP — integrations and utilities for Google Cloud Platform.
- Telegram — components for working with the Telegram Bot API and related logic.
- Utilities — common helpers, extensions, and reusable utilities.
- Web3 — tools for interacting with the Web3 ecosystem (e.g., optional integrations).
- Licensing — artifacts and data related to licensing.

Note: The module list is based on the repository structure and may include both runnable services and libraries.

## Tech Stack
- Language & Platform: Java 11 (LTS)
- Build: Maven (root pom.xml)
- Web Layer: Spring MVC
- Annotations & Specs: Jakarta (jakarta.*)
- Developer Productivity: Lombok

## Requirements
- Java Development Kit (JDK) 11
- Maven 3.9+ (recommended)

## Quick Start
1) Install JDK 11 and Maven.
2) Build and run the relevant application/module(s).

## Project Structure
```
├── Ai/           # AI/ML and supporting services
├── AWS/          # AWS integrations
├── GCP/          # GCP integrations
├── Licensing/    # licensing services
├── Telegram/     # Telegram integrations
├── Utilities/    # shared utilities
├── Web3/         # Web3 tools
├── .gitignore    # gitignore file
├── pom.xml       # root Maven descriptor 
└── README.md     # this file
```

## Development
- Ensure Lombok is enabled in your IDE (IntelliJ IDEA/Eclipse).
- Code style: follow standard Java conventions and project consistency.
- Jakarta imports: use jakarta.* packages in new components.

## Testing & Quality

```bash
mvn test
```

## Contributing
- Open issues for bugs and features.
- For changes: fork/branch/PR with a clear description.
- Follow the established code style and review practices.

## License
See the Licensing directory for licensing details.