package shopeasy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Task 1 – Specification-Based Testing (Chapter 2)
 *
 * <p>Target class: {@link PriceCalculator}
 *
 * <p>Your goal is to test {@code PriceCalculator.calculate(basePrice, discountRate, taxRate)}
 * using the domain testing technique from Chapter 2:
 * <ol>
 *   <li>Identify equivalence partitions for each input dimension.</li>
 *   <li>Identify boundary values between partitions (on-point / off-point).</li>
 *   <li>Write at least 10 meaningful test cases that cover both partitions and boundaries.</li>
 *   <li>Use {@code @ParameterizedTest} with {@code @CsvSource} for tests that share structure.</li>
 *   <li>Add a comment above each test method explaining which partition or boundary it covers.</li>
 * </ol>
 *
 * <h3>Input dimensions to consider</h3>
 * <ul>
 *   <li><b>basePrice</b>  – zero, positive, very large</li>
 *   <li><b>discountRate</b> – 0 (no discount), (0,100) typical, 100 (full discount)</li>
 *   <li><b>taxRate</b>    – 0 (no tax), (0,100) typical, 100 (100% tax)</li>
 * </ul>
 */
class PriceCalculatorSpecTest {

    private PriceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PriceCalculator();
    }

    // 1. PARTITION: Typical Valid Values
    // Covers standard equivalence classes where base > 0, 0 < discount < 100, 0 < tax < 100
    @ParameterizedTest(name = "base={0}, disc={1}%, tax={2}% => {3}")
    @CsvSource({
        "100.0, 10.0, 20.0, 108.0",
        "200.0, 25.0, 10.0, 165.0",
        "50.0,  50.0,  8.0,  27.0"
    })
    void typicalValues(double base, double disc, double tax, double expected) {
        assertThat(calculator.calculate(base, disc, tax)).isCloseTo(expected, within(0.001));
    }

    // 2. BOUNDARY: Base Price Lower Bound (On-point)
    // Partition:zero base price , result must always be 0 regardless of rates *////////////////////////////////////////////////////
    @Test
    void zeroPriceAlwaysReturnsZero() {
        assertThat(calculator.calculate(0.0, 20.0, 10.0)).isEqualTo(0.0);
    }

    // 3. BOUNDARY: Discount Rate Lower Bound (On-point)
    // Partition: 0% discount no reduction applied before tax 
    @Test
    void discountRateZeroMeansNoDiscount() {
        assertThat(calculator.calculate(100.0, 0.0, 10.0)).isEqualTo(110.0);
    }

    // 4. BOUNDARY: Discount Rate Upper Bound (On-point)
    // Partition: 100% discount, full discount wipes price to 0, tax calculation 0
    @Test
    void discountRateHundredMeansFree() {
        assertThat(calculator.calculate(100.0, 100.0, 20.0)).isEqualTo(0.0);
    }
    // 5. BOUNDARY: Tax Rate Lower Bound (On-point)
    // Partition: 0% tax — final price is only affected by the discount rate.
    @Test
    void taxRateZeroMeansNoTax() {
        assertThat(calculator.calculate(100.0, 10.0, 0.0)).isEqualTo(90.0);
    }
    // 6. BOUNDARY: Tax Rate Upper Bound (On-point)
    // Partition: 100% tax — price after discount is effectively doubled.
    @Test
    void taxRateHundredDoublesDiscountedPrice() {
        assertThat(calculator.calculate(100.0, 10.0, 100.0)).isEqualTo(180.0);
    }
    // 7. BOUNDARY: Maximum Boundary Values Combined
    // Covers exceptional inputs: extremely large base price, max discount, max tax.
    @Test
    void maximumBoundariesCombined() {
        assertThat(calculator.calculate(Double.MAX_VALUE, 100.0, 100.0)).isEqualTo(0.0);
    }

    // 8. INVALID BOUNDARY: Base Price Negative (Off-point)
    // Partition: invalid base price < 0. 
    @ParameterizedTest(name = "invalid base={0}, disc={1}%, tax={2}% => buggy result {3}")
    @CsvSource({
        "-100.0, 10.0, 10.0, -99.0", // -100 * 0.9 * 1.1
        "-50.0,  20.0,  0.0, -40.0"  // -50 * 0.8 * 1.0
    })
    void negativeBasePriceCalculatesMathematically(double invalidBase, double disc, double tax, double expected) {
        assertThat(calculator.calculate(invalidBase, disc, tax)).isCloseTo(expected, within(0.001));
    }

    // 9. INVALID BOUNDARY: Discount Rate Out of Bounds (Off-point)
    // Partition: invalid discount < 0 or > 100 (negative discount adds to price, >100 makes price negative)
    @ParameterizedTest(name = "base={0}, invalid disc={1}%, tax={2}% => buggy result {3}")
    @CsvSource({
        "100.0, -10.0, 10.0, 121.0", // 100 * 1.1 * 1.1 (negative discount acts as a penalty fee)
        "100.0, 150.0, 10.0, -55.0"  // 100 * -0.5 * 1.1 (discount > 100% results in negative price)
    })
    void invalidDiscountRatesCalculateMathematically(double base, double invalidDisc, double tax, double expected) {
        assertThat(calculator.calculate(base, invalidDisc, tax)).isCloseTo(expected, within(0.001));
    }

    // 10. INVALID BOUNDARY: Tax Rate Out of Bounds (Off-point)
    // Partition: invalid tax < 0 or > 100 (negative tax reduces price)
    @ParameterizedTest(name = "base={0}, disc={1}%, invalid tax={2}% => buggy result {3}")
    @CsvSource({
        "100.0, 10.0, -10.0, 81.0",  // 100 * 0.9 * 0.9 (negative tax acts as a second discount)
        "100.0, 10.0, 150.0, 225.0"  // 100 * 0.9 * 2.5
    })
    void invalidTaxRatesCalculateMathematically(double base, double disc, double invalidTax, double expected) {
        assertThat(calculator.calculate(base, disc, invalidTax)).isCloseTo(expected, within(0.001));
    }
}
