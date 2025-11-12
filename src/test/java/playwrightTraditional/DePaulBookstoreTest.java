package playwrightTraditional;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * DePaul Bookstore UI Test Suite Tests the complete purchase pathway for JBL Quantum earbuds
 * Following the 7 test cases as specified in Assignment 6
 * 
 * NOTE: The majority of this file was written and improved from the initial script. AI assisted in
 * improving comments and certain variable names but did not write core logic, organization, or
 * structure.
 */
public class DePaulBookstoreTest {

        private static Playwright playwright;
        private static Browser browser;
        private BrowserContext context;
        private Page page;

        @BeforeAll
        static void launchBrowser() {
                playwright = Playwright.create();
                browser = playwright.chromium()
                                .launch(new BrowserType.LaunchOptions().setHeadless(true));
        }

        @AfterAll
        static void closeBrowser() {
                if (browser != null) {
                        browser.close();
                }
                if (playwright != null) {
                        playwright.close();
                }
        }

        @BeforeEach
        void createContextAndPage() {
                // Create context with video recording enabled
                context = browser.newContext(new Browser.NewContextOptions()
                                .setRecordVideoDir(Paths.get("videos/"))
                                .setRecordVideoSize(1280, 720));

                page = context.newPage();
                page.navigate("https://depaul.bncollege.com/");
        }

        @AfterEach
        void closeContext() {
                if (context != null) {
                        context.close();
                }
        }

        /**
         * TestCase 1: Bookstore Search for earbuds, apply filters, select product, and add to cart
         */
        @Test
        @DisplayName("TestCase 1: Bookstore - Search, Filter, and Add to Cart")
        void testCase1_Bookstore() {
                System.out.println("TestCase 1: Bookstore");

                // Enter "earbuds" on the search box in the upper right and press the return key
                page.getByPlaceholder("Enter your search details (").fill("earbuds");
                page.getByPlaceholder("Enter your search details (").press("Enter");

                // Click on the "Brand" filter to expand it, then select "JBL"
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand"))
                                .click();
                page.locator("#facet-brand").getByRole(AriaRole.LIST).locator("label")
                                .filter(new Locator.FilterOptions().setHasText("brand JBL (12)"))
                                .getByRole(AriaRole.IMG).click();

                // Click on the "Color" filter to expand it, then select "Black"
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color"))
                                .click();
                page.locator("label")
                                .filter(new Locator.FilterOptions().setHasText("Color Black (9)"))
                                .locator("svg").first().click();

                // Click on the "Price" filter to expand it, then select "Over $50"
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price"))
                                .click();
                page.locator("#facet-price svg").nth(2).click();

                // Click on the "JBL Quantum True Wireless Noise Cancelling Gaming..." item link
                page.getByRole(AriaRole.LINK,
                                new Page.GetByRoleOptions().setName("JBL Quantum True Wireless"))
                                .click();

                // AssertThat product name, SKU number, the price, and the product description
                assertThat(page.getByLabel("main").getByRole(AriaRole.HEADING)).containsText(
                                "JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
                assertThat(page.getByLabel("main")).containsText("668972707"); // SKU number
                assertThat(page.getByLabel("main")).containsText("$164.98"); // Price
                assertThat(page.getByLabel("main")).containsText("Adaptive noise cancelling"); // Product
                                                                                               // description

                // Add 1 to the Cart
                page.getByLabel("Add to cart").click();

                // AssertThat "1 Items" in cart (upper-right of page)
                page.waitForSelector("text=Cart 1 item");
                assertThat(page.getByRole(AriaRole.LINK,
                                new Page.GetByRoleOptions().setName("Cart 1 item"))).isVisible();

                // Click "Cart" (click icon in upper right of page)
                page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 item"))
                                .click();
        }

        /**
         * TestCase 2: Your Shopping Cart Page Verify cart contents, select pickup, verify pricing,
         * test promo code
         */
        @Test
        @DisplayName("TestCase 2: Your Shopping Cart Page")
        void testCase2_ShoppingCartPage() {
                System.out.println("TestCase 2: Your Shopping Cart Page");

                // Setup: Add product to cart first
                navigateAndAddProductToCart();

                // AssertThat you are at cart: "Your Shopping Cart"
                assertThat(page.getByLabel("main")).containsText("Your Shopping Cart");

                // AssertThat the product name (JBL Quantum...), quantity (1), and price ($164.98)
                assertThat(page.getByLabel("main")).containsText(
                                "JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
                assertThat(page.getByLabel("Quantity, edit and press")).hasValue("1");
                assertThat(page.getByLabel("main")).containsText("$164.98");

                // Select "FAST In-Store Pickup"
                page.getByText("FAST In-Store PickupDePaul").click();

                // AssertThat sidebar subtotal (164.98), handling (3.00), taxes (value is "TBD"),
                // and
                // estimated total (167.98)
                assertThat(page.getByLabel("main")).containsText("$164.98"); // Subtotal
                assertThat(page.getByLabel("main")).containsText("$3.00"); // Handling
                assertThat(page.getByLabel("main")).containsText("TBD"); // Taxes
                assertThat(page.getByLabel("main")).containsText("$167.98"); // Estimated total

                // Enter promo code TEST and click APPLY (code doesn't exist so it should fail)
                page.getByLabel("Enter Promo Code").fill("TEST");
                page.getByLabel("Apply Promo Code").click();

                // AssertThat promo code reject message is displayed
                assertThat(page.locator("#js-voucher-result"))
                                .containsText("The coupon code entered is not valid.");

                // Click "PROCEED TO CHECKOUT"
                page.getByLabel("Proceed To Checkout").first().click();
        }

        /**
         * TestCase 3: Create Account Page Verify create account page and proceed as guest
         */
        @Test
        @DisplayName("TestCase 3: Create Account Page")
        void testCase3_CreateAccountPage() {
                System.out.println("TestCase 3: Create Account Page");

                // Setup: Navigate through cart to checkout
                navigateAndAddProductToCart();
                selectInStorePickupAndProceedToCheckout();

                // AssertThat "Create Account" label is present
                assertThat(page.getByLabel("main")).containsText("Create Account");

                // Select "Proceed as Guest"
                page.getByRole(AriaRole.LINK,
                                new Page.GetByRoleOptions().setName("Proceed As Guest")).click();
        }

        /**
         * TestCase 4: Contact Information Page Fill contact information and verify pricing remains
         * consistent
         */
        @Test
        @DisplayName("TestCase 4: Contact Information Page")
        void testCase4_ContactInformationPage() {
                System.out.println("TestCase 4: Contact Information Page");

                // Setup: Navigate to contact information page
                navigateAndAddProductToCart();
                selectInStorePickupAndProceedToCheckout();
                page.getByRole(AriaRole.LINK,
                                new Page.GetByRoleOptions().setName("Proceed As Guest")).click();

                // AssertThat you are at Contact Information page
                assertThat(page.getByLabel("main")).containsText("Contact Information");

                // Enter a first name, a last name, an email address, a phone number
                page.getByPlaceholder("Please enter your first name").fill("John");
                page.getByPlaceholder("Please enter your last name").fill("Doe");
                page.getByPlaceholder("Please enter a valid email").fill("john.doe@example.com");
                page.getByPlaceholder("Please enter a valid phone").fill("3125551234");

                // AssertThat sidebar subtotal (164.98), handling (3.00), taxes (value is "TBD"),
                // and
                // estimated total (167.98)
                assertThat(page.getByLabel("main")).containsText("$164.98");
                assertThat(page.getByLabel("main")).containsText("$3.00");
                assertThat(page.getByLabel("main")).containsText("TBD");
                assertThat(page.getByLabel("main")).containsText("$167.98");

                // Click CONTINUE
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"))
                                .click();
        }

        /**
         * TestCase 5: Pickup Information Verify contact info, pickup location, and order details
         */
        @Test
        @DisplayName("TestCase 5: Pickup Information")
        void testCase5_PickupInformation() {
                System.out.println("TestCase 5: Pickup Information");

                // Setup: Navigate to pickup information page
                navigateToPickupInformationPage();

                // AssertThat Contact Information: name, email, and phone are correct
                assertThat(page.getByLabel("main")).containsText("John");
                assertThat(page.getByLabel("main")).containsText("Doe");
                assertThat(page.getByLabel("main")).containsText("john.doe@example.com");
                assertThat(page.getByLabel("main")).containsText("3125551234");

                // AssertThat Pick Up location (DePaul University Loop Campus & SAIC)
                assertThat(page.locator("#bnedPickupPersonForm"))
                                .containsText("DePaul University Loop Campus & SAIC");

                // AssertThat selected Pickup Person ("I'll pick them up")
                assertThat(page.locator("#bnedPickupPersonForm")).containsText("I'll pick them up");
                assertThat(page.locator("label")
                                .filter(new Locator.FilterOptions().setHasText("I'll pick them up"))
                                .locator("span").nth(1)).isVisible();

                // AssertThat Sidebar order subtotal (164.98), handling (3.00), taxes (value is
                // "TBD"), and
                // estimated total (167.98)
                assertThat(page.getByLabel("main")).containsText("$164.98");
                assertThat(page.getByLabel("main")).containsText("$3.00");
                assertThat(page.getByLabel("main")).containsText("TBD");
                assertThat(page.getByLabel("main")).containsText("$167.98");

                // AssertThat pickup item and price are correct
                assertThat(page.getByLabel("main")).containsText(
                                "JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
                assertThat(page.getByLabel("main")).containsText("$164.98");

                // Click CONTINUE
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"))
                                .click();
        }

        /**
         * TestCase 6: Payment Information Verify final pricing with calculated tax and return to
         * cart
         */
        @Test
        @DisplayName("TestCase 6: Payment Information")
        void testCase6_PaymentInformation() {
                System.out.println("TestCase 6: Payment Information");

                // Setup: Navigate to payment information page
                navigateToPickupInformationPage();
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"))
                                .click();

                // Wait for payment page to load
                page.waitForSelector("text=Payment");

                // AssertThat Sidebar order subtotal (164.98), handling (3.00), taxes (17.22), and
                // total
                // (185.20)
                assertThat(page.getByLabel("main")).containsText("$164.98");
                assertThat(page.getByLabel("main")).containsText("$3.00");
                assertThat(page.getByLabel("main")).containsText("$17.22"); // Tax now calculated
                assertThat(page.getByLabel("main")).containsText("$185.20"); // Total with tax

                // AssertThat pickup item and price are correct
                assertThat(page.getByLabel("main")).containsText(
                                "JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
                assertThat(page.getByLabel("main")).containsText("$164.98");

                // Click "< BACK TO CART" (upper-right of page)
                page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart"))
                                .click();
        }

        /**
         * TestCase 7: Your Shopping Cart - Delete Item Delete product from cart and verify cart is
         * empty
         */
        @Test
        @DisplayName("TestCase 7: Your Shopping Cart - Delete Product")
        void testCase7_DeleteProductFromCart() {
                System.out.println("TestCase 7: Your Shopping Cart - Delete Product");

                // Setup: Add product to cart
                navigateAndAddProductToCart();

                // Delete product from cart
                page.getByLabel("Remove product JBL Quantum").click();

                // AssertThat your cart is empty
                assertThat(page.getByLabel("main").getByRole(AriaRole.HEADING))
                                .containsText("Your cart is empty");

                // Close window (handled by @AfterEach)
                System.out.println("Test completed - cart is empty");
        }

        // ========== Helper Methods ==========

        /**
         * Helper method: Navigate to product and add to cart
         */
        private void navigateAndAddProductToCart() {
                page.getByPlaceholder("Enter your search details (").fill("earbuds");
                page.getByPlaceholder("Enter your search details (").press("Enter");

                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand"))
                                .click();
                page.locator("#facet-brand").getByRole(AriaRole.LIST).locator("label")
                                .filter(new Locator.FilterOptions().setHasText("brand JBL (12)"))
                                .getByRole(AriaRole.IMG).click();

                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color"))
                                .click();
                page.locator("label")
                                .filter(new Locator.FilterOptions().setHasText("Color Black (9)"))
                                .locator("svg").first().click();

                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price"))
                                .click();
                page.locator("#facet-price svg").nth(2).click();

                page.getByRole(AriaRole.LINK,
                                new Page.GetByRoleOptions().setName("JBL Quantum True Wireless"))
                                .click();
                page.getByLabel("Add to cart").click();

                page.waitForSelector("text=Cart 1 item");
                page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 item"))
                                .click();
        }

        /**
         * Helper method: Select in-store pickup and proceed to checkout
         */
        private void selectInStorePickupAndProceedToCheckout() {
                page.getByText("FAST In-Store PickupDePaul").click();
                page.getByLabel("Proceed To Checkout").first().click();
        }

        /**
         * Helper method: Navigate to pickup information page
         */
        private void navigateToPickupInformationPage() {
                navigateAndAddProductToCart();
                selectInStorePickupAndProceedToCheckout();
                page.getByRole(AriaRole.LINK,
                                new Page.GetByRoleOptions().setName("Proceed As Guest")).click();

                page.getByPlaceholder("Please enter your first name").fill("John");
                page.getByPlaceholder("Please enter your last name").fill("Doe");
                page.getByPlaceholder("Please enter a valid email").fill("john.doe@example.com");
                page.getByPlaceholder("Please enter a valid phone").fill("3125551234");

                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"))
                                .click();
        }
}
