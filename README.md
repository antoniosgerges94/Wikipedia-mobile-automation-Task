# Wikipedia Mobile Automation

Professional Java/Appium/Cucumber automation for the Wikipedia mobile application's reading-list workflow. Android is the executable primary implementation; the driver, configuration, locators, and base layer provide a non-breaking iOS extension point.

## Assessment coverage

### Main scenario (`@smoke`)
1. Launch Wikipedia and complete onboarding when shown.
2. Search for **Artificial Intelligence**.
3. open the exact result and validate article-page structure (not merely title text).
4. Save it, invoke the application's **Add to list** action, and create **AI Reading List**.
5. Navigate through **Saved → Collections**, search for and open the list.
6. Assert the article is visible, remove it through the real selection UI, confirm removal, and assert zero visible occurrences.

### Duplicate protection (`@duplicate`)
The test creates the list, opens the saved article from that list, uses the article's saved-state menu, selects **Add to another reading list**, and selects the same list. It requires the AUT's **already contains** confirmation, returns to the actual list UI, and asserts exactly one rendered article occurrence. No framework-only flag substitutes for the UI count.

## Stack

- Java 21
- Maven
- Appium Java Client 10
- UiAutomator2 (Android), XCUITest capability construction (iOS extension)
- Cucumber 7 / Gherkin Scenario Outlines
- JUnit Platform
- Cucumber HTML + JSON and Allure results

## Prerequisites (Windows 11)

1. Install JDK 21 and set `JAVA_HOME`; verify with `java -version`.
2. Install Maven; verify with `mvn -version`.
3. Install Android Studio/SDK, Platform Tools, and an Android 17 emulator image.
4. Add `%ANDROID_HOME%\platform-tools` and `%ANDROID_HOME%\emulator` to `PATH`.
5. Install Node.js LTS, Appium 2/3, and UiAutomator2:
   ```powershell
   npm install -g appium
   appium driver install uiautomator2
   appium driver list --installed
   ```
6. Install the official Wikipedia app (`org.wikipedia`) from Google Play on the emulator, or set `app.path` to a trusted APK.
7. Optional Allure CLI: `scoop install allure` (or install from the Allure releases page).

## Emulator and Appium startup

```powershell
emulator -list-avds
emulator -avd YOUR_AVD_NAME
adb devices
adb -s emulator-5554 shell pm list packages org.wikipedia
appium --address 127.0.0.1 --port 4723
```

`adb devices` must show `emulator-5554 device`, not `offline` or `unauthorized`. Appium's status endpoint should answer at `http://127.0.0.1:4723/status`.

## Configuration

Edit `src/test/resources/config/config.properties`, or override any value with a JVM property (for example `-Dudid=emulator-5556`) or an uppercase underscore environment variable (for example `UDID`). Resolution is JVM property → environment → file → default.

Supported keys include:

- `platform.name`, `appium.url`, `device.name`, `udid`, `platform.version`, `automation.name`
- `app.package`, `app.activity`, `app.wait.activity`, `app.path`, `bundle.id`
- `no.reset`, `full.reset`, `auto.grant.permissions`, `auto.accept.alerts`, `new.command.timeout`
- `wait.timeout.seconds`, `wait.short.timeout.seconds`, `wait.long.timeout.seconds`, `wait.polling.millis`
- `screenshots.dir`, `system.port`, `wda.local.port`

Comma-separated `appium.url`, `udid`, `system.port`, and `wda.local.port` values support parallel workers.

### State isolation

The submission default is `no.reset=false` and `full.reset=false`: UiAutomator2 starts each scenario with reset application state while preserving the installed APK. Every scenario gets a new Appium session and quits it in an `@After` hook. Parallel tests require separate devices. Do not enable `no.reset=true` for assessment runs unless an external cleanup strategy is supplied; persistent lists would invalidate deterministic preconditions.

## Run tests

From the repository root:

```powershell
mvn clean test
mvn clean test "-Dcucumber.filter.tags=@smoke"
mvn clean test "-Dcucumber.filter.tags=@duplicate"
```

Equivalent Windows scripts are in `scripts/`.

## Reports and diagnostics

After execution:

- Cucumber HTML: `target/cucumber-reports/cucumber.html`
- Cucumber JSON: `target/cucumber-reports/cucumber.json`
- Surefire: `target/surefire-reports/`
- Allure raw results: `target/allure-results/`
- Failed-scenario rerun file: `target/failed-scenarios.txt`

Generate Allure HTML:

```powershell
allure serve target\allure-results
# or
scripts\generate-allure-report.bat
allure open target\allure-report
```

On failure, the hook attaches and writes both a PNG screenshot and XML page source under `target/failure-screenshots/` before quitting the session.

## Parallel execution

Use one emulator and one automation backend port per worker. Separate Appium endpoints are recommended:

```powershell
# Terminal 1
appium --port 4723
# Terminal 2
appium --port 4724

mvn clean test `
  "-Dcucumber.execution.parallel.enabled=true" `
  "-Dcucumber.execution.parallel.config.strategy=fixed" `
  "-Dcucumber.execution.parallel.config.fixed.parallelism=2" `
  "-Dudid=emulator-5554,emulator-5556" `
  "-Dappium.url=http://127.0.0.1:4723,http://127.0.0.1:4724" `
  "-Dsystem.port=8200,8201"
```

Never run more concurrent scenarios than configured devices. Driver state is `ThreadLocal`; page and step state is scenario-local.

## Android / iOS status

- **Android:** page workflow and source-supported locators are implemented for UiAutomator2.
- **iOS bonus architecture:** XCUITest capabilities, iOS configuration, cross-platform text/accessibility locator builders, and driver isolation are present. iOS page-specific selectors and workflow are intentionally not claimed as validated because no iOS simulator/device hierarchy was supplied. Add them only from an actual iOS Appium page source; Android compilation and behavior remain isolated.

See `LOCATOR_EVIDENCE.md` for the exact official Android source commit and evidence used. A final live run against the installed Play Store build remains mandatory because upstream source inspection cannot prove the hierarchy of an unknown installed APK.

## Project structure

```text
src/main/java/com/automation/wikipedia/
  config/     centralized typed runtime configuration
  core/       BasePage waits, interactions, gestures, diagnostics
  driver/     ThreadLocal Appium lifecycle and platform capabilities
  pages/      page-specific behavior
  utils/      cross-platform locator builders
src/test/java/com/automation/wikipedia/
  runners/    JUnit Platform suite
  steps/      Cucumber orchestration and hooks
  utils/      failure evidence
src/test/resources/
  features/   business-readable Gherkin
  config/     runtime defaults
  allure/     report metadata
```

All synchronization timeouts come from configuration through `BasePage`. There are no `Thread.sleep` calls or page-level timeout literals.

## Troubleshooting

- **Session cannot start:** check `adb devices`, Appium URL, installed UiAutomator2 driver, package/activity, and Java/Node compatibility.
- **Activity never starts:** leave `app.wait.activity` blank unless the exact installed build proves a required value; onboarding can precede the main screen.
- **Element timeout:** inspect the paired PNG/XML artifact. Confirm app language is English and compare the hierarchy with `LOCATOR_EVIDENCE.md` before changing a selector.
- **Search has no result:** verify network access in the emulator and wait for Wikipedia content to load.
- **List already exists:** restore clean-state defaults and ensure `no.reset=false`.
- **Parallel collision:** use distinct UDIDs, Appium ports, and `system.port` values; keep parallelism at or below device count.

## GitHub submission

Generated reports, screenshots, IDE metadata, logs, and `target/` are ignored. Before pushing:

```powershell
git init
git add .
git status
git commit -m "Add Wikipedia mobile reading-list automation assessment"
git branch -M main
git remote add origin https://github.com/YOUR_USER/YOUR_REPOSITORY.git
git push -u origin main
```
