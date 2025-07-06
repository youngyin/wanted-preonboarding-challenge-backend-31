### init Project

* Spring Boot 프로젝트를 `splitmart`라는 이름으로 생성
* application.yml 설정:

  ```yaml
  spring:
    application:
      name: splitmart
    datasource:
      url: jdbc:postgresql://localhost:5432/splitmart
      username: splitmart
      password: splitmart
    jpa:
      hibernate:
        ddl-auto: update
      show-sql: true
  ```
* Docker 기반 환경 구성:

    * PostgreSQL, MongoDB, Redis(Valkey), Kafka, Zookeeper 포함한 `docker-compose.yml`
    * `docker-compose up -d` 명령어로 컨테이너 실행
* SQL 파일은 수동으로 insert 처리