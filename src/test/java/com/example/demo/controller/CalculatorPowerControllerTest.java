package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.controller.api.ErrorResponse;
import com.example.demo.controller.api.PowerResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
class CalculatorPowerControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    @DisplayName("POST /calculator/power - Sucesso")
    void shouldReturnOkOnSuccess() {
        assertThat(mvc.post().uri("/calculator/power")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"base\":2.0,\"exponent\":3.0}"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(PowerResponse.class)
                .satisfies(response -> assertThat(response.result()).isEqualTo(8.0));
    }

    @Test
    @DisplayName("POST /calculator/power - Overflow (422)")
    void shouldReturnUnprocessableEntityOnOverflow() {
        assertThat(mvc.post().uri("/calculator/power")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"base\":10.0,\"exponent\":1000.0}"))
                .hasStatus(422)
                .bodyJson()
                .convertTo(ErrorResponse.class)
                .satisfies(error -> {
                    assertThat(error.error()).isEqualTo("Unprocessable Entity");
                    assertThat(error.message()).contains("Numeric overflow");
                });
    }

    @Test
    @DisplayName("POST /calculator/power - Operação Inválida (400)")
    void shouldReturnBadRequestOnArithmeticException() {
        assertThat(mvc.post().uri("/calculator/power")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"base\":-4.0,\"exponent\":0.5}"))
                .hasStatus4xxClientError()
                .bodyJson()
                .convertTo(ErrorResponse.class)
                .satisfies(error -> {
                    assertThat(error.error()).isEqualTo("Bad Request");
                    assertThat(error.message()).contains("Invalid operation");
                });
    }

    @Test
    @DisplayName("POST /calculator/power - arredonda resultado final com context")
    void shouldRoundFinalResultWhenContextIsProvided() {
        assertThat(mvc.post().uri("/calculator/power")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"base":2.0,"exponent":0.5,"context":{"scale":4,"roundingMode":"HALF_UP"}}
                                """))
                .hasStatusOk()
                .bodyJson()
                .convertTo(PowerResponse.class)
                .satisfies(response -> assertThat(response.result()).isEqualTo(1.4142));
    }
}
