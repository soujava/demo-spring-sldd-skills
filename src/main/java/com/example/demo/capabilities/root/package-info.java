/// # Root
///
/// > Calculates a numeric root for a radicand and index.
///
/// ## Boundary
///
/// - `calculate-root` - Calculate the root of the provided radicand using the provided index.
///
/// ## Requirements
///
/// ### R1 Root calculation
///
/// - R1.1 When finite radicand and non-zero index values are provided, the capability shall return the requested root.
/// - R1.2 When the index represents a cube root, the capability shall return the cube root of the radicand.
/// - R1.3 When a calculation context is provided, the capability shall apply the requested scale and rounding mode to the final result.
///
/// ### R2 Input and error contract
///
/// - R2.1 If the index is zero, then the capability shall reject the request with a bad request response.
/// - R2.2 If the radicand is absent, then the capability shall reject the request with a standardized bad request response.
/// - R2.3 If the index is absent, then the capability shall reject the request with a standardized bad request response.
/// - R2.4 If the request body is malformed, then the capability shall reject the request with a standardized bad request response.
/// - R2.5 If an operand is not numeric, then the capability shall reject the request with a standardized bad request response.
/// - R2.6 If the root result is undefined or imaginary, then the capability shall reject the request with a bad request response.
///
/// ## Entities
///
/// - Root - Expression composed from a radicand and an index.
/// - Radicand - Finite numeric operand from which the root is calculated.
/// - Index - Non-zero finite numeric operand determining which root to calculate.
/// - CalculationContext - Optional scale and rounding mode used to calculate the final result.
/// - RootResult - Numeric result produced by calculating the requested root.
/// - ErrorResponse - Standardized rejection payload for invalid root requests.
///
/// ## Out of scope
///
/// - Persisting calculations.
package com.example.demo.capabilities.root;
