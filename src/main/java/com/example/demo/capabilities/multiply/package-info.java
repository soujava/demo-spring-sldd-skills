/// # Multiply
///
/// > Calculates the arithmetic product of a multiplicand and a multiplier.
///
/// ## Boundary
///
/// - `calculate-multiply` - Calculate the arithmetic product of the provided multiplicand and multiplier.
///
/// ## Requirements
///
/// ### R1 Multiplication calculation
///
/// - R1.1 When finite multiplicand and multiplier values are provided, the capability shall return their arithmetic product.
/// - R1.2 When decimal values are provided, the capability shall return the arithmetic decimal product.
/// - R1.3 When the multiplier is zero, the capability shall return zero as the result.
/// - R1.4 When the operands have opposite signs, the capability shall return a negative result.
/// - R1.5 When decimal operands require precise decimal multiplication, the capability shall calculate using decimal arithmetic.
///
/// ### R2 Input contract
///
/// - R2.1 When a valid request contains unrecognized fields, the capability shall ignore those fields and calculate the product.
/// - R2.2 If the multiplicand is absent, then the capability shall reject the request with a standardized bad request response.
/// - R2.3 If the multiplier is absent, then the capability shall reject the request with a standardized bad request response.
/// - R2.4 If an operand is not numeric, then the capability shall reject the request with a standardized bad request response.
/// - R2.5 If the request body is malformed, then the capability shall reject the request with a standardized bad request response.
/// - R2.6 If the request body is empty, then the capability shall reject the request with a standardized bad request response.
/// - R2.7 If an operand uses an unsupported non-finite value, then the capability shall reject the request.
///
/// ## Entities
///
/// - Multiply - Expression composed from a multiplicand and a multiplier.
/// - Multiplicand - Finite numeric operand being multiplied.
/// - Multiplier - Finite numeric operand multiplying the multiplicand.
/// - ProductResult - Numeric result produced by multiplying the operands.
/// - ErrorResponse - Standardized rejection payload for invalid multiplication requests.
///
/// ## Out of scope
///
/// - Persisting calculations.
/// - Choosing custom rounding or scale for multiplication calculations.
package com.example.demo.capabilities.multiply;
