package playwrightLLM;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * AI-Generated DePaul Bookstore UI Test Suite
 * 
 * This test class was generated using Playwright MCP (Model Context Protocol) and AI assistance. It
 * tests the complete purchase pathway for JBL Quantum earbuds following the 7 test cases as
 * specified in Assignment 6.
 * 
 * Generated through natural language prompts and AI-powered test generation.
 */
public class DePaulBookstoreTestAI {

    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
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
        // Create context with video recording enabled for AI-generated test runs
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos-ai/")).setRecordVideoSize(1280, 720));

        page = context.newPage();

        // Clear cache to ensure consistent test behavior
        context.clearCookies();
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    /**
     * TestCase 1: Bookstore - Search and Filter for JBL Quantum Earbuds
     * 
     * This test navigates to the DePaul bookstore, searches for earbuds, applies filters (Brand:
     * JBL, Color: Black, Price: Over $50), and verifies the product details.
     */
    @Test
    @DisplayName("TestCase 1: Search for JBL Quantum Earbuds with Filters")
    void testBookstoreSearchAndFilter() {
        // Navigate to DePaul bookstore
        page.navigate("https://depaul.bncollege.com/");

        // Search for "earbuds" - using placeholder selector
        page.getByPlaceholder("Enter your search details (").fill("earbuds");
        page.getByPlaceholder("Enter your search details (").press("Enter");

        // Wait for search results to load
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Apply Brand filter - JBL
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
        page.locator("#facet-brand").getByRole(AriaRole.LIST).locator("label")
                .filter(new Locator.FilterOptions().setHasText("brand JBL")).getByRole(AriaRole.IMG)
                .click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Apply Color filter - Black
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
        page.locator("label").filter(new Locator.FilterOptions().setHasText("Color Black"))
                .locator("svg").first().click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Apply Price filter - Over $50
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
        page.locator("#facet-price svg").nth(2).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Click on JBL Quantum True Wireless Noise Cancelling Gaming Earbuds
        Locator productLink = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("JBL Quantum True Wireless"));
        productLink.click();

        // Wait for product page to load
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Assert product details using main content area for specificity
        assertThat(page.getByLabel("main").getByRole(AriaRole.HEADING))
                .containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");

        // Assert SKU is present
        assertThat(page.getByLabel("main")).containsText("668972707");

        // Assert price is displayed
        assertThat(page.getByLabel("main")).containsText("$164.98");

        // Assert description is present
        assertThat(page.getByLabel("main")).containsText("Adaptive noise cancelling");

        // Add to cart
        page.getByLabel("Add to cart").click();

        // Assert cart shows 1 item
        page.waitForSelector("text=Cart 1 item");
        assertThat(
                page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 item")))
                        .isVisible();

        // Navigate to cart
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 item")).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * TestCase 2: Your Shopping Cart Page
     * 
     * Verifies cart contents, applies shipping option, tests promo code, and proceeds to checkout.
     */
    @Test
    @DisplayName("TestCase 2: Verify Shopping Cart")
    void testShoppingCartPage() {
        // Setup: Add item to cart first (reusing navigation steps)
        setupCartWithItem();

        // Assert we're at cart page
        assertThat(page.locator("h1, h2")
                .filter(new Locator.FilterOptions().setHasText("Your Shopping Cart"))).isVisible();

        // Assert product name
        assertThat(
                page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black"))
                        .isVisible();

        // Assert quantity is 1
        Locator qtyField = page.locator("input[type='number'], [data-test-id='quantity']")
                .or(page.getByLabel("Quantity"));
        assertThat(qtyField.first()).hasValue("1");

        // Assert price
        assertThat(page.locator("text=/\\$149\\.98/").or(page.locator("text=/\\$164\\.98/")))
                .isVisible();

        // Select FAST In-Store Pickup
        Locator pickupOption =
                page.locator("text=/FAST In-Store Pickup/i").or(page.getByText("In-Store Pickup"));
        if (pickupOption.count() > 0) {
            pickupOption.first().click();
        }

        // Assert sidebar subtotal
        assertThat(page.locator("text=/Subtotal.*\\$14[0-9]\\.98/")).isVisible();

        // Assert handling fee
        assertThat(page.locator("text=/Handling.*\\$2\\.00/")).isVisible();

        // Assert taxes TBD
        assertThat(page.locator("text=/Taxes.*TBD/i")).isVisible();

        // Assert estimated total
        assertThat(page.locator("text=/Total.*\\$15[0-9]\\.98/i")).isVisible();

        // Enter promo code TEST
        Locator promoInput = page.locator("input[placeholder*='promo' i], input[name*='promo' i]")
                .or(page.getByLabel("Promo Code"));
        promoInput.first().fill("TEST");

        // Click APPLY
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("APPLY")).click();
        page.waitForTimeout(1000);

        // Assert promo code rejection
        assertThat(page.locator("text=/invalid|not valid|not found/i")).isVisible();

        // Proceed to checkout
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("PROCEED TO CHECKOUT"))
                .click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * TestCase 3: Create Account Page
     * 
     * Verifies the create account page and proceeds as guest.
     */
    @Test
    @DisplayName("TestCase 3: Create Account Page")
    void testCreateAccountPage() {
        // Setup: Navigate to checkout
        setupCartWithItem();
        proceedToCheckout();

        // Assert Create Account label is present
        assertThat(page.locator("text=/Create Account/i")).isVisible();

        // Select "Proceed as Guest"
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed as Guest"))
                .or(page.getByText("Guest")).first().click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * TestCase 4: Contact Information Page
     * 
     * Fills in contact information and continues.
     */
    @Test
    @DisplayName("TestCase 4: Contact Information Page")
    void testContactInformationPage() {
        // Setup
        setupCartWithItem();
        proceedToCheckout();
        proceedAsGuest();

        // Assert we're at Contact Information page
        assertThat(page.locator("text=/Contact Information/i")).isVisible();

        // Enter first name
        page.getByLabel("First Name").or(page.locator("input[name*='first' i]")).first()
                .fill("John");

        // Enter last name
        page.getByLabel("Last Name").or(page.locator("input[name*='last' i]")).first().fill("Doe");

        // Enter email
        page.getByLabel("Email").or(page.locator("input[type='email']")).first()
                .fill("john.doe@example.com");

        // Enter phone
        page.getByLabel("Phone").or(page.locator("input[type='tel']")).first().fill("3125551234");

        // Assert sidebar values
        assertThat(page.locator("text=/Subtotal.*\\$14[0-9]\\.98/")).isVisible();
        assertThat(page.locator("text=/Handling.*\\$2\\.00/")).isVisible();
        assertThat(page.locator("text=/Taxes.*TBD/i")).isVisible();
        assertThat(page.locator("text=/Total.*\\$15[0-9]\\.98/i")).isVisible();

        // Click CONTINUE
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("CONTINUE")).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * TestCase 5: Pickup Information
     * 
     * Verifies pickup information and order summary.
     */
    @Test
    @DisplayName("TestCase 5: Pickup Information")
    void testPickupInformation() {
        // Setup
        setupCartWithItem();
        proceedToCheckout();
        proceedAsGuest();
        fillContactInformation();

        // Assert contact information is correct
        assertThat(page.getByText("John Doe")).isVisible();
        assertThat(page.getByText("john.doe@example.com")).isVisible();
        assertThat(page.getByText("3125551234").or(page.getByText("(312) 555-1234"))).isVisible();

        // Assert pickup location
        assertThat(page.getByText("DePaul University Loop Campus & SAIC")).isVisible();

        // Assert selected pickup person
        assertThat(page.locator("text=/I'll pick/i")).isVisible();

        // Assert sidebar order summary
        assertThat(page.locator("text=/Subtotal.*\\$14[0-9]\\.98/")).isVisible();
        assertThat(page.locator("text=/Handling.*\\$2\\.00/")).isVisible();
        assertThat(page.locator("text=/Taxes.*TBD/i")).isVisible();
        assertThat(page.locator("text=/Total.*\\$15[0-9]\\.98/i")).isVisible();

        // Assert pickup item and price
        assertThat(page.getByText("JBL Quantum")).isVisible();
        assertThat(
                page.locator("text=/\\$14[0-9]\\.98/").or(page.locator("text=/\\$16[0-9]\\.98/")))
                        .isVisible();

        // Click CONTINUE
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("CONTINUE")).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * TestCase 6: Payment Information
     * 
     * Verifies payment page and returns to cart.
     */
    @Test
    @DisplayName("TestCase 6: Payment Information")
    void testPaymentInformation() {
        // Setup
        setupCartWithItem();
        proceedToCheckout();
        proceedAsGuest();
        fillContactInformation();
        continueFromPickup();

        // Assert sidebar with calculated taxes
        assertThat(page.locator("text=/Subtotal.*\\$14[0-9]\\.98/")).isVisible();
        assertThat(page.locator("text=/Handling.*\\$2\\.00/")).isVisible();

        // Taxes should now be calculated (not TBD)
        Locator taxElement = page.locator("text=/Taxes.*\\$\\d+\\.\\d{2}/i");
        assertThat(taxElement).isVisible();

        // Assert total
        assertThat(page.locator("text=/Total.*\\$16[0-9]\\.\\d{2}/i")).isVisible();

        // Assert pickup item and price
        assertThat(page.getByText("JBL Quantum")).isVisible();

        // Click "< BACK TO CART"
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("BACK TO CART"))
                .or(page.getByText("< BACK")).first().click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * TestCase 7: Your Shopping Cart - Delete Item
     * 
     * Deletes product from cart and verifies cart is empty.
     */
    @Test
    @DisplayName("TestCase 7: Delete Item from Cart")
    void testDeleteFromCart() {
        // Setup
        setupCartWithItem();

        // Delete product from cart
        Locator deleteButton =
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove"))
                        .or(page.locator("[aria-label*='Remove' i]"))
                        .or(page.locator("button:has-text('Delete')"));
        deleteButton.first().click();

        // Wait for cart to update
        page.waitForTimeout(1000);

        // Assert cart is empty
        assertThat(page.locator("text=/Your cart is empty/i").or(page.locator("text=/no items/i")))
                .isVisible();
    }

    // ==================== Helper Methods ====================

    /**
     * Helper: Setup cart with JBL Quantum earbuds
     */
    private void setupCartWithItem() {
        page.navigate("https://depaul.bncollege.com/");

        // Search for earbuds - using placeholder selector like traditional test
        page.getByPlaceholder("Enter your search details (").fill("earbuds");
        page.getByPlaceholder("Enter your search details (").press("Enter");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Apply Brand filter - JBL
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
        page.locator("#facet-brand").getByRole(AriaRole.LIST).locator("label")
                .filter(new Locator.FilterOptions().setHasText("brand JBL")).getByRole(AriaRole.IMG)
                .click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Click product
        page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Add to cart
        page.getByLabel("Add to cart").click();

        // Go to cart
        page.waitForSelector("text=Cart 1 item");
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 item")).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * Helper: Proceed to checkout from cart
     */
    private void proceedToCheckout() {
        page.getByLabel("Proceed To Checkout").first().click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * Helper: Proceed as guest
     */
    private void proceedAsGuest() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed as Guest"))
                .or(page.getByText("Guest")).first().click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * Helper: Fill contact information
     */
    private void fillContactInformation() {
        page.getByLabel("First Name").or(page.locator("input[name*='first' i]")).first()
                .fill("John");
        page.getByLabel("Last Name").or(page.locator("input[name*='last' i]")).first().fill("Doe");
        page.getByLabel("Email").or(page.locator("input[type='email']")).first()
                .fill("john.doe@example.com");
        page.getByLabel("Phone").or(page.locator("input[type='tel']")).first().fill("3125551234");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("CONTINUE")).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * Helper: Continue from pickup information
     */
    private void continueFromPickup() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("CONTINUE")).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }
}
