## Introduction

Application for managing employees of the company

## Installation

Clone the repo with https

```https
git clone https://github.com/JuliaTomchuk/employee-app.git
```
## How to build

   ```bash
   ./mvnw clean package

   ```

## How to run locally

```shell script
$ java -jar target/employee-0.0.1-SNAPSHOT.jar --spring.profiles.active=test 
```

## Run in docker

   ```bash
   docker-compose up
   ```
## Run tests

  ```bash
   ./mvnw test
   ```
## Swagger

```
http://localhost:8083/swagger-ui/index.html
```
