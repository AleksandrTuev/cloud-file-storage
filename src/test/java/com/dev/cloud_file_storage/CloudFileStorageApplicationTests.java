package com.dev.cloud_file_storage;

import com.dev.cloud_file_storage.dto.PersonDto;
import com.dev.cloud_file_storage.dto.PersonRegistrationDto;
import com.dev.cloud_file_storage.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class CloudFileStorageApplicationTests {

	@Autowired
	private WebTestClient webClient;
	@Autowired
	private AuthService authService;

	@Test
	@DisplayName("Returns 200 when going to the main page")
	void shouldReturn200WhenGoingToTheMainPage() {

		webClient.get()
				.uri("/")
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("Returns 200 when signing in with valid credentials")
	@Sql(scripts = "/sql/cleanup-test-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void shouldReturn200WhenSigningInWithValidCredentials() {
		PersonRegistrationDto person = new PersonRegistrationDto("Vladimir", "password", "password");
		authService.register(person);

		webClient.post()
				.uri("/api/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(person)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.username").isEqualTo("Vladimir");
	}

	@Test
	@DisplayName("Returns 400 when signing in with invalid credentials")
	@Sql(scripts = "/sql/create-test-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = "/sql/cleanup-test-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void shouldReturn400WhenSigningInWithInvalidCredentials() {
		PersonDto person = new PersonDto("Vl", "p1");

		webClient.post()
				.uri("/api/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(person)
				.exchange()
				.expectStatus().is4xxClientError();
	}

	@Test
	@DisplayName("Returns 401 when signing in with invalid credentials")
	@Sql(scripts = "/sql/cleanup-test-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void shouldReturn401WhenSigningInWithInvalidCredentials() {
		PersonRegistrationDto person = new PersonRegistrationDto("Vladimir", "password", "password");

		webClient.post()
				.uri("/api/auth/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(person)
				.exchange()
				.expectStatus().isUnauthorized();
	}

	@Test
	@DisplayName("Returns 500 when invalid address")
	void shouldReturn500WhenInvalidAddress() {
		webClient.post()
				.uri("/auth")
				.exchange()
				.expectStatus().is5xxServerError();
	}

	@Test
	@DisplayName("Returns 201 when signing up with valid credentials")
	@Sql(scripts = "/sql/cleanup-test-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void shouldReturn201WhenSigningUpWithValidCredentials() {
		PersonRegistrationDto person = new PersonRegistrationDto("Vladimir", "password", "password");

		webClient.post()
				.uri("/api/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(person)
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
				.jsonPath("$.username").isEqualTo("Vladimir");
	}

	@Test
	@DisplayName("Returns 409 when user already exists")
	@Sql(scripts = "/sql/create-test-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = "/sql/cleanup-test-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void shouldReturn409WhenUserAlreadyExists() {
		PersonRegistrationDto person = new PersonRegistrationDto("Vladimir", "password", "password");

		webClient.post()
				.uri("/api/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(person)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectBody().jsonPath("$.message").isEqualTo("User already exists");
	}

	@Test
	@DisplayName("Returns 400 when signing up with invalid credentials")
	void shouldReturn400WhenSigningUpWithInvalidCredentials() {
		PersonRegistrationDto person = new PersonRegistrationDto("Vla", "1", "1");

		webClient.post()
				.uri("/api/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(person)
				.exchange()
				.expectStatus().is4xxClientError();
	}
}
