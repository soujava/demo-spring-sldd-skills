/// # Divide
///
/// > Calculates the arithmetic quotient of a dividend divided by a non-zero divisor.
///
/// ## Boundary
///
/// - `calculate-divide` - Calculate the arithmetic quotient of the provided dividend and divisor.
///
/// ## Requirements
///
/// ### R1 Division calculation
///
/// - R1.1 When finite dividend and non-zero divisor values are provided, the capability shall return the dividend divided by the divisor.
/// - R1.2 When decimal operands are provided, the capability shall return the arithmetic quotient.
/// - R1.3 When the dividend is zero and the divisor is non-zero, the capability shall return zero as the result.
/// - R1.4 When operands produce a negative quotient, the capability shall return a negative result.
/// - R1.5 When a calculation context is provided, the capability shall apply the requested scale and rounding mode to the quotient.
///
/// ### R2 Input contract
///
/// - R2.1 When a valid request contains unrecognized fields, the capability shall ignore those fields and calculate the quotient.
/// - R2.2 If the divisor is absent, then the capability shall reject the request with a standardized bad request response.
/// - R2.3 If the dividend is absent, then the capability shall reject the request with a standardized bad request response.
/// - R2.4 If the divisor is zero, then the capability shall reject the request with a division-by-zero bad request response.
/// - R2.5 If an operand is not numeric, then the capability shall reject the request with a standardized bad request response.
/// - R2.6 If the request body is malformed, then the capability shall reject the request with a standardized bad request response.
/// - R2.7 If the request body is empty, then the capability shall reject the request with a standardized bad request response.
/// - R2.8 If an operand uses an unsupported non-finite value, then the capability shall reject the request.
/// - R2.9 If the calculation context scale is below the supported minimum, then the capability shall reject the request with a scale validation message.
/// - R2.10 If the calculation context scale is above the supported maximum, then the capability shall reject the request with a scale validation message.
/// - R2.11 If the calculation context rounding mode is invalid, then the capability shall reject the request with a rounding mode validation message.
///
/// ## Entities
///
/// - Divide - Expression composed from a dividend and a divisor.
/// - Dividend - Finite numeric operand being divided.
/// - Divisor - Non-zero finite numeric operand dividing the dividend.
/// - CalculationContext - Optional scale and rounding mode used to calculate the quotient.
/// - QuotientResult - Numeric result produced by dividing the dividend by the divisor.
/// - ErrorResponse - Standardized rejection payload for invalid division requests.
///
/// ## Out of scope
///
/// - Persisting calculations.
package com.example.demo.capabilities.divide;
