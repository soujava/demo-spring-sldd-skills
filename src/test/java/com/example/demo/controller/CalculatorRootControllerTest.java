package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.controller.api.ErrorResponse;
import com.example.demo.controller.api.RootResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("POST /calculator/root")
class CalculatorRootControllerTest {

	@Autowired
	private MockMvcTester mvc;

	@Test
	@DisplayName("retorna 200 com resultado 3.0 quando radicand=9.0 e index=2.0")
	void shouldReturnSquareRootForValidPayload() {
		assertThat(mvc.post().uri("/calculator/root")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"radicand":9.0,"index":2.0}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(RootResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(3.0));
	}

	@Test
	@DisplayName("retorna 200 com resultado 3.0 quando radicand=27.0 e index=3.0")
	void shouldReturnCubeRootForValidPayload() {
		assertThat(mvc.post().uri("/calculator/root")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"radicand":27.0,"index":3.0}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(RootResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(3.0));
	}

	@Test
	@DisplayName("retorna 400 quando index e zero")
	void shouldReturnBadRequestWhenIndexIsZero() {
		assertThat(mvc.post().uri("/calculator/root")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"radicand":9.0,"index":0.0}
						"""))
			.hasStatus4xxClientError()
			.bodyJson()
			.convertTo(ErrorResponse.class)
			.satisfies(error -> {
				assertThat(error.error()).isEqualTo("Bad Request");
				assertThat(error.message()).contains("Invalid operation");
			});
	}

	@Test
	@DisplayName("retorna 400 com corpo de erro padronizado quando radicand esta ausente")
	void shouldReturnBadRequestWhenRadicandIsMissing() {
		assertThat(mvc.post().uri("/calculator/root")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"index":2.0}
						"""))
			.hasStatus4xxClientError()
			.bodyJson()
			.convertTo(ErrorResponse.class)
			.satisfies(error -> {
				assertThat(error.error()).isEqualTo("Bad Request");
				assertThat(error.message()).isEqualTo("Invalid request body");
			});
	}

	@Test
	@DisplayName("retorna 400 com corpo de erro padronizado quando index esta ausente")
	void shouldReturnBadRequestWhenIndexIsMissing() {
		assertThat(mvc.post().uri("/calculator/root")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"radicand":9.0}
						"""))
			.hasStatus4xxClientError()
			.bodyJson()
			.convertTo(ErrorResponse.class)
			.satisfies(error -> {
				assertThat(error.error()).isEqualTo("Bad Request");
				assertThat(error.message()).isEqualTo("Invalid request body");
			});
	}

	@Test
	@DisplayName("retorna 400 com corpo de erro padronizado quando JSON esta malformado")
	void shouldReturnBadRequestWhenJsonIsMalformed() {
		assertThat(mvc.post().uri("/calculator/root")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"radicand":9.0,
						"""))
			.hasStatus4xxClientError()
			.bodyJson()
			.convertTo(ErrorResponse.class)
			.satisfies(error -> {
				assertThat(error.error()).isEqualTo("Bad Request");
				assertThat(error.message()).isEqualTo("Invalid request body");
			});
	}

	@Test
	@DisplayName("retorna 400 com corpo de erro padronizado quando radicand tem tipo invalido")
	void shouldReturnBadRequestWhenRadicandHasInvalidType() {
		assertThat(mvc.post().uri("/calculator/root")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"radicand":"abc","index":2.0}
						"""))
			.hasStatus4xxClientError()
			.bodyJson()
			.convertTo(ErrorResponse.class)
			.satisfies(error -> {
				assertThat(error.error()).isEqualTo("Bad Request");
				assertThat(error.message()).isEqualTo("Invalid request body");
			});
	}
}
