package shopeasy;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Task 4 – Property-Based Testing (Chapter 5)
 *
 * <p>Target classes: {@link PriceCalculator}, {@link ShoppingCart}
 *
 * <p>Using jqwik, define and test at least <strong>3 distinct properties</strong>.
 * You must use at least one custom {@code @Provide} method.
 *
 * <h3>Suggested properties (you may use these or design your own)</h3>
 * <ul>
 *   <li><b>Monotonicity</b> – For any fixed base and tax, increasing the discount
 *       rate never increases the final price.</li>
 *   <li><b>Identity</b> – A 0% discount and 0% tax returns exactly the base price.</li>
 *   <li><b>Boundedness</b> – The result is always &gt;= 0.</li>
 *   <li><b>Cart commutativity</b> – Adding product A then B yields the same total
 *       as adding B then A.</li>
 *   <li><b>Discount transitivity</b> – Applying a 10% then another 10% discount via
 *       {@code applyDiscount} is equivalent to a single call with the compounded rate
 *       (think carefully: is this actually true for this implementation?).</li>
 * </ul>
 *
 * <h3>For each property, include a comment that answers:</h3>
 * <ol>
 *   <li>What does this property mean in plain English?</li>
 *   <li>What class of bugs would this property catch?</li>
 * </ol>
 *
 * <h3>If jqwik finds a failing case</h3>
 * Do not just fix the test. Investigate the root cause and explain it in your
 * reflection report (include the counterexample jqwik printed).
 */
class ShopEasyPropertyTest {


    ////////////////////////////////////////////////////////////////////////////
    // Property 1: Identity (PriceCalculator)
    ////////////////////////////////////////////////////////////////////////////
    //If a product has a 0% discount and a 0% tax rate, the final calculated price must be exactly equal to the original base price
    @Property
    void identityProperty(
            @ForAll @DoubleRange(min = 0.0, max = 10_000.0) double base) {
        
        PriceCalculator calc = new PriceCalculator();
        double result = calc.calculate(base, 0.0, 0.0);
        
        assertThat(result).isCloseTo(base, within(0.001));
    }

    //////////////////////////////////////////////////////////////////////////////

    // Property 2: Monotonicity (PriceCalculator)
    //////////////////////////////////////////////////////////////////////////////

    //applying a higher discount rate should never result in a higher final price than a lower discount rate
    @Property
    void monotonicityProperty(
            @ForAll @DoubleRange(min = 0.0, max = 10_000.0) double base,
            @ForAll @DoubleRange(min = 0.0, max = 100.0) double tax,
            @ForAll @DoubleRange(min = 0.0, max = 100.0) double discount1,
            @ForAll @DoubleRange(min = 0.0, max = 100.0) double discount2) {

        // We sort the randomly generated discounts to guarantee lowerDiscount <= higherDiscount
        double lowerDiscount = Math.min(discount1, discount2);
        double higherDiscount = Math.max(discount1, discount2);

        PriceCalculator calc = new PriceCalculator();
        double priceWithLowerDiscount = calc.calculate(base, lowerDiscount, tax);
        double priceWithHigherDiscount = calc.calculate(base, higherDiscount, tax);

        assertThat(priceWithHigherDiscount).isLessThanOrEqualTo(priceWithLowerDiscount);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Property 3: Boundedness (PriceCalculator)
    ////////////////////////////////////////////////////////////////////////////
    //final calculated price can never fall below zero, with a positive base price and valid discount/tax rates

    @Property
    void boundednessProperty(
            @ForAll @DoubleRange(min = 0.0, max = 10_000.0) double base,
            @ForAll("validRates") double discount,
            @ForAll("validRates") double tax) {

        PriceCalculator calc = new PriceCalculator();
        double result = calc.calculate(base, discount, tax);

        assertThat(result).isGreaterThanOrEqualTo(0.0);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Custom Provider (Required by Task 4)
    ////////////////////////////////////////////////////////////////////////////
    //Generates valid percentage rates (0.0 to 100.0) for taxes and discounts
    @Provide
    Arbitrary<Double> validRates() {
        return Arbitraries.doubles().between(0.0, 100.0);
    }

}
