/// # Power
///
/// > Calculates a numeric base raised to an exponent.
///
/// ## Boundary
///
/// - `calculate-power` - Calculate the result of raising the provided base to the provided exponent.
///
/// ## Requirements
///
/// ### R1 Power calculation
///
/// - R1.1 When finite base and exponent values are provided, the capability shall return the base raised to the exponent.
/// - R1.2 When a calculation context is provided, the capability shall apply the requested scale and rounding mode to the final result.
///
/// ### R2 Error contract
///
/// - R2.1 If the power result overflows the supported numeric range, then the capability shall reject the request with an unprocessable entity response.
/// - R2.2 If the power result is undefined or imaginary, then the capability shall reject the request with a bad request response.
///
/// ## Entities
///
/// - Power - Expression composed from a base and an exponent.
/// - Base - Finite numeric operand raised to a power.
/// - Exponent - Finite numeric operand that determines the power.
/// - CalculationContext - Optional scale and rounding mode used to calculate the final result.
/// - PowerResult - Numeric result produced by raising the base to the exponent.
/// - ErrorResponse - Standardized rejection payload for invalid or overflowing power requests.
///
/// ## Out of scope
///
/// - Persisting calculations.
package com.example.demo.capabilities.power;
