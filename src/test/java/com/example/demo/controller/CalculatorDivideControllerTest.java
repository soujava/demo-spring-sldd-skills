package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.controller.api.DivideResponse;
import com.example.demo.controller.api.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("POST /calculator/divide")
class CalculatorDivideControllerTest {

	@Autowired
	MockMvcTester mvc;

	@Test
	@DisplayName("retorna 200 com resultado 3.0 quando dividend=6.0 e divisor=2.0")
	void returnsDivideForValidPayload() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":6.0,"divisor":2.0}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(DivideResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(3.0));
	}

	@Test
	@DisplayName("retorna 200 com resultado 3.0 quando dividend=7.5 e divisor=2.5")
	void returnsDivideForDecimalPayload() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":7.5,"divisor":2.5}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(DivideResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(3.0));
	}

	@Test
	@DisplayName("retorna 200 com resultado zero quando dividend e zero")
	void returnsDivideWhenDividendIsZero() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":0.0,"divisor":5.0}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(DivideResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(0.0));
	}

	@Test
	@DisplayName("retorna 200 com resultado negativo quando dividend e negativo")
	void returnsDivideWhenResultIsNegative() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":-10.0,"divisor":2.0}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(DivideResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(-5.0));
	}

	@Test
	@DisplayName("retorna 400 quando divisor esta ausente")
	void returnsBadRequestWhenDivisorIsMissing() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":6.0}
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
	@DisplayName("retorna 400 quando dividend esta ausente")
	void returnsBadRequestWhenDividendIsMissing() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"divisor":2.0}
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
	@DisplayName("retorna 400 quando divisor e zero (divisao por zero)")
	void returnsBadRequestWhenDivisorIsZero() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":1.0,"divisor":0.0}
						"""))
			.hasStatus4xxClientError()
			.bodyJson()
			.convertTo(ErrorResponse.class)
			.satisfies(error -> {
				assertThat(error.error()).isEqualTo("Bad Request");
				assertThat(error.message()).isEqualTo("Division by zero");
			});
	}

	@Test
	@DisplayName("retorna 400 quando dividend nao e numerico")
	void returnsBadRequestWhenDividendIsNotNumeric() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":"abc","divisor":2.5}
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
	@DisplayName("retorna 400 quando JSON esta malformado")
	void returnsBadRequestWhenJsonIsMalformed() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":5.0,
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
	@DisplayName("retorna 400 quando corpo esta vazio")
	void returnsBadRequestWhenBodyIsEmpty() {
		assertThat(mvc.post().uri("/calculator/divide")
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
	@DisplayName("retorna 400 para NaN fora do contrato")
	void rejectsNaNAsUnsupported() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":NaN,"divisor":2.5}
						"""))
			.hasStatus(400);
	}

	@Test
	@DisplayName("retorna 400 para Infinity fora do contrato")
	void rejectsInfinityAsUnsupported() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":Infinity,"divisor":2.5}
						"""))
			.hasStatus(400);
	}

	@Test
	@DisplayName("retorna 400 para -Infinity fora do contrato")
	void rejectsNegativeInfinityAsUnsupported() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":-Infinity,"divisor":2.5}
						"""))
			.hasStatus(400);
	}

	@Test
	@DisplayName("retorna 200 para payload com campos extras")
	void ignoresExtraFieldsInPayload() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":6.0,"divisor":2.0,"ignored":99}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(DivideResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(3.0));
	}

	@Test
	@DisplayName("retorna 200 com 0.3333 quando divide com scale=4 e roundingMode=HALF_DOWN")
	void returnsDivideWithExplicitScaleAndRounding() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":1.0,"divisor":3.0,"scale":4,"roundingMode":"HALF_DOWN"}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(DivideResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(0.3333));
	}

	@Test
	@DisplayName("retorna 200 com 0.3333333333 quando divide sem scale (default)")
	void returnsDivideWithDefaultScale() {
		assertThat(mvc.post().uri("/calculator/divide")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"dividend":1.0,"divisor":3.0}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(DivideResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(0.3333333333));
	}
}
