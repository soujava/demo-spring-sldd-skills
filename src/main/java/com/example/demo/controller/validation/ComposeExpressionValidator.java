package com.example.demo.controller.validation;

import com.example.demo.domain.expression.Expression;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ComposeExpressionValidator {

	private static final Set<String> ALLOWED_FIELDS = Set.of("operation", "left", "right");
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
			return Expression.literal(node.doubleValue());
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
		String operation = operationNode.asText();
		Expression left = parseNode(leftNode);
		Expression right = parseNode(rightNode);
		return switch (operation) {
			case "ADD" -> Expression.add(left, right);
			case "SUBTRACT" -> Expression.subtract(left, right);
			case "MULTIPLY" -> Expression.multiply(left, right);
			case "DIVIDE" -> Expression.divide(left, right);
			case "POWER" -> Expression.power(left, right);
			case "ROOT" -> Expression.root(left, right);
			default -> throw new InvalidExpressionPayloadException("Invalid request body");
		};
	}

	private void validateAllowedFields(JsonNode node) {
		node.fieldNames().forEachRemaining(field -> {
			if (!ALLOWED_FIELDS.contains(field)) {
				throw new InvalidExpressionPayloadException("Invalid request body");
			}
		});
	}
}