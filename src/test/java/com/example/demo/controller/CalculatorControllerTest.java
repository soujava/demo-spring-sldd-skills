package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.controller.api.ErrorResponse;
import com.example.demo.controller.api.SumResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("POST /calculator/sum")
class CalculatorControllerTest {

	@Autowired
	MockMvcTester mvc;

	// --- Cenarios de sucesso ---

	@Test
	@DisplayName("retorna 200 com resultado 4.0 quando firstAddend=1.5 e secondAddend=2.5")
	void returnsSumForValidPayload() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"firstAddend":1.5,"secondAddend":2.5}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(SumResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(4.0));
	}

	@Test
	@DisplayName("retorna 200 com resultado quando os valores incluem zero")
	void returnsSumWhenOneAddendIsZero() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"firstAddend":0.0,"secondAddend":7.4}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(SumResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(7.4));
	}

	@Test
	@DisplayName("retorna 200 com resultado quando os valores incluem numero negativo")
	void returnsSumWhenOneAddendIsNegative() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"firstAddend":-2.5,"secondAddend":4.0}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(SumResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(1.5));
	}

	@Test
	@DisplayName("retorna 200 quando recebe doubles muito grandes mas finitos")
	void returnsSumForLargeFiniteDoubles() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"firstAddend":1.0E307,"secondAddend":2.0E307}
						"""))
			.hasStatusOk();
	}

	@Test
	@DisplayName("retorna 200 quando recebe campos extras alem do contrato")
	void ignoresExtraFieldsInPayload() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"firstAddend":1.5,"secondAddend":2.5,"ignored":99}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(SumResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(4.0));
	}

	// --- Cenarios de falha de validacao (campo obrigatorio ausente) ---

	@Test
	@DisplayName("retorna 400 com corpo de erro padronizado quando firstAddend esta ausente")
	void returnsBadRequestWithErrorBodyWhenFirstAddendIsMissing() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"secondAddend":2.5}
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
	@DisplayName("retorna 400 com corpo de erro padronizado quando secondAddend esta ausente")
	void returnsBadRequestWithErrorBodyWhenSecondAddendIsMissing() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"firstAddend":2.5}
						"""))
			.hasStatus4xxClientError()
			.bodyJson()
			.convertTo(ErrorResponse.class)
			.satisfies(error -> {
				assertThat(error.error()).isEqualTo("Bad Request");
				assertThat(error.message()).isEqualTo("Invalid request body");
			});
	}

	// --- Cenarios de falha de parsing ---

	@Test
	@DisplayName("retorna 400 com corpo de erro padronizado quando o corpo esta vazio")
	void returnsBadRequestWithErrorBodyWhenBodyIsEmpty() {
		assertThat(mvc.post().uri("/calculator/sum")
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
	@DisplayName("retorna 400 com corpo de erro padronizado quando o JSON esta malformado")
	void returnsBadRequestWithErrorBodyWhenJsonIsMalformed() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"firstAddend":1.5,
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
	@DisplayName("retorna 400 com corpo de erro padronizado quando firstAddend nao e numerico")
	void returnsBadRequestWithErrorBodyWhenFirstAddendIsNotNumeric() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"firstAddend":"abc","secondAddend":2.5}
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
	@DisplayName("retorna 400 com corpo de erro padronizado quando secondAddend tem tipo invalido")
	void returnsBadRequestWithErrorBodyWhenSecondAddendHasInvalidType() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"firstAddend":1.5,"secondAddend":true}
						"""))
			.hasStatus4xxClientError()
			.bodyJson()
			.convertTo(ErrorResponse.class)
			.satisfies(error -> {
				assertThat(error.error()).isEqualTo("Bad Request");
				assertThat(error.message()).isEqualTo("Invalid request body");
			});
	}

	// --- Edge cases de contrato: NaN, Infinity, -Infinity ---

	@Test
	@DisplayName("NaN e tratado como fora do contrato suportado")
	void rejectsNaNAsUnsupported() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"firstAddend":NaN,"secondAddend":2.5}
						"""))
			.hasStatus4xxClientError();
	}

	@Test
	@DisplayName("Infinity e tratado como fora do contrato suportado")
	void rejectsInfinityAsUnsupported() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"firstAddend":Infinity,"secondAddend":2.5}
						"""))
			.hasStatus4xxClientError();
	}

	@Test
	@DisplayName("-Infinity e tratado como fora do contrato suportado")
	void rejectsNegativeInfinityAsUnsupported() {
		assertThat(mvc.post().uri("/calculator/sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"firstAddend":-Infinity,"secondAddend":2.5}
						"""))
			.hasStatus4xxClientError();
	}
}
