package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.controller.api.DivideResponse;
import com.example.demo.controller.api.EvaluateResponse;
import com.example.demo.controller.api.MultiplyResponse;
import com.example.demo.controller.api.PowerResponse;
import com.example.demo.controller.api.RootResponse;
import com.example.demo.controller.api.SubtractResponse;
import com.example.demo.controller.api.SumResponse;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@DisplayName("CalculatorController return types")
class CalculatorControllerReturnTypeTest {

	@Test
	@DisplayName("metodos HTTP retornam ResponseEntity tipado com o DTO de resposta")
	void httpMethodsReturnTypedResponseEntity() {
		Map<String, Class<?>> expectedResponseTypes = Map.of(
			"evaluate", EvaluateResponse.class,
			"sum", SumResponse.class,
			"subtract", SubtractResponse.class,
			"multiply", MultiplyResponse.class,
			"divide", DivideResponse.class,
			"power", PowerResponse.class,
			"root", RootResponse.class
		);

		for (var method : CalculatorController.class.getDeclaredMethods()) {
			if (!method.isAnnotationPresent(PostMapping.class)) {
				continue;
			}

			Type genericReturnType = method.getGenericReturnType();

			assertThat(genericReturnType)
				.as("%s must declare ResponseEntity with a concrete body type", method.getName())
				.isInstanceOf(ParameterizedType.class);

			var responseEntityType = (ParameterizedType) genericReturnType;
			assertThat(responseEntityType.getRawType())
				.as("%s raw return type", method.getName())
				.isEqualTo(ResponseEntity.class);
			assertThat(responseEntityType.getActualTypeArguments())
				.as("%s response body type", method.getName())
				.containsExactly(expectedResponseTypes.get(method.getName()));
		}
	}
}
