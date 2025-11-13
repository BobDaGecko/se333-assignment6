# Assignment 6 - UI Testing

**Student:** Kellen Siczka  
**Course:** SE333 - Software Testing  
**Assignment:** Assignment 6 - UI Testing with Playwright  
**Date:** November 11, 2025

## AI Assistance Disclosure

This assignment explicitly permits and encourages the use of AI tools in specific areas, particularly in Part 4 where Playwright MCP (Model Context Protocol) is used to generate UI tests through natural language prompts.

**AI was used in the following areas:**

- **Part 4 (playwrightLLM package)**: AI agent via Playwright MCP to generate test cases from natural language descriptions
- **Documentation**: Improving clarity and readability of technical explanations
- **Code quality**: Refactoring variable names and code organization for better maintainability
- **Build configuration**: Validating and supplementing Maven configuration
- **CI/CD configuration**: Validating and supplementing Github Actions configuration

**Original work includes:**

- All test case design and logic for Part B (playwrightTraditional package)
- Manual Playwright test implementation and assertions
- Test execution strategy and verification approach
- Comparative analysis and reflection on traditional vs. AI-assisted testing approaches

All core testing concepts, assertions, and comparative analysis represent my own understanding and reasoning.

---

## GitHub Repository

**Repository Link:** [https://github.com/BobDaGecko/se333-assignment6](https://github.com/BobDaGecko/se333-assignment6)

**GitHub Actions Status:** All tests are automatically executed via GitHub Actions on every push. The workflow builds the project, runs both traditional and AI-generated tests, and generates video recordings of test executions.

---

## Project Structure

```
Assignment_6-SE333-Kellen_Siczka-11_11_25/
├── src/
│   └── test/
│       └── java/
│           ├── playwrightTraditional/
│           │   └── DePaulBookstoreTest.java       # Manually written tests
│           └── playwrightLLM/
│               └── DePaulBookstoreTestAI.java     # AI-generated tests
├── videos/                                         # Traditional test recordings
├── videos-ai/                                      # AI-generated test recordings
├── pom.xml                                         # Maven configuration
├── package.json                                    # NPM dependencies for Playwright
└── README.md                                       # This file
```

---

## Running the Tests

### Prerequisites

- Java 1.8+
- Maven 3.8.5+
- Node.js and npm (for Playwright browsers)

### Setup

```bash
# Install Node dependencies
npm install

# Install Playwright browsers
npx playwright install

# Run all tests
mvn test

# Run only traditional tests
mvn test -Dtest=playwrightTraditional.*

# Run only AI-generated tests
mvn test -Dtest=playwrightLLM.*
```

---

## Reflection: Traditional vs. AI-Assisted UI Testing

This was an interesting assignment. Software testing, while crucial, is incredibly tedious and time-consuming. Don't get me wrong—testing can have enjoyable aspects, it's rewarding when you catch bugs, and the logic is pretty intuitive. But being able to automate the process and remove the tedium is crucial, especially in UI testing. UI testing is one of those areas that's not only tedious but also abstract and relatively ambiguous compared to the clear-cut stats and logic of pure software testing. This assignment tackled both of those challenges with some really cool technologies.

### Part B: Manual Testing with Playwright (playwrightTraditional)

First up is **Playwright**, a browser automation framework that can record your interactions with a live UI and generate test code. I'd thought about this kind of tool existing, but it's cool to actually see it work so seamlessly. You launch Playwright Codegen with `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="codegen"`, and it opens a browser window where you can navigate through your application. As you click buttons, fill in forms, and navigate pages, Playwright records every action and translates it into Java code using its API.

This is an incredibly powerful tool that really opened my eyes to the world of testing automation. The process was straightforward:

1. Launch Codegen and navigate through the DePaul bookstore website
2. Perform all the test steps I wanted to automate (searching, filtering, adding to cart, etc.)
3. Copy the generated Playwright API calls
4. Refactor and organize the code into structured JUnit test methods
5. Add assertions to verify expected behavior
6. Debug and refine selectors until everything worked

**The Good:**

- **Complete control:** I could fine-tune every selector, wait condition, and assertion
- **Predictable:** Once it worked, it worked consistently
- **Learning experience:** Manually writing tests forced me to really understand the DOM structure and how Playwright interacts with web elements
- **Easy debugging:** When something failed, I knew exactly what line of code caused it

**The Challenges:**

- **Time-intensive:** Took 4-6 hours to write, refine, and debug all the test cases
- **Brittle selectors:** Some elements required very specific selectors that could break if the website UI changes
- **Tedious:** A lot of repetitive code for similar patterns
- **Learning curve:** Had to learn Playwright's API, understand different locator strategies (role-based, text-based, CSS selectors), and figure out when to use waits

However, Playwright isn't perfect. Manually dealing with selectors can be tedious, and while advanced configurations and options exist, it wasn't necessarily the most intuitive tool out there. Emulating a browser and attempting to track changes can be particularly challenging, especially when website architectures obfuscate things when viewed through a browser, which can restrict testing capabilities.

### Part 4: AI-Generated Testing with Playwright MCP (playwrightLLM)

The second part of this assignment used **Playwright MCP (Model Context Protocol)** with **Claude Sonnet 4.5** via VS Code's GitHub Copilot integration. This is where things got really interesting—and frustrating.

MCP servers represent a groundbreaking integration between advanced language models and powerful testing tools. The ability to describe what you want in natural language and have the AI not only generate code but actually interact with the browser to understand the application is huge. This is exactly what this assignment demonstrated—or at least, what it was supposed to demonstrate.

**The Process:**

I provided natural language descriptions to the AI agent like "Navigate to the DePaul bookstore, search for earbuds, filter by brand and color, and verify the product details." The AI was supposed to:

1. Use Playwright MCP to interact with the browser
2. Explore the website's DOM structure
3. Generate appropriate Java test code with proper selectors and assertions

**The Reality: Configuration Nightmares**

Here's where things went sideways. **Getting the MCP server working was the biggest challenge of this entire assignment.** I develop with Ubuntu 22.04 LTS on WSL2 via Windows 11. WSL is widely supported and the dev experience is usually seamless, but configurations can have issues—primarily due to pathing.

In this case, Chrome was being referenced with a Windows path **within WSL**, which just didn't work. The MCP server couldn't launch the browser properly. I spent hours troubleshooting this issue, trying different path configurations, environment variables, and workarounds. The only solution I found that actually worked was to pull the project from GitHub to a local Windows location and run the MCP server from there instead of within WSL. This was **incredibly tedious** to figure out.

**AI Agent Behavior Issues**

Even after getting MCP working, the AI agent had some strange behaviors:

1. **Limited browser interaction:** The AI was keen to generate tests without actually using Playwright MCP to explore the site thoroughly. It seemed to prefer guessing what selectors should be rather than verifying them.

2. **Context awareness without reference:** Interestingly, the AI did "read" my manual test file (`DePaulBookstoreTest.java`) and understood the general structure. However, it explicitly acknowledged that it couldn't use this as a reference for selectors, which was odd. It seemed to have context about what I was trying to do but refused to leverage working examples.

3. **Guessing instead of validating:** When tests failed, the AI would sometimes suggest fixes without actually re-running MCP to verify them. This led to multiple rounds of trial and error.

**The Iterative Debugging Process**

The AI-generated tests initially failed completely—all 7 test cases errored out. The issues ranged from:

- **Incorrect selectors:** The AI used generic role-based selectors that matched multiple elements, causing "strict mode violations"
- **Wrong interaction patterns:** For example, it tried to click a search button then fill a textbox, when the actual site used a placeholder-based search field
- **Over-defensive code:** Lots of `.or()` chains with multiple fallback selectors that were unnecessary
- **Assumptions about text:** Cart count was "(1)" in the generated code but "Cart 1 item" on the actual site

I had to go through multiple iterations of:

1. Run the tests
2. Read the error messages
3. Compare with my working manual tests
4. Prompt the AI to fix specific issues
5. Repeat

After extensive debugging, I got **2 out of 7 tests passing** (`testBookstoreSearchAndFilter` and `testDeleteFromCart`). The remaining 5 tests still fail with various selector issues—mostly strict mode violations where selectors match multiple elements instead of being specific enough.

**What the AI Did Well:**

- **Fast initial generation:** When it worked, it generated a lot of code quickly
- **Structured output:** The AI created well-organized test classes with helper methods
- **Best practices:** It included proper video recording configuration and followed JUnit conventions

**What Went Wrong:**

- **Configuration hell:** The MCP setup issues on WSL were a nightmare
- **Reliability:** AI-generated selectors didn't match the actual DOM structure
- **Validation gap:** The AI didn't effectively use MCP to verify its generated code
- **Time investment:** While initial generation was fast, debugging took hours—possibly longer than writing tests manually

### Comparison and Key Takeaways

| Aspect             | Manual (Playwright) | AI-Assisted (MCP)     |
| ------------------ | ------------------- | --------------------- |
| **Setup Time**     | ~30 minutes         | ~3 hours (WSL issues) |
| **Coding Time**    | ~4-6 hours          | ~30 minutes           |
| **Debugging Time** | ~1 hour             | ~3+ hours             |
| **Tests Passing**  | 7/7 (100%)          | 2/7 (29%)             |
| **Learning Value** | High                | Medium                |
| **Control**        | Complete            | Limited               |
| **Frustration**    | Low                 | Very high             |

### Final Thoughts

This assignment demonstrated both the promise and the limitations of AI-assisted testing. In an ideal world, AI would dramatically accelerate test development while maintaining high quality. In reality—at least with current tools and my development environment—the AI approach was more frustrating than helpful.

**Manual testing** with Playwright took longer upfront but resulted in reliable, maintainable tests that I fully understood. When something broke, I knew exactly how to fix it.

**AI-assisted testing** promised speed but delivered configuration headaches, unreliable selectors, and hours of debugging. The AI agent seemed to understand context but struggled to effectively use the MCP tools it was supposed to leverage. Getting 2 out of 7 tests working after all that effort felt like a failure rather than a success.

**Would I use AI-assisted testing again?** Maybe, but not in its current state. The configuration issues with WSL/Windows were a dealbreaker, and the AI's tendency to guess rather than validate undermined the entire purpose of using MCP. If these tools mature and become more reliable, I can see value in using AI for initial test generation followed by manual refinement. But for now, I'll stick with manual testing for anything that actually needs to work.

**The hybrid approach** might be the answer: use AI to generate boilerplate code and structure, but rely on manual expertise to ensure selectors are correct and tests are reliable. Or better yet, use AI to accelerate manual workflows rather than trying to replace them entirely. For this assignment, I learned far more from the manual testing process than from fighting with AI configuration issues.

In the end, software testing is about reliability and confidence. These AI tools aren't there yet—at least not for my setup. But the potential is undeniable, and I'm curious to see how they evolve.
