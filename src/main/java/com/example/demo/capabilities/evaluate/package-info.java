/// # Evaluate
///
/// > Evaluates a structured calculator expression and returns its numeric result.
///
/// ## Boundary
///
/// - `evaluate-expression` - Evaluate a literal or nested operation expression using optional calculation contexts.
///
/// ## Requirements
///
/// ### R1 Expression evaluation
///
/// - R1.1 When a simple operation expression with literal operands is provided, the capability shall evaluate the operation and return the result.
/// - R1.2 When an expression contains nested operations, the capability shall evaluate nested expressions as operands before evaluating the parent operation.
/// - R1.3 If evaluating an expression fails because a subexpression violates an operation contract, then the capability shall reject the request with a standardized bad request response.
///
/// ### R2 Calculation context
///
/// - R2.1 When a root calculation context is provided, the capability shall apply that context to operations that use scale and rounding.
/// - R2.2 When an operation has a partial local calculation context, the capability shall inherit missing context fields from the root context.
/// - R2.3 When an operation has a local calculation context, the capability shall apply that context to the operation.
/// - R2.4 When a nested operation has a local calculation context, the capability shall scope that context to the nested operation only.
///
/// ## Entities
///
/// - EvaluateRequest - Request containing the expression tree and optional root calculation context.
/// - Expression - Literal or operation node that can be evaluated to a numeric result.
/// - Literal - Numeric leaf value in an expression tree.
/// - Operation - Expression node composed from an operator and left and right operands.
/// - CalculationContext - Optional scale and rounding mode used by context-sensitive operations.
/// - EvaluateResponse - Numeric result produced by evaluating the expression.
/// - ErrorResponse - Standardized rejection payload for invalid evaluate requests.
///
/// ## Out of scope
///
/// - Persisting evaluated expressions.
package com.example.demo.capabilities.evaluate;
