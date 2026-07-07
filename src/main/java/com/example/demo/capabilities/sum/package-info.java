/// # Sum
///
/// > Calculates the arithmetic sum of two finite numeric addends.
///
/// ## Boundary
///
/// - `calculate-sum` - Calculate the arithmetic sum of the provided first and second addends.
///
/// ## Requirements
///
/// ### R1 Sum calculation
///
/// - R1.1 When two finite addends are provided, the capability shall return their arithmetic sum.
/// - R1.2 When either addend is zero, the capability shall return the other addend as the result.
/// - R1.3 When one addend is negative, the capability shall include the negative value in the arithmetic sum.
/// - R1.4 When the addends are large finite doubles, the capability shall accept the request and return a successful result.
/// - R1.5 When an addend is itself an expression, the capability shall evaluate the nested expression before calculating the sum.
///
/// ### R2 Input contract
///
/// - R2.1 When a valid request contains unrecognized fields, the capability shall ignore those fields and calculate the sum.
/// - R2.2 If the first addend is absent, then the capability shall reject the request with a standardized bad request response.
/// - R2.3 If the second addend is absent, then the capability shall reject the request with a standardized bad request response.
/// - R2.4 If the request body is empty, then the capability shall reject the request with a standardized bad request response.
/// - R2.5 If the request body is malformed, then the capability shall reject the request with a standardized bad request response.
/// - R2.6 If an addend is not numeric, then the capability shall reject the request with a standardized bad request response.
/// - R2.7 If an addend uses an unsupported non-finite value, then the capability shall reject the request.
///
/// ## Entities
///
/// - Sum - Expression composed from a first addend and a second addend.
/// - Addend - Finite numeric operand accepted by the sum calculation.
/// - SumResult - Numeric result produced by adding the two addends.
/// - ErrorResponse - Standardized rejection payload for invalid sum requests.
///
/// ## Out of scope
///
/// - Persisting calculations.
/// - Choosing custom rounding or scale for sum calculations.
package com.example.demo.capabilities.sum;
