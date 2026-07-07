/// # Subtract
///
/// > Calculates the arithmetic difference between a minuend and a subtrahend.
///
/// ## Boundary
///
/// - `calculate-subtract` - Calculate the arithmetic difference produced by subtracting the subtrahend from the minuend.
///
/// ## Requirements
///
/// ### R1 Subtraction calculation
///
/// - R1.1 When finite minuend and subtrahend values are provided, the capability shall return the minuend minus the subtrahend.
/// - R1.2 When decimal values are provided, the capability shall return the arithmetic decimal difference.
/// - R1.3 When the subtrahend is zero, the capability shall return the minuend as the result.
/// - R1.4 When the subtrahend is greater than the minuend, the capability shall return a negative result.
///
/// ### R2 Input contract
///
/// - R2.1 When a valid request contains unrecognized fields, the capability shall ignore those fields and calculate the difference.
/// - R2.2 If the minuend is absent, then the capability shall reject the request with a standardized bad request response.
/// - R2.3 If the subtrahend is absent, then the capability shall reject the request with a standardized bad request response.
/// - R2.4 If an operand is not numeric, then the capability shall reject the request with a standardized bad request response.
/// - R2.5 If the request body is malformed, then the capability shall reject the request with a standardized bad request response.
/// - R2.6 If the request body is empty, then the capability shall reject the request with a standardized bad request response.
/// - R2.7 If an operand uses an unsupported non-finite value, then the capability shall reject the request.
///
/// ## Entities
///
/// - Subtract - Expression composed from a minuend and a subtrahend.
/// - Minuend - Finite numeric operand from which the subtrahend is removed.
/// - Subtrahend - Finite numeric operand removed from the minuend.
/// - DifferenceResult - Numeric result produced by subtracting the subtrahend from the minuend.
/// - ErrorResponse - Standardized rejection payload for invalid subtraction requests.
///
/// ## Out of scope
///
/// - Persisting calculations.
/// - Choosing custom rounding or scale for subtraction calculations.
package com.example.demo.capabilities.subtract;
