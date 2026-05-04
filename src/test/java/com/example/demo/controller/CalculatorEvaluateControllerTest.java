package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.controller.api.EvaluateRequest;
import com.example.demo.controller.api.EvaluateResponse;
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
@DisplayName("POST /calculator/evaluate")
public class CalculatorEvaluateControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    @DisplayName("retorna 200 com resultado 15.0 para uma soma simples (10 + 5)")
    void evaluate_SimpleSum_ReturnsCorrectResult() {
        assertThat(mvc.post().uri("/calculator/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "expression": {
                        "type": "operation",
                        "operator": "SUM",
                        "left": { "type": "literal", "value": 10.0 },
                        "right": { "type": "literal", "value": 5.0 }
                      }
                    }
                    """))
                .hasStatusOk()
                .bodyJson()
                .convertTo(EvaluateResponse.class)
                .satisfies(response -> assertThat(response.result()).isEqualTo(15.0));
    }

    @Test
    @DisplayName("retorna 200 com resultado 30.0 para operacoes aninhadas (10 + 5) * 2")
    void evaluate_NestedOperations_RespectsOrder() {
        assertThat(mvc.post().uri("/calculator/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "expression": {
                        "type": "operation",
                        "operator": "MULTIPLY",
                        "left": {
                          "type": "operation",
                          "operator": "SUM",
                          "left": { "type": "literal", "value": 10.0 },
                          "right": { "type": "literal", "value": 5.0 }
                        },
                        "right": { "type": "literal", "value": 2.0 }
                      }
                    }
                    """))
                .hasStatusOk()
                .bodyJson()
                .convertTo(EvaluateResponse.class)
                .satisfies(response -> assertThat(response.result()).isEqualTo(30.0));
    }

    @Test
    @DisplayName("retorna 400 quando ocorre divisao por zero na expressao")
    void evaluate_DivisionByZero_ReturnsBadRequest() {
        assertThat(mvc.post().uri("/calculator/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "expression": {
                        "type": "operation",
                        "operator": "DIVIDE",
                        "left": { "type": "literal", "value": 10.0 },
                        "right": { "type": "literal", "value": 0.0 }
                      }
                    }
                    """))
                .hasStatus4xxClientError()
                .bodyJson()
                .convertTo(ErrorResponse.class)
                .satisfies(error -> {
                    assertThat(error.error()).isEqualTo("Bad Request");
                });
    }

    @Test
    @DisplayName("aplica context raiz em operacao DIVIDE")
    void evaluate_AppliesRootContextToDivideOperation() {
        assertThat(mvc.post().uri("/calculator/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "context": { "scale": 4, "roundingMode": "HALF_UP" },
                      "expression": {
                        "type": "operation",
                        "operator": "DIVIDE",
                        "left": { "type": "literal", "value": 1.0 },
                        "right": { "type": "literal", "value": 3.0 }
                      }
                    }
                    """))
                .hasStatusOk()
                .bodyJson()
                .convertTo(EvaluateResponse.class)
                .satisfies(response -> assertThat(response.result()).isEqualTo(0.3333));
    }

    @Test
    @DisplayName("context local parcial herda roundingMode do context raiz")
    void evaluate_LocalContextInheritsMissingFieldsFromRootContext() {
        assertThat(mvc.post().uri("/calculator/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "context": { "scale": 6, "roundingMode": "HALF_EVEN" },
                      "expression": {
                        "type": "operation",
                        "operator": "DIVIDE",
                        "left": { "type": "literal", "value": 1.0 },
                        "right": { "type": "literal", "value": 3.0 },
                        "context": { "scale": 2 }
                      }
                    }
                    """))
                .hasStatusOk()
                .bodyJson()
                .convertTo(EvaluateResponse.class)
                .satisfies(response -> assertThat(response.result()).isEqualTo(0.33));
    }

    @Test
    @DisplayName("aplica context local em operacao POWER")
    void evaluate_AppliesLocalContextToPowerOperation() {
        assertThat(mvc.post().uri("/calculator/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "expression": {
                        "type": "operation",
                        "operator": "POWER",
                        "left": { "type": "literal", "value": 2.0 },
                        "right": { "type": "literal", "value": 0.5 },
                        "context": { "scale": 4, "roundingMode": "HALF_UP" }
                      }
                    }
                    """))
                .hasStatusOk()
                .bodyJson()
                .convertTo(EvaluateResponse.class)
                .satisfies(response -> assertThat(response.result()).isEqualTo(1.4142));
    }

    @Test
    @DisplayName("context local em operacao aninhada afeta apenas a subexpressao")
    void evaluate_NestedLocalContext_AffectsOnlyNestedExpression() {
        assertThat(mvc.post().uri("/calculator/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "context": { "scale": 4, "roundingMode": "HALF_UP" },
                      "expression": {
                        "type": "operation",
                        "operator": "DIVIDE",
                        "left": {
                          "type": "operation",
                          "operator": "DIVIDE",
                          "left": { "type": "literal", "value": 1.0 },
                          "right": { "type": "literal", "value": 3.0 },
                          "context": { "scale": 2, "roundingMode": "HALF_UP" }
                        },
                        "right": { "type": "literal", "value": 3.0 }
                      }
                    }
                    """))
                .hasStatusOk()
                .bodyJson()
                .convertTo(EvaluateResponse.class)
                .satisfies(response -> assertThat(response.result()).isEqualTo(0.11));
    }
}
