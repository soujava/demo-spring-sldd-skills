package com.example.demo.controller.validation;

import com.example.demo.domain.expression.Expression;
import com.example.demo.domain.expression.ExpressionContext;
import com.example.demo.domain.expression.ExpressionLiteral;
import com.example.demo.domain.expression.ExpressionNode;
import com.example.demo.domain.expression.ExpressionOperation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.RoundingMode;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ComposeExpressionValidator {

	private static final Set<String> ALLOWED_FIELDS = Set.of("operation", "left", "right", "context");
	private final ObjectMapper objectMapper = new ObjectMapper();

	public Expression parse(String requestBody) {
		final JsonNode node;
		try {
			node = objectMapper.readTree(requestBody);
		} catch (JsonProcessingException ex) {
			throw new InvalidExpressionPayloadException("Invalid request body");
		}
		return parseNode(node);
	}

	private Expression parseNode(JsonNode node) {
		if (node == null || node.isNull()) {
			throw new InvalidExpressionPayloadException("Invalid request body");
		}
		if (node.isNumber()) {
			return new ExpressionLiteral(node.doubleValue());
		}
		if (!node.isObject()) {
			throw new InvalidExpressionPayloadException("Invalid request body");
		}
		validateAllowedFields(node);
		JsonNode operationNode = node.get("operation");
		JsonNode leftNode = node.get("left");
		JsonNode rightNode = node.get("right");
		if (operationNode == null || !operationNode.isTextual()) {
			throw new InvalidExpressionPayloadException("Invalid request body");
		}
		if (leftNode == null || rightNode == null) {
			throw new InvalidExpressionPayloadException("Invalid request body");
		}
		ExpressionOperation operation = parseOperation(operationNode.asText());
		Expression left = parseNode(leftNode);
		Expression right = parseNode(rightNode);
		JsonNode contextNode = node.get("context");
		ExpressionContext context = parseContext(contextNode);
		return new ExpressionNode(operation, left, right, context);
	}

	private void validateAllowedFields(JsonNode node) {
		node.fieldNames().forEachRemaining(field -> {
			if (!ALLOWED_FIELDS.contains(field)) {
				throw new InvalidExpressionPayloadException("Invalid request body");
			}
		});
	}

	private ExpressionContext parseContext(JsonNode contextNode) {
		if (contextNode == null || contextNode.isNull()) {
			return ExpressionContext.DEFAULT;
		}
		if (!contextNode.isObject()) {
			throw new InvalidExpressionPayloadException("Invalid request body");
		}
		validateContextFields(contextNode);
		JsonNode scaleNode = contextNode.get("scale");
		JsonNode roundingModeNode = contextNode.get("roundingMode");
		int scale = (scaleNode != null && !scaleNode.isNull())
			? scaleNode.asInt()
			: ExpressionContext.DEFAULT.scale();
		RoundingMode roundingMode = (roundingModeNode != null && !roundingModeNode.isNull())
			? parseRoundingMode(roundingModeNode.asText())
			: ExpressionContext.DEFAULT.roundingMode();
		return new ExpressionContext(scale, roundingMode);
	}

	private void validateContextFields(JsonNode contextNode) {
		Set<String> allowed = Set.of("scale", "roundingMode");
		contextNode.fieldNames().forEachRemaining(field -> {
			if (!allowed.contains(field)) {
				throw new InvalidExpressionPayloadException("Invalid request body");
			}
		});
	}

	private RoundingMode parseRoundingMode(String text) {
		try {
			return RoundingMode.valueOf(text);
		} catch (IllegalArgumentException ex) {
			throw new InvalidExpressionPayloadException("Invalid request body");
		}
	}

	private ExpressionOperation parseOperation(String operation) {
		try {
			return ExpressionOperation.valueOf(operation);
		} catch (IllegalArgumentException ex) {
			throw new InvalidExpressionPayloadException("Invalid request body");
		}
	}
}