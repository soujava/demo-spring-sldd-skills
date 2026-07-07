package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.controller.api.ErrorResponse;
import com.example.demo.controller.api.MultiplyResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("POST /calculator/multiply")
class CalculatorMultiplyControllerTest {

	@Autowired
	MockMvcTester mvc;

	@Test
	@DisplayName("R1.1 retorna 200 com resultado 6.0 quando multiplicand=3.0 e multiplier=2.0")
	void returnsMultiplyForValidPayload() {
		assertThat(mvc.post().uri("/calculator/multiply")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"multiplicand":3.0,"multiplier":2.0}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(MultiplyResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(6.0));
	}

	@Test
	@DisplayName("R1.2 retorna 200 com resultado 3.75 quando multiplicand=1.5 e multiplier=2.5")
	void returnsMultiplyForDecimalPayload() {
		assertThat(mvc.post().uri("/calculator/multiply")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"multiplicand":1.5,"multiplier":2.5}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(MultiplyResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(3.75));
	}

	@Test
	@DisplayName("R1.3 retorna 200 com resultado zero quando multiplier e zero")
	void returnsMultiplyWhenMultiplierIsZero() {
		assertThat(mvc.post().uri("/calculator/multiply")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"multiplicand":7.4,"multiplier":0.0}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(MultiplyResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(0.0));
	}

	@Test
	@DisplayName("R1.4 retorna 200 com resultado negativo quando factores tem sinais opostos")
	void returnsMultiplyWhenResultIsNegative() {
		assertThat(mvc.post().uri("/calculator/multiply")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"multiplicand":-2.5,"multiplier":4.0}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(MultiplyResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(-10.0));
	}

	@Test
	@DisplayName("R2.2 retorna 400 quando multiplicand esta ausente")
	void returnsBadRequestWhenMultiplicandIsMissing() {
		assertThat(mvc.post().uri("/calculator/multiply")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"multiplier":2.5}
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
	@DisplayName("R2.3 retorna 400 quando multiplier esta ausente")
	void returnsBadRequestWhenMultiplierIsMissing() {
		assertThat(mvc.post().uri("/calculator/multiply")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"multiplicand":2.5}
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
	@DisplayName("R2.4 retorna 400 quando multiplicand nao e numerico")
	void returnsBadRequestWhenMultiplicandIsNotNumeric() {
		assertThat(mvc.post().uri("/calculator/multiply")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"multiplicand":"abc","multiplier":2.5}
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
		assertThat(mvc.post().uri("/calculator/multiply")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"multiplicand":5.0,
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
		assertThat(mvc.post().uri("/calculator/multiply")
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
		assertThat(mvc.post().uri("/calculator/multiply")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"multiplicand":NaN,"multiplier":2.5}
						"""))
			.hasStatus(400);
	}

	@Test
	@DisplayName("R2.7 retorna 400 para Infinity fora do contrato")
	void rejectsInfinityAsUnsupported() {
		assertThat(mvc.post().uri("/calculator/multiply")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"multiplicand":Infinity,"multiplier":2.5}
						"""))
			.hasStatus(400);
	}

	@Test
	@DisplayName("R2.7 retorna 400 para -Infinity fora do contrato")
	void rejectsNegativeInfinityAsUnsupported() {
		assertThat(mvc.post().uri("/calculator/multiply")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"multiplicand":-Infinity,"multiplier":2.5}
						"""))
			.hasStatus(400);
	}

	@Test
	@DisplayName("R2.1 retorna 200 para payload com campos extras")
	void ignoresExtraFieldsInPayload() {
		assertThat(mvc.post().uri("/calculator/multiply")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"multiplicand":3.0,"multiplier":2.0,"ignored":99}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(MultiplyResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(6.0));
	}
}
