# Static audit and remediation report

## Scope and limitation

The authoritative input was the source listing supplied in chat; no project ZIP, APK, Appium page-source XML, screenshot set, emulator, ADB, or Maven installation was available initially. The omitted original README could not be audited. Android selectors were therefore checked against the official upstream application source commit recorded in `LOCATOR_EVIDENCE.md`, not falsely represented as live-device validation.

## Findings in the supplied project

### Correctness / runtime
- `SearchPage.isResultCandidate()` compared `text` with itself, so every non-empty text passed rather than the requested title.
- Article-open validation used `titleVisible || articleActionVisible`; the OR allowed insufficient evidence. It did not require article structure and title together.
- `HomePage` used obsolete `nav_tab_saved` semantics indirectly in other navigation; current upstream defines `nav_tab_reading_lists`.
- Reading-list navigation relied on generic text and repeated Back presses instead of the stable bottom-tab ID.
- Current upstream Saved UI uses `Collections` and `menu_search_lists`; the old `Filter my lists` path is not the primary current flow.
- Removal expected a text menu after long press. Current upstream enters selection mode and exposes `menu_delete_selected`, followed by a removal confirmation dialog.
- `ReadingListsPage.lastOpenedArticleTitle` was never assigned; return synchronization could take the wrong branch.
- Duplicate add did not require the AUT's `already contains` feedback, so it could not prove that the same-list attempt completed.
- Exact occurrence counting was only done after a potentially unproven duplicate action.
- Optional two-second probes were used repeatedly for normal transitions, creating race-prone branching.

### Architecture / lifecycle
- Driver creation was lazy from page objects, making hook ownership of session startup unclear and preventing startup failures from being uniformly scoped.
- Parallel configuration comments used JUnit Jupiter parallel keys; Cucumber's engine uses `cucumber.execution.parallel.*` keys.
- Parallel Android sessions lacked distinct UiAutomator2 `systemPort` configuration.
- Multiple dead onboarding constants/helpers and duplicated screen-dump implementations increased maintenance cost.
- `clearAppData()` iterated over every configured device from each scenario hook, unsafe in parallel execution.

### BDD / assessment alignment
- Main flow split “save” and “add to list” into steps whose page implementation did not strongly validate the AUT transition.
- Duplicate scenario lacked an explicit assertion of product feedback proving the duplicate add reached the app.
- Assertions were mostly hidden inside page methods; revised steps own final business assertions while pages own synchronization and UI behavior.

### Reporting / repository
- Failure artifact handling was fundamentally sound, but filenames could collide at one-second resolution under parallel execution.
- IDE files were present in the supplied listing despite `.idea/` being ignored; they should be removed from Git history.
- The original README was explicitly omitted, so evaluator setup completeness could not be verified.

## Live-run evidence received after the first delivery

The emulator screenshot showed the current AUT rendering the canonical result as
`Artificial intelligence`, while the data row requested `Artificial Intelligence`.
The first implementation used a case-sensitive UiSelector to discover the result,
even though its Java candidate check was case-insensitive. Consequently, the result
could remain visible while the locator waited for a title that differed only by
capitalization. The final locator API now includes an exact, case-insensitive
UiAutomator regex selector. Search-result discovery, article-title validation, saved
article lookup, removal, and duplicate occurrence counting all use that same
case-insensitive title policy. Reading-list names and action labels remain exact and
case-sensitive.

## Current-build first-run prompt evidence

The first Android 16 smoke run supplied an Appium screenshot, page summary, and stack
trace for Wikipedia `50600-r-2026-07-28`. Opening Search displayed the blocking modal
`A Faster way to Search`, with an accessibility action named `Close`. The old flow
mistook the covered Search screen for a missing navigation tab and attempted to tap
the tab a second time. `HomePage.openSearch()` now waits for one of three evidenced
states—search input, search card, or that promotion—dismisses the promotion by its
accessibility label, waits for it to disappear, and then opens the search card. It no
longer retries the bottom tab while a modal is blocking the screen.

The Saved flow also handles the upstream-source-supported `Sync reading lists` /
`Not now` first-run prompt before selecting Collections. This is scoped by the prompt
title and cannot dismiss an unrelated dialog.

## Remediation design

- Explicit hook-owned session lifecycle with `ThreadLocal<AppiumDriver>`.
- Central typed configuration with system property/environment/file precedence.
- One centralized wait API in `BasePage`; no sleeps and no page timeout literals.
- Source-evidenced Android IDs and exact UI text, scoped XPath only for text-or-description and clickable ancestry.
- Strict article transition condition: search input absent AND article WebView/action structure present AND exact title visible.
- Current Saved/Collections search and action-mode removal workflow.
- Genuine duplicate attempt requiring AUT feedback and one visible occurrence afterward.
- Scenario-local page/step state, clean Appium reset defaults, and one device/backend port per parallel worker.
- Screenshot and XML evidence attached and persisted before driver cleanup.
- GitHub-ready README, ignore rules, scripts, Cucumber HTML/JSON, and Allure integration.
