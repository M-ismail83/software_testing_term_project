package shopeasy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Task 2 – Structural Testing &amp; Code Coverage (Chapter 3)
 *
 * <p>Target class: {@link ShoppingCart}
 *
 * <h3>Workflow</h3>
 * <ol>
 *   <li>Write an initial test suite based on the specification (Javadoc of ShoppingCart).</li>
 *   <li>Run {@code mvn test} to generate the JaCoCo report:
 *       <pre>  target/site/jacoco/index.html</pre></li>
 *   <li>Open the report, navigate to {@code ShoppingCart}, and identify uncovered branches.</li>
 *   <li>Add tests specifically to cover those branches until branch coverage &gt;= 80%.</li>
 *   <li>Take a screenshot of the final JaCoCo summary and put it in {@code report/jacoco-screenshot.png}.</li>
 * </ol>
 *
 * <h3>Branches to think about</h3>
 * <ul>
 *   <li>{@code addItem}: product already in cart vs. new product</li>
 *   <li>{@code removeItem}: product found vs. not found in cart</li>
 *   <li>{@code updateQuantity}: product found vs. not found, quantity valid vs. invalid</li>
 *   <li>{@code applyDiscount}: zero discount, positive discount</li>
 *   <li>{@code total}: empty cart vs. non-empty cart</li>
 * </ul>
 *
 * <h3>Bonus (PIT Mutation Testing)</h3>
 * Run: {@code mvn org.pitest:pitest-maven:mutationCoverage}
 * <br>Examine the HTML report in {@code target/pit-reports/}. Find two surviving mutants,
 * explain why each survived, and describe a test that would kill it. Add this analysis
 * to your reflection report.
 */
class ShoppingCartStructuralTest {

    private ShoppingCart cart;
    private Product apple;
    private Product banana;

    @BeforeEach
    void setUp() {
        cart   = new ShoppingCart();
        apple  = new Product("P001", "Apple",  1.50, 150);
        banana = new Product("P002", "Banana", 0.80, 50);
    }

    // -----------------------------------------------------------------------
    // TODO: Write your tests below.
    //
    // Start with happy-path tests, then add tests that target specific branches.
    //
    // HINT: Run `mvn test` after every few tests to see coverage progress.
    // -----------------------------------------------------------------------

    @Test
    void addingItemTest(){
        cart.addItem(apple, 50);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(50);
        cart.addItem(apple, 20);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(70);
        cart.addItem(banana, 30);
        assertThat(cart.getItems().get(1).getQuantity()).isEqualTo(30);
    }

    @Test
    void updatingQuantityTest(){
        cart.addItem(apple, 80);
        cart.updateQuantity(apple.getId(), 20);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(20);
    }
    
    @Test
    void toStringTest(){
        String cartList = cart.toString();
        assertThat(cartList).contains(String.valueOf(cart.itemCount()));
    }

    @Test
    void removingItemTest(){
        cart.addItem(banana, 20);
        cart.removeItem(banana.getId());
        assertThat(cart.getItems().contains(banana)).isFalse();
        cart.removeItem("P999");
        assertThat(cart.getItems().contains(banana)).isFalse();
    }


    @Test
    void updatingQuantityInvalidTest(){
        assertThatThrownBy(() -> cart.updateQuantity(apple.getId(), -5))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> cart.updateQuantity("P999", 10))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> cart.updateQuantity("", 10))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void discountTest(){
        double totalCost = cart.total();
        double discountedCost = cart.applyDiscount(20);
        assertThat(discountedCost).isEqualTo(totalCost * 0.8);
    }

    @Test
    void clearTest(){
        cart.clear();
        assertThat(cart.itemCount()).isEqualTo(0);
        assertThat(cart.total()).isEqualTo(0);
    }



}
