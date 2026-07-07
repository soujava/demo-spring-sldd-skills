package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.controller.api.ErrorResponse;
import com.example.demo.controller.api.SubtractResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("POST /calculator/subtract")
class CalculatorSubtractionControllerTest {

	@Autowired
	MockMvcTester mvc;

	@Test
	@DisplayName("R1.1 retorna 200 com resultado 2.0 quando minuend=5.0 e subtrahend=3.0")
	void returnsSubtractionForValidPayload() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"minuend":5.0,"subtrahend":3.0}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(SubtractResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(2.0));
	}

	@Test
	@DisplayName("R1.2 retorna 200 com resultado decimal quando minuend=7.75 e subtrahend=2.25")
	void returnsSubtractionForDecimalPayload() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"minuend":7.75,"subtrahend":2.25}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(SubtractResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(5.5));
	}

	@Test
	@DisplayName("R1.3 retorna 200 quando subtrahend e zero")
	void returnsSubtractionWhenSubtrahendIsZero() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"minuend":9.4,"subtrahend":0.0}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(SubtractResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(9.4));
	}

	@Test
	@DisplayName("R1.4 retorna 200 quando resultado da subtracao e negativo")
	void returnsSubtractionWhenResultIsNegative() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"minuend":2.0,"subtrahend":5.5}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(SubtractResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(-3.5));
	}

	@Test
	@DisplayName("R2.2 retorna 400 quando minuend esta ausente")
	void returnsBadRequestWhenMinuendIsMissing() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"subtrahend":2.5}
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
	@DisplayName("R2.3 retorna 400 quando subtrahend esta ausente")
	void returnsBadRequestWhenSubtrahendIsMissing() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"minuend":2.5}
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
	@DisplayName("R2.4 retorna 400 quando minuend nao e numerico")
	void returnsBadRequestWhenMinuendIsNotNumeric() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"minuend":"abc","subtrahend":2.5}
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
	@DisplayName("R2.5 retorna 400 quando JSON esta malformado")
	void returnsBadRequestWhenJsonIsMalformed() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"minuend":5.0,
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
	@DisplayName("R2.6 retorna 400 quando corpo esta vazio")
	void returnsBadRequestWhenBodyIsEmpty() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content(""))
			.hasStatus4xxClientError()
			.bodyJson()
			.convertTo(ErrorResponse.class)
			.satisfies(error -> {
				assertThat(error.error()).isEqualTo("Bad Request");
				assertThat(error.message()).isEqualTo("Invalid request body");
			});
	}

	@Test
	@DisplayName("R2.7 retorna 400 para NaN fora do contrato")
	void rejectsNaNAsUnsupported() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"minuend":NaN,"subtrahend":2.5}
						"""))
			.hasStatus(400);
	}

	@Test
	@DisplayName("R2.7 retorna 400 para Infinity fora do contrato")
	void rejectsInfinityAsUnsupported() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"minuend":Infinity,"subtrahend":2.5}
						"""))
			.hasStatus(400);
	}

	@Test
	@DisplayName("R2.7 retorna 400 para -Infinity fora do contrato")
	void rejectsNegativeInfinityAsUnsupported() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"minuend":-Infinity,"subtrahend":2.5}
						"""))
			.hasStatus(400);
	}

	@Test
	@DisplayName("R2.1 retorna 200 para payload com campos extras")
	void ignoresExtraFieldsInPayload() {
		assertThat(mvc.post().uri("/calculator/subtract")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"minuend":5.0,"subtrahend":3.0,"ignored":99}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(SubtractResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(2.0));
	}
}
