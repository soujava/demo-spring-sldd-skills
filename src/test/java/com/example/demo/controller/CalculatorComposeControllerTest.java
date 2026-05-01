package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.controller.api.ComposeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("POST /calculator/compose")
class CalculatorComposeControllerTest {

	@Autowired
	private MockMvcTester mvc;

	@Test
	@DisplayName("retorna 200 com resultado 16 para 10 + (2 * 3)")
	void returnsResultForNestedMultiplyExpression() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"operation":"ADD","left":10,"right":{"operation":"MULTIPLY","left":2,"right":3}}
						"""))
			.hasStatusOk();
	}

	@Test
	@DisplayName("retorna 200 com resultado 36 para (10 + 2) * 3")
	void returnsResultForNestedAddExpression() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"operation":"MULTIPLY","left":{"operation":"ADD","left":10,"right":2},"right":3}
						"""))
			.hasStatusOk();
	}

	@Test
	@DisplayName("retorna 400 quando a raiz nao possui operation")
	void returnsBadRequestWhenRootOperationIsMissing() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"left":10,"right":3}
						"""))
			.hasStatus4xxClientError();
	}

	@Test
	@DisplayName("retorna 400 quando um no interno nao possui left ou right")
	void returnsBadRequestWhenInternalNodeIsIncomplete() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"operation":"ADD","left":{"operation":"MULTIPLY","left":2},"right":3}
						"""))
			.hasStatus4xxClientError();
	}

	@Test
	@DisplayName("retorna 400 quando operation e desconhecida")
	void returnsBadRequestWhenOperationIsUnknown() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"operation":"MODULO","left":10,"right":3}
						"""))
			.hasStatus4xxClientError();
	}

	@Test
	@DisplayName("retorna 400 quando ocorre divisao por zero")
	void returnsBadRequestWhenDivisionByZeroOccurs() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"operation":"DIVIDE","left":10,"right":0}
						"""))
			.hasStatus4xxClientError();
	}

	@Test
	@DisplayName("retorna 400 quando o JSON esta malformado")
	void returnsBadRequestWhenJsonIsMalformed() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"operation":"ADD","left":10,"right":
						"""))
			.hasStatus4xxClientError();
	}

	@Test
	@DisplayName("retorna 400 quando o payload possui campos extras")
	void returnsBadRequestWhenPayloadHasExtraFields() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"operation":"ADD","left":10,"right":3,"ignored":99}
						"""))
			.hasStatus4xxClientError();
	}

	@Test
	@DisplayName("retorna 200 com result=3.33333 para DIVIDE com context(scale=5,HALF_UP)")
	void returnsDivideWithExplicitContext() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"operation":"DIVIDE","left":10,"right":3,"context":{"scale":5,"roundingMode":"HALF_UP"}}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(ComposeResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(3.33333));
	}

	@Test
	@DisplayName("retorna 200 com result=3.3333333333 para DIVIDE sem context (defaults)")
	void returnsDivideWithoutContext() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"operation":"DIVIDE","left":10,"right":3}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(ComposeResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(3.3333333333));
	}

	@Test
	@DisplayName("retorna 400 quando context tem roundingMode invalido")
	void returnsBadRequestWhenContextHasInvalidRoundingMode() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"operation":"DIVIDE","left":10,"right":3,"context":{"scale":5,"roundingMode":"INVALID"}}
						"""))
			.hasStatus4xxClientError();
	}

	@Test
	@DisplayName("retorna 400 quando context possui campos extras")
	void returnsBadRequestWhenContextHasExtraFields() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"operation":"DIVIDE","left":10,"right":3,"context":{"scale":5,"roundingMode":"HALF_UP","extra":1}}
						"""))
			.hasStatus4xxClientError();
	}

	@Test
	@DisplayName("retorna 200 com result=5.33333 para ADD(DIVIDE(10,3 ctx=5HU), 2)")
	void returnsNestedDivideWithContext() {
		assertThat(mvc.post().uri("/calculator/compose")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"operation":"ADD","left":{"operation":"DIVIDE","left":10,"right":3,"context":{"scale":5,"roundingMode":"HALF_UP"}},"right":2}
						"""))
			.hasStatusOk()
			.bodyJson()
			.convertTo(ComposeResponse.class)
			.satisfies(response -> assertThat(response.result()).isEqualTo(5.33333));
	}
}