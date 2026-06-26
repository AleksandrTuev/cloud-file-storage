package com.dev.cloud_file_storage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudFileStorageApplicationTests {

	@Autowired
	private WebTestClient webClient;

	@Test
	@DisplayName("ReturnStatus200")
	void shouldReturnSuccessfulHealthCheck() {

		webClient.get()
				.uri("/")
				.exchange()
				.expectStatus().is2xxSuccessful();
//				.expectBody(String.class).isEqualTo("UP");
	}

	@Test
	void contextLoads() {
	}


}
