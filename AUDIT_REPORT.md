# Technical audit and remediation report

## Scope

This project automates Wikipedia Android reading-list behavior using Java 21, Appium, Cucumber, JUnit Platform, and Maven. Android is the executable primary implementation. XCUITest capability construction is retained as a non-breaking iOS extension point; iOS page behavior is not claimed as runtime-validated.

The exact Android target used during remediation was:

```text
Wikipedia package: org.wikipedia
Wikipedia versionName: 50600-r-2026-07-28
Android: 16 / API 36
Device: Pixel 7 API 36, emulator-5554
Appium: 3.5.0
UiAutomator2: 7.6.2
```

## Architecture

- `ThreadLocal<AppiumDriver>` isolates driver state per worker.
- Hooks own session creation, failure capture, and cleanup.
- Page objects own UI behavior and synchronization.
- Step definitions own business-level assertions and scenario state.
- `Config` provides typed timeouts, polling, gestures, ports, capabilities, and reset policy.
- `BasePage` centralizes explicit waits, W3C gestures, element interaction, and timeout diagnostics.
- Every scenario starts a new Appium session; the default `no.reset=false` provides deterministic application-state reset.

## Remediated runtime issues

1. **First-run Search promotion** — the `A Faster way to Search` modal is dismissed through its evidenced `Close` accessibility action.
2. **Exact search result** — the query input is excluded; the clickable result row for canonical title `Artificial intelligence` is selected using runtime hierarchy evidence.
3. **Article validation** — title alone is insufficient. The test also requires the Save action and evidenced article controls, while avoiding the unavailable `page_web_view` accessibility node.
4. **Article first-run prompts** — the Wikipedia Games modal and toolbar coach-mark interaction are handled with bounded, explicit logic.
5. **Create-list transition** — both picker-first and direct create-dialog states are supported.
6. **Saved navigation** — bounded Back navigation returns from the article activity before selecting the Saved tab.
7. **Current Saved variant** — the direct list UI and `Filter my lists` action are supported, with alternate Collections/search behavior retained.
8. **Saved promotions** — Sync, Discover, and share coach marks are handled only when their exact evidenced prompts appear.
9. **Article removal** — long-pressing the article opens `ReadingListItemActionsDialog`; the test selects `org.wikipedia:id/reading_list_item_remove`, waits for the bottom sheet to close, and then asserts zero title occurrences.
10. **Duplicate protection** — the duplicate scenario must receive the AUT's already-contains feedback and then count exactly one rendered article occurrence in the actual list UI.

## Quality controls

- No `Thread.sleep()` usage.
- No page-object timeout literals.
- Exact, case-insensitive article-title comparison handles AUT canonical casing without allowing partial matches.
- Failure screenshot and XML page source are attached to Cucumber and persisted on disk.
- Cucumber HTML/JSON and Allure result generation are configured.
- Generated reports, screenshots, IDE metadata, logs, and `target/` are excluded through `.gitignore`.
- Android and iOS capability construction are isolated so the iOS extension does not break Android execution.

## Submission gate

Before publishing or submitting the repository, run all three commands against the configured emulator and Appium server:

```powershell
mvn clean test "-Dcucumber.filter.tags=@smoke"
mvn clean test "-Dcucumber.filter.tags=@duplicate"
mvn clean test
```

Do not describe the suite as fully runtime-passing unless all three commands finish with `BUILD SUCCESS`. Runtime reports remain generated artifacts and should not be committed unless an evaluator explicitly requests them.
