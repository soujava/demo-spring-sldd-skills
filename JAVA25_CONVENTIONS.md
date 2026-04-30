# Convencoes Java 25

O projeto usa Java 25. Sempre prefira as features modernas da linguagem.

---

## Sealed Interface + Records

Use `sealed interface` com `permits` para modelar hierarquias fechadas de tipos. As implementacoes devem ser `record` quando sao portadores de dados imutaveis.

```java
public sealed interface Expression permits ExpressionLiteral, ExpressionNode {}

public record ExpressionLiteral(double value) implements Expression {}
public record ExpressionNode(ExpressionOperation op, Expression left, Expression right) implements Expression {}
```

---

## Pattern Matching com instanceof

Use pattern matching em `instanceof` para extrair variaveis vinculadas, evitando casts manuais.

```java
if (expression instanceof ExpressionLiteral literal) {
    return literal.value();
}
if (expression instanceof ExpressionNode node) {
    return evaluate(node.left());
}
```

---

## Switch Expressoes

Use switch expressao (seta `->`) em vez de switch tradicional (`:`). Combina com pattern matching.

```java
return switch (node.operation()) {
    case ADD -> sum(left, right);
    case SUBTRACT -> subtract(left, right);
    case MULTIPLY -> multiply(left, right);
    case DIVIDE -> divide(left, right);
};
```

---

## var

Use `var` para inferencia de tipo local quando o tipo e obvio pelo lado direito (construtores, metodos factory). Nao use `var` quando o tipo nao e claro (literais numericos, retorno de metodo generico).

```java
var a = BigDecimal.valueOf(firstAddend);  // tipo obvio
var result = a.add(b);                    // tipo obvio
```

---

## Method Reference

Prefira method references (`Class::metodo`) sobre lambdas quando o lambda e apenas um delegador direto.

```java
// prefira
list.stream().map(String::toUpperCase)
// em vez de
list.stream().map(s -> s.toUpperCase())
```

---

## Regras gerais

- **Records** para DTOs de request/response e modelos imutaveis de dominio
- **Sealed interfaces** para hierarquias fechadas (interpretores, visitors, ASTs)
- **Pattern matching + switch** no lugar de cadeias de `if-instanceof-cast`
- **var** quando melhora legibilidade; tipo explicito quando evita ambiguidade
