# Backend REST API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the Spring Boot REST backend for the Personal Library Manager, with session auth, MongoDB persistence, Open Library import, VCR/WireMock, Testcontainers, JaCoCo, SonarQube, GitHub Actions, and README evidence.

**Architecture:** The app is a backend-first REST API. MVC is applied as `Controller -> Service -> Repository`, with an isolated `OpenLibraryClient` for the external HTTP dependency. The future frontend consumes JSON and uses cookie-backed session auth.

**Tech Stack:** Java 21, Spring Boot 3.5.14, Maven, Spring Web, Spring Security, Spring Data MongoDB, Bean Validation, Testcontainers MongoDB, WireMock 3.13.2, JUnit 5, MockMvc, JaCoCo, SonarQube, GitHub Actions.

---

## Source Notes

- Spring Boot parent version `3.5.14` is selected from Maven Central's Spring Boot starter parent listing dated April 23, 2026.
- WireMock `3.13.2` is selected from the official WireMock Java quick start dependency example.
- The official project protocol requires SonarQube, not SonarCloud.

## File Structure

Create this structure:

```text
pom.xml
README.md
scripts/update-openlibrary-vcr.ps1
.github/workflows/quality.yml
.github/workflows/vcr-refresh.yml
src/main/resources/application.yml
src/test/resources/application-test.yml
src/test/resources/wiremock/openlibrary/mappings/open-library-clean-code.json
src/test/resources/wiremock/openlibrary/__files/open-library-clean-code-response.json
src/main/java/br/senac/biblioteca/BibliotecaApplication.java
src/main/java/br/senac/biblioteca/config/SecurityConfig.java
src/main/java/br/senac/biblioteca/config/OpenLibraryProperties.java
src/main/java/br/senac/biblioteca/controller/AuthController.java
src/main/java/br/senac/biblioteca/controller/BookController.java
src/main/java/br/senac/biblioteca/controller/BookImportController.java
src/main/java/br/senac/biblioteca/dto/request/RegisterRequest.java
src/main/java/br/senac/biblioteca/dto/request/LoginRequest.java
src/main/java/br/senac/biblioteca/dto/request/BookCreateRequest.java
src/main/java/br/senac/biblioteca/dto/request/BookUpdateRequest.java
src/main/java/br/senac/biblioteca/dto/response/UserResponse.java
src/main/java/br/senac/biblioteca/dto/response/AuthResponse.java
src/main/java/br/senac/biblioteca/dto/response/BookResponse.java
src/main/java/br/senac/biblioteca/dto/response/BookImportPreviewResponse.java
src/main/java/br/senac/biblioteca/dto/response/ErrorResponse.java
src/main/java/br/senac/biblioteca/exception/ApiException.java
src/main/java/br/senac/biblioteca/exception/BadRequestException.java
src/main/java/br/senac/biblioteca/exception/DuplicateEmailException.java
src/main/java/br/senac/biblioteca/exception/ForbiddenOperationException.java
src/main/java/br/senac/biblioteca/exception/GlobalExceptionHandler.java
src/main/java/br/senac/biblioteca/exception/NotFoundException.java
src/main/java/br/senac/biblioteca/model/Book.java
src/main/java/br/senac/biblioteca/model/BookStatus.java
src/main/java/br/senac/biblioteca/model/MetadataSource.java
src/main/java/br/senac/biblioteca/model/User.java
src/main/java/br/senac/biblioteca/repository/BookRepository.java
src/main/java/br/senac/biblioteca/repository/UserRepository.java
src/main/java/br/senac/biblioteca/service/AuthService.java
src/main/java/br/senac/biblioteca/service/BookMapper.java
src/main/java/br/senac/biblioteca/service/BookService.java
src/main/java/br/senac/biblioteca/service/CurrentUserService.java
src/main/java/br/senac/biblioteca/service/OpenLibraryService.java
src/main/java/br/senac/biblioteca/client/OpenLibraryClient.java
src/test/java/br/senac/biblioteca/AbstractMongoIntegrationTest.java
src/test/java/br/senac/biblioteca/repository/UserRepositoryTest.java
src/test/java/br/senac/biblioteca/repository/BookRepositoryTest.java
src/test/java/br/senac/biblioteca/controller/AuthControllerTest.java
src/test/java/br/senac/biblioteca/controller/BookControllerTest.java
src/test/java/br/senac/biblioteca/client/OpenLibraryClientTest.java
src/test/java/br/senac/biblioteca/controller/BookImportControllerTest.java
```

---

### Task 1: Maven Scaffold And Application Boot

**Files:**
- Create: `pom.xml`
- Create: `src/main/java/br/senac/biblioteca/BibliotecaApplication.java`
- Create: `src/main/java/br/senac/biblioteca/config/OpenLibraryProperties.java`
- Create: `src/main/resources/application.yml`
- Create: `src/test/resources/application-test.yml`

- [ ] **Step 1: Create `pom.xml`**

Use this content:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.14</version>
    <relativePath/>
  </parent>

  <groupId>br.senac</groupId>
  <artifactId>biblioteca-pessoal</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <name>biblioteca-pessoal</name>
  <description>Gerenciador de Biblioteca Pessoal - backend REST API</description>

  <properties>
    <java.version>21</java.version>
    <wiremock.version>3.13.2</wiremock.version>
    <jacoco.version>0.8.12</jacoco.version>
    <sonar.coverage.jacoco.xmlReportPaths>${project.build.directory}/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-test</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>junit-jupiter</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>mongodb</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.wiremock</groupId>
      <artifactId>wiremock</artifactId>
      <version>${wiremock.version}</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
      <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>${jacoco.version}</version>
        <executions>
          <execution>
            <goals>
              <goal>prepare-agent</goal>
            </goals>
          </execution>
          <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals>
              <goal>report</goal>
            </goals>
          </execution>
          <execution>
            <id>check</id>
            <phase>verify</phase>
            <goals>
              <goal>check</goal>
            </goals>
            <configuration>
              <rules>
                <rule>
                  <element>BUNDLE</element>
                  <limits>
                    <limit>
                      <counter>LINE</counter>
                      <value>COVEREDRATIO</value>
                      <minimum>0.80</minimum>
                    </limit>
                  </limits>
                </rule>
              </rules>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```

- [ ] **Step 2: Create the application entry point**

Use this content for `src/main/java/br/senac/biblioteca/BibliotecaApplication.java`:

```java
package br.senac.biblioteca;

import br.senac.biblioteca.config.OpenLibraryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OpenLibraryProperties.class)
public class BibliotecaApplication {
    public static void main(String[] args) {
        SpringApplication.run(BibliotecaApplication.class, args);
    }
}
```

- [ ] **Step 3: Create Open Library configuration properties**

Use this content for `src/main/java/br/senac/biblioteca/config/OpenLibraryProperties.java`:

```java
package br.senac.biblioteca.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "open-library")
public record OpenLibraryProperties(String baseUrl, int timeoutMillis) {}
```

- [ ] **Step 4: Create runtime configuration**

Use this content for `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: biblioteca-pessoal
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/biblioteca_pessoal}

server:
  servlet:
    session:
      cookie:
        http-only: true
        same-site: lax

open-library:
  base-url: ${OPEN_LIBRARY_BASE_URL:https://openlibrary.org}
  timeout-millis: 5000
```

Use this content for `src/test/resources/application-test.yml`:

```yaml
spring:
  data:
    mongodb:
      database: biblioteca_pessoal_test

open-library:
  base-url: http://localhost:8089
  timeout-millis: 2000
```

- [ ] **Step 5: Run the empty scaffold**

Run:

```bash
mvn -q test
```

Expected: Maven downloads dependencies and reports build success. If Docker is not running, this task still succeeds because Testcontainers is not used yet.

- [ ] **Step 6: Commit**

```bash
git add pom.xml src/main/java/br/senac/biblioteca/BibliotecaApplication.java src/main/java/br/senac/biblioteca/config/OpenLibraryProperties.java src/main/resources/application.yml src/test/resources/application-test.yml
git commit -m "chore: scaffold Spring Boot REST backend"
```

---

### Task 2: Domain Models And Mongo Repositories

**Files:**
- Create: `src/main/java/br/senac/biblioteca/model/User.java`
- Create: `src/main/java/br/senac/biblioteca/model/Book.java`
- Create: `src/main/java/br/senac/biblioteca/model/BookStatus.java`
- Create: `src/main/java/br/senac/biblioteca/model/MetadataSource.java`
- Create: `src/main/java/br/senac/biblioteca/repository/UserRepository.java`
- Create: `src/main/java/br/senac/biblioteca/repository/BookRepository.java`
- Create: `src/test/java/br/senac/biblioteca/AbstractMongoIntegrationTest.java`
- Create: `src/test/java/br/senac/biblioteca/repository/UserRepositoryTest.java`
- Create: `src/test/java/br/senac/biblioteca/repository/BookRepositoryTest.java`

- [ ] **Step 1: Write repository integration tests first**

Use this content for `src/test/java/br/senac/biblioteca/AbstractMongoIntegrationTest.java`:

```java
package br.senac.biblioteca;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractMongoIntegrationTest {
    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
    }
}
```

Use this content for `src/test/java/br/senac/biblioteca/repository/UserRepositoryTest.java`:

```java
package br.senac.biblioteca.repository;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import br.senac.biblioteca.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends AbstractMongoIntegrationTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void findsUserByEmail() {
        User user = new User(null, "Ada Lovelace", "ada@example.com", "hashed", Instant.now(), Instant.now());
        userRepository.save(user);

        assertThat(userRepository.findByEmail("ada@example.com")).isPresent();
        assertThat(userRepository.existsByEmail("ada@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("other@example.com")).isFalse();
    }
}
```

Use this content for `src/test/java/br/senac/biblioteca/repository/BookRepositoryTest.java`:

```java
package br.senac.biblioteca.repository;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import br.senac.biblioteca.model.Book;
import br.senac.biblioteca.model.BookStatus;
import br.senac.biblioteca.model.MetadataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookRepositoryTest extends AbstractMongoIntegrationTest {
    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void clean() {
        bookRepository.deleteAll();
    }

    @Test
    void keepsBooksIsolatedByUserId() {
        Book own = new Book(null, "user-1", "Clean Code", List.of("Robert C. Martin"), "9780132350884",
                "Prentice Hall", "July 2008", 431, null, BookStatus.TO_READ, null,
                "Study reference", List.of("quality"), MetadataSource.MANUAL, Instant.now(), Instant.now());
        Book other = new Book(null, "user-2", "Refactoring", List.of("Martin Fowler"), "9780201485677",
                "Addison-Wesley", "1999", 431, null, BookStatus.READING, null,
                null, List.of("design"), MetadataSource.MANUAL, Instant.now(), Instant.now());
        bookRepository.saveAll(List.of(own, other));

        assertThat(bookRepository.findByUserId("user-1")).extracting(Book::getTitle).containsExactly("Clean Code");
        assertThat(bookRepository.findByIdAndUserId(own.getId(), "user-1")).isPresent();
        assertThat(bookRepository.findByIdAndUserId(other.getId(), "user-1")).isEmpty();
    }
}
```

- [ ] **Step 2: Run tests and verify failure**

Run:

```bash
mvn -q -Dtest=UserRepositoryTest,BookRepositoryTest test
```

Expected: compilation fails because `User`, `Book`, enums, and repositories do not exist.

- [ ] **Step 3: Add domain models and repositories**

Use this content for `src/main/java/br/senac/biblioteca/model/BookStatus.java`:

```java
package br.senac.biblioteca.model;

public enum BookStatus {
    TO_READ,
    READING,
    READ
}
```

Use this content for `src/main/java/br/senac/biblioteca/model/MetadataSource.java`:

```java
package br.senac.biblioteca.model;

public enum MetadataSource {
    MANUAL,
    OPEN_LIBRARY
}
```

Use this content for `src/main/java/br/senac/biblioteca/model/User.java`:

```java
package br.senac.biblioteca.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("users")
public class User {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private String passwordHash;
    private Instant createdAt;
    private Instant updatedAt;

    public User() {
    }

    public User(String id, String name, String email, String passwordHash, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
```

Use this content for `src/main/java/br/senac/biblioteca/model/Book.java`:

```java
package br.senac.biblioteca.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("books")
public class Book {
    @Id
    private String id;
    @Indexed
    private String userId;
    private String title;
    private List<String> authors = new ArrayList<>();
    private String isbn;
    private String publisher;
    private String publishDate;
    private Integer pageCount;
    private String coverUrl;
    private BookStatus status;
    private Integer rating;
    private String notes;
    private List<String> tags = new ArrayList<>();
    private MetadataSource metadataSource;
    private Instant createdAt;
    private Instant updatedAt;

    public Book() {
    }

    public Book(String id, String userId, String title, List<String> authors, String isbn, String publisher,
                String publishDate, Integer pageCount, String coverUrl, BookStatus status, Integer rating,
                String notes, List<String> tags, MetadataSource metadataSource, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.authors = authors == null ? new ArrayList<>() : new ArrayList<>(authors);
        this.isbn = isbn;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.pageCount = pageCount;
        this.coverUrl = coverUrl;
        this.status = status;
        this.rating = rating;
        this.notes = notes;
        this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
        this.metadataSource = metadataSource;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getAuthors() { return authors; }
    public void setAuthors(List<String> authors) { this.authors = authors == null ? new ArrayList<>() : new ArrayList<>(authors); }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getPublishDate() { return publishDate; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }
    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags); }
    public MetadataSource getMetadataSource() { return metadataSource; }
    public void setMetadataSource(MetadataSource metadataSource) { this.metadataSource = metadataSource; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
```

Use this content for `src/main/java/br/senac/biblioteca/repository/UserRepository.java`:

```java
package br.senac.biblioteca.repository;

import br.senac.biblioteca.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

Use this content for `src/main/java/br/senac/biblioteca/repository/BookRepository.java`:

```java
package br.senac.biblioteca.repository;

import br.senac.biblioteca.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {
    List<Book> findByUserId(String userId);
    Optional<Book> findByIdAndUserId(String id, String userId);
}
```

- [ ] **Step 4: Run repository tests**

Run:

```bash
mvn -q -Dtest=UserRepositoryTest,BookRepositoryTest test
```

Expected: tests pass if Docker is running. If Docker is not running, Testcontainers reports a Docker environment failure; start Docker Desktop and rerun.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/br/senac/biblioteca/model src/main/java/br/senac/biblioteca/repository src/test/java/br/senac/biblioteca
git commit -m "feat: add Mongo domain and repositories"
```

---

### Task 3: Error Contract, Security Configuration, And Session Auth

**Files:**
- Create: `src/main/java/br/senac/biblioteca/dto/request/RegisterRequest.java`
- Create: `src/main/java/br/senac/biblioteca/dto/request/LoginRequest.java`
- Create: `src/main/java/br/senac/biblioteca/dto/response/UserResponse.java`
- Create: `src/main/java/br/senac/biblioteca/dto/response/AuthResponse.java`
- Create: `src/main/java/br/senac/biblioteca/dto/response/ErrorResponse.java`
- Create: `src/main/java/br/senac/biblioteca/exception/ApiException.java`
- Create: `src/main/java/br/senac/biblioteca/exception/BadRequestException.java`
- Create: `src/main/java/br/senac/biblioteca/exception/DuplicateEmailException.java`
- Create: `src/main/java/br/senac/biblioteca/exception/ForbiddenOperationException.java`
- Create: `src/main/java/br/senac/biblioteca/exception/GlobalExceptionHandler.java`
- Create: `src/main/java/br/senac/biblioteca/exception/NotFoundException.java`
- Create: `src/main/java/br/senac/biblioteca/config/SecurityConfig.java`
- Create: `src/main/java/br/senac/biblioteca/service/AuthService.java`
- Create: `src/main/java/br/senac/biblioteca/service/CurrentUserService.java`
- Create: `src/main/java/br/senac/biblioteca/controller/AuthController.java`
- Create: `src/test/java/br/senac/biblioteca/controller/AuthControllerTest.java`

- [ ] **Step 1: Write auth controller tests first**

Use this content for `src/test/java/br/senac/biblioteca/controller/AuthControllerTest.java`:

```java
package br.senac.biblioteca.controller;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import br.senac.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthControllerTest extends AbstractMongoIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void registersUserWithoutReturningPasswordHash() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ada Lovelace","email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("ada@example.com"))
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist());
    }

    @Test
    void rejectsDuplicateEmail() throws Exception {
        String body = """
                {"name":"Ada Lovelace","email":"ada@example.com","password":"StrongPass123"}
                """;
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email ja cadastrado."));
    }

    @Test
    void loginCreatesSessionAndMeReturnsCurrentUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ada Lovelace","email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isCreated());

        var login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("JSESSIONID"))
                .andReturn();

        mockMvc.perform(get("/api/auth/me").session((org.springframework.mock.web.MockHttpSession) login.getRequest().getSession(false)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada@example.com"));
    }

    @Test
    void unauthenticatedMeReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
```

- [ ] **Step 2: Run auth tests and verify failure**

Run:

```bash
mvn -q -Dtest=AuthControllerTest test
```

Expected: compilation fails because auth DTOs, controller, security config, and service do not exist.

- [ ] **Step 3: Add DTOs and errors**

Use these DTO contents:

```java
// src/main/java/br/senac/biblioteca/dto/request/RegisterRequest.java
package br.senac.biblioteca.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password
) {}
```

```java
// src/main/java/br/senac/biblioteca/dto/request/LoginRequest.java
package br.senac.biblioteca.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}
```

```java
// src/main/java/br/senac/biblioteca/dto/response/UserResponse.java
package br.senac.biblioteca.dto.response;

public record UserResponse(String id, String name, String email) {}
```

```java
// src/main/java/br/senac/biblioteca/dto/response/AuthResponse.java
package br.senac.biblioteca.dto.response;

public record AuthResponse(UserResponse user) {}
```

```java
// src/main/java/br/senac/biblioteca/dto/response/ErrorResponse.java
package br.senac.biblioteca.dto.response;

import java.time.Instant;

public record ErrorResponse(Instant timestamp, int status, String error, String message, String path) {}
```

Use these exception contents:

```java
// src/main/java/br/senac/biblioteca/exception/ApiException.java
package br.senac.biblioteca.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {
    private final HttpStatus status;

    protected ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
```

```java
// src/main/java/br/senac/biblioteca/exception/BadRequestException.java
package br.senac.biblioteca.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
```

```java
// src/main/java/br/senac/biblioteca/exception/DuplicateEmailException.java
package br.senac.biblioteca.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends ApiException {
    public DuplicateEmailException() {
        super(HttpStatus.CONFLICT, "Email ja cadastrado.");
    }
}
```

```java
// src/main/java/br/senac/biblioteca/exception/ForbiddenOperationException.java
package br.senac.biblioteca.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenOperationException extends ApiException {
    public ForbiddenOperationException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
```

```java
// src/main/java/br/senac/biblioteca/exception/NotFoundException.java
package br.senac.biblioteca.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
```

Use this global handler:

```java
package br.senac.biblioteca.exception;

import br.senac.biblioteca.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErrorResponse> handleApi(ApiException ex, HttpServletRequest request) {
        return error(ex.getStatus(), ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Dados invalidos.", request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED, "Credenciais invalidas.", request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno controlado.", request);
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        ));
    }
}
```

- [ ] **Step 4: Add security and auth service**

Use this content for `src/main/java/br/senac/biblioteca/config/SecurityConfig.java`:

```java
package br.senac.biblioteca.config;

import br.senac.biblioteca.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPasswordHash())
                        .roles("USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado."));
    }
}
```

Use this content for `src/main/java/br/senac/biblioteca/service/AuthService.java`:

```java
package br.senac.biblioteca.service;

import br.senac.biblioteca.dto.request.LoginRequest;
import br.senac.biblioteca.dto.request.RegisterRequest;
import br.senac.biblioteca.dto.response.AuthResponse;
import br.senac.biblioteca.dto.response.UserResponse;
import br.senac.biblioteca.exception.DuplicateEmailException;
import br.senac.biblioteca.exception.NotFoundException;
import br.senac.biblioteca.model.User;
import br.senac.biblioteca.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }
        Instant now = Instant.now();
        User user = new User(null, request.name(), request.email(), passwordEncoder.encode(request.password()), now, now);
        return new AuthResponse(toResponse(userRepository.save(user)));
    }

    public AuthResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        HttpSession session = servletRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado."));
        return new AuthResponse(toResponse(user));
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
```

Use this content for `src/main/java/br/senac/biblioteca/service/CurrentUserService.java`:

```java
package br.senac.biblioteca.service;

import br.senac.biblioteca.exception.NotFoundException;
import br.senac.biblioteca.model.User;
import br.senac.biblioteca.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;
    private final AuthService authService;

    public CurrentUserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public User currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotFoundException("Usuario autenticado nao encontrado.");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Usuario autenticado nao encontrado."));
    }

    public br.senac.biblioteca.dto.response.UserResponse currentUserResponse(Authentication authentication) {
        return authService.toResponse(currentUser(authentication));
    }
}
```

- [ ] **Step 5: Add auth controller**

Use this content for `src/main/java/br/senac/biblioteca/controller/AuthController.java`:

```java
package br.senac.biblioteca.controller;

import br.senac.biblioteca.dto.request.LoginRequest;
import br.senac.biblioteca.dto.request.RegisterRequest;
import br.senac.biblioteca.dto.response.AuthResponse;
import br.senac.biblioteca.dto.response.UserResponse;
import br.senac.biblioteca.service.AuthService;
import br.senac.biblioteca.service.CurrentUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final CurrentUserService currentUserService;

    public AuthController(AuthService authService, CurrentUserService currentUserService) {
        this.authService = authService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        return authService.login(request, servletRequest);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        authService.logout(request);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return currentUserService.currentUserResponse(authentication);
    }
}
```

- [ ] **Step 6: Run auth tests**

Run:

```bash
mvn -q -Dtest=AuthControllerTest test
```

Expected: tests pass.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/br/senac/biblioteca/config src/main/java/br/senac/biblioteca/controller/AuthController.java src/main/java/br/senac/biblioteca/dto src/main/java/br/senac/biblioteca/exception src/main/java/br/senac/biblioteca/service/AuthService.java src/main/java/br/senac/biblioteca/service/CurrentUserService.java src/test/java/br/senac/biblioteca/controller/AuthControllerTest.java
git commit -m "feat: add session authentication API"
```

---

### Task 4: Authenticated Book CRUD

**Files:**
- Create: `src/main/java/br/senac/biblioteca/dto/request/BookCreateRequest.java`
- Create: `src/main/java/br/senac/biblioteca/dto/request/BookUpdateRequest.java`
- Create: `src/main/java/br/senac/biblioteca/dto/response/BookResponse.java`
- Create: `src/main/java/br/senac/biblioteca/service/BookMapper.java`
- Create: `src/main/java/br/senac/biblioteca/service/BookService.java`
- Create: `src/main/java/br/senac/biblioteca/controller/BookController.java`
- Create: `src/test/java/br/senac/biblioteca/controller/BookControllerTest.java`

- [ ] **Step 1: Write book controller tests first**

Use this content for `src/test/java/br/senac/biblioteca/controller/BookControllerTest.java`:

```java
package br.senac.biblioteca.controller;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import br.senac.biblioteca.repository.BookRepository;
import br.senac.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class BookControllerTest extends AbstractMongoIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void clean() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createsListsUpdatesAndDeletesOwnBook() throws Exception {
        MockHttpSession session = login("ada@example.com");

        String created = mockMvc.perform(post("/api/books")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code","authors":["Robert C. Martin"],"isbn":"9780132350884","status":"READING","rating":5,"tags":["quality"]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = created.replaceAll(".*\\\"id\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(get("/api/books").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));

        mockMvc.perform(put("/api/books/" + id)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code Updated","authors":["Robert C. Martin"],"isbn":"9780132350884","status":"READ","rating":5,"tags":["quality","done"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code Updated"))
                .andExpect(jsonPath("$.status").value("READ"));

        mockMvc.perform(delete("/api/books/" + id).session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/" + id).session(session))
                .andExpect(status().isNotFound());
    }

    @Test
    void doesNotExposeBookFromAnotherUser() throws Exception {
        MockHttpSession owner = login("owner@example.com");
        MockHttpSession stranger = login("stranger@example.com");

        String created = mockMvc.perform(post("/api/books")
                        .session(owner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Private Book","status":"TO_READ"}
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String id = created.replaceAll(".*\\\"id\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(get("/api/books/" + id).session(stranger))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/books/" + id).session(stranger))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsBookCreationWithoutSession() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code","status":"READING"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    private MockHttpSession login(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"%s","password":"StrongPass123"}
                                """.formatted(email)))
                .andExpect(status().isCreated());

        return (MockHttpSession) mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"StrongPass123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession(false);
    }
}
```

- [ ] **Step 2: Run book tests and verify failure**

Run:

```bash
mvn -q -Dtest=BookControllerTest test
```

Expected: compilation fails because book DTOs, mapper, service, and controller do not exist.

- [ ] **Step 3: Add book DTOs**

Use these contents:

```java
// src/main/java/br/senac/biblioteca/dto/request/BookCreateRequest.java
package br.senac.biblioteca.dto.request;

import br.senac.biblioteca.model.BookStatus;
import br.senac.biblioteca.model.MetadataSource;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record BookCreateRequest(
        @NotBlank String title,
        List<String> authors,
        String isbn,
        String publisher,
        String publishDate,
        Integer pageCount,
        String coverUrl,
        BookStatus status,
        @Min(1) @Max(5) Integer rating,
        String notes,
        List<String> tags,
        MetadataSource metadataSource
) {}
```

```java
// src/main/java/br/senac/biblioteca/dto/request/BookUpdateRequest.java
package br.senac.biblioteca.dto.request;

import br.senac.biblioteca.model.BookStatus;
import br.senac.biblioteca.model.MetadataSource;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record BookUpdateRequest(
        @NotBlank String title,
        List<String> authors,
        String isbn,
        String publisher,
        String publishDate,
        Integer pageCount,
        String coverUrl,
        BookStatus status,
        @Min(1) @Max(5) Integer rating,
        String notes,
        List<String> tags,
        MetadataSource metadataSource
) {}
```

```java
// src/main/java/br/senac/biblioteca/dto/response/BookResponse.java
package br.senac.biblioteca.dto.response;

import br.senac.biblioteca.model.BookStatus;
import br.senac.biblioteca.model.MetadataSource;

import java.time.Instant;
import java.util.List;

public record BookResponse(
        String id,
        String title,
        List<String> authors,
        String isbn,
        String publisher,
        String publishDate,
        Integer pageCount,
        String coverUrl,
        BookStatus status,
        Integer rating,
        String notes,
        List<String> tags,
        MetadataSource metadataSource,
        Instant createdAt,
        Instant updatedAt
) {}
```

- [ ] **Step 4: Add mapper, service, and controller**

Use this content for `src/main/java/br/senac/biblioteca/service/BookMapper.java`:

```java
package br.senac.biblioteca.service;

import br.senac.biblioteca.dto.response.BookResponse;
import br.senac.biblioteca.model.Book;

public class BookMapper {
    private BookMapper() {
    }

    public static BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthors(),
                book.getIsbn(),
                book.getPublisher(),
                book.getPublishDate(),
                book.getPageCount(),
                book.getCoverUrl(),
                book.getStatus(),
                book.getRating(),
                book.getNotes(),
                book.getTags(),
                book.getMetadataSource(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}
```

Use this content for `src/main/java/br/senac/biblioteca/service/BookService.java`:

```java
package br.senac.biblioteca.service;

import br.senac.biblioteca.dto.request.BookCreateRequest;
import br.senac.biblioteca.dto.request.BookUpdateRequest;
import br.senac.biblioteca.dto.response.BookResponse;
import br.senac.biblioteca.exception.NotFoundException;
import br.senac.biblioteca.model.Book;
import br.senac.biblioteca.model.BookStatus;
import br.senac.biblioteca.model.MetadataSource;
import br.senac.biblioteca.model.User;
import br.senac.biblioteca.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookResponse> list(User user) {
        return bookRepository.findByUserId(user.getId()).stream().map(BookMapper::toResponse).toList();
    }

    public BookResponse create(User user, BookCreateRequest request) {
        Instant now = Instant.now();
        Book book = new Book(null, user.getId(), request.title(), request.authors(), request.isbn(), request.publisher(),
                request.publishDate(), request.pageCount(), request.coverUrl(),
                request.status() == null ? BookStatus.TO_READ : request.status(), request.rating(), request.notes(),
                request.tags(), request.metadataSource() == null ? MetadataSource.MANUAL : request.metadataSource(), now, now);
        return BookMapper.toResponse(bookRepository.save(book));
    }

    public BookResponse get(User user, String id) {
        return BookMapper.toResponse(findOwnBook(user, id));
    }

    public BookResponse update(User user, String id, BookUpdateRequest request) {
        Book book = findOwnBook(user, id);
        book.setTitle(request.title());
        book.setAuthors(request.authors());
        book.setIsbn(request.isbn());
        book.setPublisher(request.publisher());
        book.setPublishDate(request.publishDate());
        book.setPageCount(request.pageCount());
        book.setCoverUrl(request.coverUrl());
        book.setStatus(request.status() == null ? BookStatus.TO_READ : request.status());
        book.setRating(request.rating());
        book.setNotes(request.notes());
        book.setTags(request.tags());
        book.setMetadataSource(request.metadataSource() == null ? book.getMetadataSource() : request.metadataSource());
        book.setUpdatedAt(Instant.now());
        return BookMapper.toResponse(bookRepository.save(book));
    }

    public void delete(User user, String id) {
        Book book = findOwnBook(user, id);
        bookRepository.delete(book);
    }

    private Book findOwnBook(User user, String id) {
        return bookRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Livro nao encontrado."));
    }
}
```

Use this content for `src/main/java/br/senac/biblioteca/controller/BookController.java`:

```java
package br.senac.biblioteca.controller;

import br.senac.biblioteca.dto.request.BookCreateRequest;
import br.senac.biblioteca.dto.request.BookUpdateRequest;
import br.senac.biblioteca.dto.response.BookResponse;
import br.senac.biblioteca.model.User;
import br.senac.biblioteca.service.BookService;
import br.senac.biblioteca.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final CurrentUserService currentUserService;

    public BookController(BookService bookService, CurrentUserService currentUserService) {
        this.bookService = bookService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<BookResponse> list(Authentication authentication) {
        return bookService.list(currentUserService.currentUser(authentication));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(Authentication authentication, @Valid @RequestBody BookCreateRequest request) {
        User user = currentUserService.currentUser(authentication);
        return bookService.create(user, request);
    }

    @GetMapping("/{id}")
    public BookResponse get(Authentication authentication, @PathVariable String id) {
        return bookService.get(currentUserService.currentUser(authentication), id);
    }

    @PutMapping("/{id}")
    public BookResponse update(Authentication authentication, @PathVariable String id, @Valid @RequestBody BookUpdateRequest request) {
        return bookService.update(currentUserService.currentUser(authentication), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable String id) {
        bookService.delete(currentUserService.currentUser(authentication), id);
    }
}
```

- [ ] **Step 5: Run book tests**

Run:

```bash
mvn -q -Dtest=BookControllerTest test
```

Expected: tests pass.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/br/senac/biblioteca/controller/BookController.java src/main/java/br/senac/biblioteca/dto/request/BookCreateRequest.java src/main/java/br/senac/biblioteca/dto/request/BookUpdateRequest.java src/main/java/br/senac/biblioteca/dto/response/BookResponse.java src/main/java/br/senac/biblioteca/service/BookMapper.java src/main/java/br/senac/biblioteca/service/BookService.java src/test/java/br/senac/biblioteca/controller/BookControllerTest.java
git commit -m "feat: add authenticated book CRUD"
```

---

### Task 5: Open Library Client With VCR/WireMock Replay

**Files:**
- Read: `src/main/java/br/senac/biblioteca/config/OpenLibraryProperties.java`
- Create: `src/main/java/br/senac/biblioteca/client/OpenLibraryClient.java`
- Create: `src/main/java/br/senac/biblioteca/dto/response/BookImportPreviewResponse.java`
- Create: `src/main/java/br/senac/biblioteca/service/OpenLibraryService.java`
- Create: `src/main/java/br/senac/biblioteca/controller/BookImportController.java`
- Create: `src/test/resources/wiremock/openlibrary/mappings/open-library-clean-code.json`
- Create: `src/test/resources/wiremock/openlibrary/__files/open-library-clean-code-response.json`
- Create: `src/test/java/br/senac/biblioteca/client/OpenLibraryClientTest.java`
- Create: `src/test/java/br/senac/biblioteca/controller/BookImportControllerTest.java`

- [ ] **Step 1: Add WireMock declarative replay files**

Use this content for `src/test/resources/wiremock/openlibrary/mappings/open-library-clean-code.json`:

```json
{
  "request": {
    "method": "GET",
    "urlPath": "/api/books",
    "queryParameters": {
      "bibkeys": {
        "equalTo": "ISBN:9780132350884"
      },
      "format": {
        "equalTo": "json"
      },
      "jscmd": {
        "equalTo": "data"
      }
    }
  },
  "response": {
    "status": 200,
    "headers": {
      "Content-Type": "application/json"
    },
    "bodyFileName": "open-library-clean-code-response.json"
  }
}
```

Use this content for `src/test/resources/wiremock/openlibrary/__files/open-library-clean-code-response.json`:

```json
{
  "ISBN:9780132350884": {
    "url": "https://openlibrary.org/books/OL22837051M/Clean_Code",
    "title": "Clean Code",
    "authors": [
      {
        "name": "Robert C. Martin"
      }
    ],
    "number_of_pages": 431,
    "publishers": [
      {
        "name": "Prentice Hall"
      }
    ],
    "publish_date": "July 2008",
    "cover": {
      "small": "https://covers.openlibrary.org/b/id/15126503-S.jpg"
    }
  }
}
```

- [ ] **Step 2: Write client test first**

Use this content for `src/test/java/br/senac/biblioteca/client/OpenLibraryClientTest.java`:

```java
package br.senac.biblioteca.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OpenLibraryClientTest {
    static final WireMockServer WIREMOCK = new WireMockServer(
            wireMockConfig().dynamicPort().usingFilesUnderDirectory("src/test/resources/wiremock/openlibrary")
    );

    @BeforeAll
    static void startWireMock() {
        WIREMOCK.start();
    }

    @AfterAll
    static void stopWireMock() {
        WIREMOCK.stop();
    }

    @DynamicPropertySource
    static void openLibraryProperties(DynamicPropertyRegistry registry) {
        registry.add("open-library.base-url", WIREMOCK::baseUrl);
    }

    @Test
    void importsBookMetadataByIsbn() {
        OpenLibraryClient client = new OpenLibraryClient(new br.senac.biblioteca.config.OpenLibraryProperties(WIREMOCK.baseUrl(), 2000));

        var preview = client.findByIsbn("9780132350884");

        assertThat(preview.title()).isEqualTo("Clean Code");
        assertThat(preview.authors()).containsExactly("Robert C. Martin");
        assertThat(preview.publisher()).isEqualTo("Prentice Hall");
        assertThat(preview.pageCount()).isEqualTo(431);
    }
}
```

- [ ] **Step 3: Run client test and verify failure**

Run:

```bash
mvn -q -Dtest=OpenLibraryClientTest test
```

Expected: compilation fails because `OpenLibraryClient` and `BookImportPreviewResponse` do not exist.

- [ ] **Step 4: Add Open Library DTO and client**

Use this content for `src/main/java/br/senac/biblioteca/dto/response/BookImportPreviewResponse.java`:

```java
package br.senac.biblioteca.dto.response;

import java.util.List;

public record BookImportPreviewResponse(
        String title,
        List<String> authors,
        String isbn,
        String publisher,
        String publishDate,
        Integer pageCount,
        String coverUrl
) {}
```

Use this content for `src/main/java/br/senac/biblioteca/client/OpenLibraryClient.java`:

```java
package br.senac.biblioteca.client;

import br.senac.biblioteca.config.OpenLibraryProperties;
import br.senac.biblioteca.dto.response.BookImportPreviewResponse;
import br.senac.biblioteca.exception.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class OpenLibraryClient {
    private final RestClient restClient;

    public OpenLibraryClient(OpenLibraryProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.timeoutMillis());
        factory.setReadTimeout(properties.timeoutMillis());
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .build();
    }

    public BookImportPreviewResponse findByIsbn(String isbn) {
        JsonNode root = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/books")
                        .queryParam("bibkeys", "ISBN:" + isbn)
                        .queryParam("format", "json")
                        .queryParam("jscmd", "data")
                        .build())
                .retrieve()
                .body(JsonNode.class);

        JsonNode book = root == null ? null : root.get("ISBN:" + isbn);
        if (book == null || book.isMissingNode() || book.isNull()) {
            throw new NotFoundException("ISBN nao encontrado na Open Library.");
        }

        return new BookImportPreviewResponse(
                text(book, "title"),
                names(book.path("authors")),
                isbn,
                firstName(book.path("publishers")),
                text(book, "publish_date"),
                book.path("number_of_pages").isNumber() ? book.path("number_of_pages").asInt() : null,
                book.path("cover").path("small").isTextual() ? book.path("cover").path("small").asText() : null
        );
    }

    private static String text(JsonNode node, String field) {
        return node.path(field).isTextual() ? node.path(field).asText() : null;
    }

    private static String firstName(JsonNode array) {
        if (!array.isArray() || array.isEmpty()) {
            return null;
        }
        JsonNode first = array.get(0);
        return first.path("name").isTextual() ? first.path("name").asText() : null;
    }

    private static List<String> names(JsonNode array) {
        List<String> names = new ArrayList<>();
        if (array.isArray()) {
            for (JsonNode item : array) {
                if (item.path("name").isTextual()) {
                    names.add(item.path("name").asText());
                }
            }
        }
        return names;
    }
}
```

- [ ] **Step 5: Add service and controller for import preview**

Use this content for `src/main/java/br/senac/biblioteca/service/OpenLibraryService.java`:

```java
package br.senac.biblioteca.service;

import br.senac.biblioteca.client.OpenLibraryClient;
import br.senac.biblioteca.dto.response.BookImportPreviewResponse;
import br.senac.biblioteca.exception.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class OpenLibraryService {
    private final OpenLibraryClient client;

    public OpenLibraryService(OpenLibraryClient client) {
        this.client = client;
    }

    public BookImportPreviewResponse previewByIsbn(String isbn) {
        String normalized = isbn == null ? "" : isbn.replace("-", "").trim();
        if (!normalized.matches("\\d{10}|\\d{13}")) {
            throw new BadRequestException("ISBN deve ter 10 ou 13 digitos.");
        }
        return client.findByIsbn(normalized);
    }
}
```

Use this content for `src/main/java/br/senac/biblioteca/controller/BookImportController.java`:

```java
package br.senac.biblioteca.controller;

import br.senac.biblioteca.dto.response.BookImportPreviewResponse;
import br.senac.biblioteca.service.CurrentUserService;
import br.senac.biblioteca.service.OpenLibraryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books/import")
public class BookImportController {
    private final OpenLibraryService openLibraryService;
    private final CurrentUserService currentUserService;

    public BookImportController(OpenLibraryService openLibraryService, CurrentUserService currentUserService) {
        this.openLibraryService = openLibraryService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/isbn/{isbn}")
    public BookImportPreviewResponse preview(Authentication authentication, @PathVariable String isbn) {
        currentUserService.currentUser(authentication);
        return openLibraryService.previewByIsbn(isbn);
    }
}
```

- [ ] **Step 6: Write import controller test**

Use this content for `src/test/java/br/senac/biblioteca/controller/BookImportControllerTest.java`:

```java
package br.senac.biblioteca.controller;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import br.senac.biblioteca.repository.BookRepository;
import br.senac.biblioteca.repository.UserRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class BookImportControllerTest extends AbstractMongoIntegrationTest {
    static final WireMockServer WIREMOCK = new WireMockServer(
            wireMockConfig().dynamicPort().usingFilesUnderDirectory("src/test/resources/wiremock/openlibrary")
    );

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookRepository bookRepository;

    @BeforeAll
    static void startWireMock() {
        WIREMOCK.start();
    }

    @AfterAll
    static void stopWireMock() {
        WIREMOCK.stop();
    }

    @DynamicPropertySource
    static void openLibraryProperties(DynamicPropertyRegistry registry) {
        registry.add("open-library.base-url", WIREMOCK::baseUrl);
    }

    @BeforeEach
    void clean() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void previewsBookFromOpenLibraryReplay() throws Exception {
        MockHttpSession session = login("ada@example.com");

        mockMvc.perform(get("/api/books/import/isbn/9780132350884").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.authors[0]").value("Robert C. Martin"));
    }

    @Test
    void importRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/books/import/isbn/9780132350884"))
                .andExpect(status().isUnauthorized());
    }

    private MockHttpSession login(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"%s","password":"StrongPass123"}
                                """.formatted(email)))
                .andExpect(status().isCreated());

        return (MockHttpSession) mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"StrongPass123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession(false);
    }
}
```

- [ ] **Step 7: Run Open Library tests**

Run:

```bash
mvn -q -Dtest=OpenLibraryClientTest,BookImportControllerTest test
```

Expected: tests pass and no request reaches the real Open Library API.

- [ ] **Step 8: Commit**

```bash
git add src/main/java/br/senac/biblioteca/client src/main/java/br/senac/biblioteca/controller/BookImportController.java src/main/java/br/senac/biblioteca/dto/response/BookImportPreviewResponse.java src/main/java/br/senac/biblioteca/service/OpenLibraryService.java src/test/java/br/senac/biblioteca/client src/test/java/br/senac/biblioteca/controller/BookImportControllerTest.java src/test/resources/wiremock
git commit -m "feat: add Open Library VCR import"
```

---

### Task 6: Weekly VCR Refresh Script And Workflow

**Files:**
- Create: `scripts/update-openlibrary-vcr.ps1`
- Create: `.github/workflows/vcr-refresh.yml`

- [ ] **Step 1: Create the VCR refresh script**

Use this content for `scripts/update-openlibrary-vcr.ps1`:

```powershell
$ErrorActionPreference = 'Stop'

$isbn = '9780132350884'
$root = Split-Path -Parent $PSScriptRoot
$target = Join-Path $root 'src/test/resources/wiremock/openlibrary/__files/open-library-clean-code-response.json'
$url = "https://openlibrary.org/api/books?bibkeys=ISBN:$isbn&format=json&jscmd=data"

$response = Invoke-RestMethod -Uri $url -Method Get -TimeoutSec 30
$json = $response | ConvertTo-Json -Depth 20

if (-not $json.Contains('Clean Code')) {
  throw 'Resposta da Open Library nao contem o livro esperado para o ISBN configurado.'
}

Set-Content -LiteralPath $target -Value $json -Encoding UTF8
Write-Output "VCR atualizado em $target"
```

- [ ] **Step 2: Create the weekly workflow**

Use this content for `.github/workflows/vcr-refresh.yml`:

```yaml
name: refresh-open-library-vcr

on:
  schedule:
    - cron: '0 9 * * 1'
  workflow_dispatch:

permissions:
  contents: write

jobs:
  refresh:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Refresh Open Library VCR
        shell: pwsh
        run: ./scripts/update-openlibrary-vcr.ps1
      - name: Commit refreshed VCR files
        shell: bash
        run: |
          if git diff --quiet -- src/test/resources/wiremock/openlibrary/__files/open-library-clean-code-response.json; then
            echo "No VCR changes."
            exit 0
          fi
          git config user.name "github-actions[bot]"
          git config user.email "41898282+github-actions[bot]@users.noreply.github.com"
          git add src/test/resources/wiremock/openlibrary/__files/open-library-clean-code-response.json
          git commit -m "test: refresh Open Library VCR"
          git push
```

- [ ] **Step 3: Run the script locally**

Run:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/update-openlibrary-vcr.ps1
```

Expected: script prints `VCR atualizado em ...open-library-clean-code-response.json` and the JSON file remains valid.

- [ ] **Step 4: Run VCR tests after refresh**

Run:

```bash
mvn -q -Dtest=OpenLibraryClientTest,BookImportControllerTest test
```

Expected: tests pass using refreshed replay data.

- [ ] **Step 5: Commit**

```bash
git add scripts/update-openlibrary-vcr.ps1 .github/workflows/vcr-refresh.yml src/test/resources/wiremock/openlibrary/__files/open-library-clean-code-response.json
git commit -m "ci: add weekly Open Library VCR refresh"
```

---

### Task 7: Quality Pipeline With JaCoCo And SonarQube

**Files:**
- Create: `.github/workflows/quality.yml`
- Modify: `README.md`

- [ ] **Step 1: Create the quality workflow**

Use this content for `.github/workflows/quality.yml`:

```yaml
name: quality

on:
  push:
    branches: [main]
  pull_request:

jobs:
  verify:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - name: Run tests and coverage
        run: mvn -B verify
      - name: Upload JaCoCo report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-report
          path: target/site/jacoco/
      - name: SonarQube analysis
        if: github.event_name != 'pull_request' || github.event.pull_request.head.repo.full_name == github.repository
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
        run: mvn -B sonar:sonar
```

- [ ] **Step 2: Run full verification locally**

Run:

```bash
mvn -B verify
```

Expected: all tests pass, JaCoCo creates `target/site/jacoco/index.html`, and the line coverage check is at least 80%.

- [ ] **Step 3: Commit**

```bash
git add .github/workflows/quality.yml
git commit -m "ci: add quality pipeline"
```

---

### Task 8: README And Delivery Evidence

**Files:**
- Create: `README.md`
- Modify: `docs/09-checklist-entrega.md`
- Modify: `docs/10-guia-prova-oral.md`

- [ ] **Step 1: Create README**

Use this content for `README.md`:

```markdown
# Gerenciador de Biblioteca Pessoal

Backend REST API em Spring Boot para organizar livros de usuarios autenticados.

## Stack

- Java 21
- Spring Boot
- Spring Security com sessao/cookie
- MongoDB
- Testcontainers
- VCR em Java com WireMock
- JaCoCo
- SonarQube
- GitHub Actions

## Funcionalidades

- Cadastro de usuario
- Login, logout e sessao
- CRUD de livros do usuario autenticado
- Restricao de acesso por usuario
- Importacao de metadados por ISBN com Open Library

## Executar Localmente

1. Suba MongoDB local ou configure `MONGODB_URI`.
2. Execute:

```bash
mvn spring-boot:run
```

## Testes

```bash
mvn verify
```

Os testes de persistencia usam MongoDB via Testcontainers. Os testes da Open Library usam VCR em Java com WireMock em modo replay, sem chamar a internet no fluxo normal.

## Atualizar VCR Da Open Library

```powershell
powershell -ExecutionPolicy Bypass -File scripts/update-openlibrary-vcr.ps1
```

A atualizacao online do VCR roda uma vez por semana no GitHub Actions para evitar cassete eterno.

## Qualidade

- Cobertura minima: 80% com JaCoCo.
- Analise estatica: SonarQube.
- CI: GitHub Actions.

## Endpoints

| Metodo | Rota | Descricao |
| --- | --- | --- |
| POST | `/api/auth/register` | Cadastrar usuario |
| POST | `/api/auth/login` | Login com sessao |
| POST | `/api/auth/logout` | Logout |
| GET | `/api/auth/me` | Usuario autenticado |
| GET | `/api/books` | Listar livros |
| POST | `/api/books` | Criar livro |
| GET | `/api/books/{id}` | Consultar livro |
| PUT | `/api/books/{id}` | Atualizar livro |
| DELETE | `/api/books/{id}` | Excluir livro |
| GET | `/api/books/import/isbn/{isbn}` | Pre-visualizar metadados por ISBN |
```

- [ ] **Step 2: Update delivery checklist**

Add this line to `docs/09-checklist-entrega.md` under checklist before presentation:

```markdown
- Conferir README com comandos `mvn spring-boot:run`, `mvn verify`, atualizacao VCR, JaCoCo, SonarQube e GitHub Actions.
```

- [ ] **Step 3: Update oral guide**

Add this question to `docs/10-guia-prova-oral.md` under `Perguntas Provaveis`:

```markdown
| Por que comecar pelo backend REST? | Porque o nucleo avaliado esta em Spring Boot, MongoDB, sessao, testes, VCR, Testcontainers, JaCoCo, SonarQube e CI; o frontend moderno pode consumir a API depois sem alterar as regras de negocio. |
```

- [ ] **Step 4: Commit**

```bash
git add README.md docs/09-checklist-entrega.md docs/10-guia-prova-oral.md
git commit -m "docs: add backend execution and evidence guide"
```

---

### Task 9: Full Verification And Final Review

**Files:**
- Read: all project files.

- [ ] **Step 1: Run full test suite**

Run:

```bash
mvn -B verify
```

Expected: build success, all tests pass, JaCoCo report exists, and coverage check is at least 80%.

- [ ] **Step 2: Confirm no Open Library network access in normal tests**

Run:

```bash
rg -n "https://openlibrary.org" src/test src/main
```

Expected: URL appears only in configuration, refresh script, or documentation. Normal WireMock tests should use local WireMock base URL.

- [ ] **Step 3: Confirm SonarQube requirement**

Run:

```bash
rg -n "SonarCloud|SonarQube ou SonarCloud|SonarQube/SonarCloud" docs README.md .github src pom.xml
```

Expected: no result that presents SonarCloud as an accepted delivery requirement. References to SonarCloud are allowed only in source-trail docs that identify the lesson title as complementary material.

- [ ] **Step 4: Confirm VCR policy**

Run:

```bash
rg -n "Replay normal|Atualizacao semanal|cassete eterno|WireMock" docs README.md .github scripts src/test
```

Expected: output shows replay in normal tests and weekly refresh for Open Library.

- [ ] **Step 5: Inspect git status**

Run:

```bash
git status --short
```

Expected: clean working tree after all planned commits.

---

## Self-Review

- Spec coverage: backend REST, session auth, CRUD, user isolation, Open Library, VCR/WireMock, weekly refresh, Testcontainers, JaCoCo, SonarQube, GitHub Actions, README, and frontend-future contract are each mapped to tasks.
- Gap scan: no forbidden marker from the planning skill remains.
- Type consistency: package root is `br.senac.biblioteca`; DTO, model, repository, service, controller, and test names use the same identifiers across tasks.
