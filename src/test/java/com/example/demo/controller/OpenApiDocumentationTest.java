package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("OpenAPI documentation")
class OpenApiDocumentationTest {

	@Autowired
	private MockMvcTester mvc;

	@Test
	@DisplayName("retorna documento OpenAPI JSON em /v3/api-docs")
	void returnsOpenApiJsonDocument() {
		assertThat(mvc.get().uri("/v3/api-docs"))
			.hasStatusOk()
			.hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
			.bodyText()
			.contains("\"openapi\"");
	}

	@Test
	@DisplayName("documenta todos os endpoints da calculadora")
	void documentsCalculatorPaths() throws Exception {
		MvcTestResult result = mvc.get().uri("/v3/api-docs").exchange();

		assertThat(result).hasStatusOk();
		assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
			.contains("\"/calculator/sum\"")
			.contains("\"/calculator/subtract\"")
			.contains("\"/calculator/multiply\"")
			.contains("\"/calculator/divide\"")
			.contains("\"/calculator/power\"")
			.contains("\"/calculator/root\"");
	}

	@Test
	@DisplayName("disponibiliza a Swagger UI")
	void servesSwaggerUi() {
		MvcTestResult result = mvc.get().uri("/swagger-ui.html").exchange();

		assertThat(result.getResponse().getStatus()).isBetween(200, 399);
	}

	@Test
	@DisplayName("documenta context e enum roundingMode")
	void documentsCalculationContextAndRoundingModeEnum() throws Exception {
		MvcTestResult result = mvc.get().uri("/v3/api-docs").exchange();

		assertThat(result).hasStatusOk();
		assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
			.contains("\"context\"")
			.contains("\"roundingMode\"")
			.contains("\"HALF_UP\"")
			.contains("\"HALF_EVEN\"")
			.contains("\"UNNECESSARY\"");
	}
}
