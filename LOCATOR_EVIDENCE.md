# Android locator evidence

The Android implementation was checked against both:

1. the official `wikimedia/apps-android-wikipedia` source tree at commit `88c72a95d098fc266f85acd2c54cf75701b9c5ef`; and
2. Appium screenshots and XML captured from Wikipedia `50600-r-2026-07-28` on Android 16 / API 36 on 2026-08-15.

Source inspection is supporting evidence, not a substitute for runtime hierarchy evidence from the installed APK.

## Runtime-evidenced behavior

| Framework behavior | Installed-AUT evidence |
|---|---|
| Search navigation | `org.wikipedia:id/nav_tab_search`, Search card, and `org.wikipedia:id/search_src_text` |
| Search promotion | `A Faster way to Search` with accessibility action `Close` |
| Exact result | Clickable `android.view.View` row containing title `Artificial intelligence` and description `Intelligence of machines` |
| Article page | Exact title plus `org.wikipedia:id/page_save` and evidenced controls such as `Language`, `Find in article`, or `Contents`; the installed build does not expose `page_web_view` through UiAutomator2 |
| Article promotion | `Wikipedia games` modal with accessibility action `Close` |
| Save transition | `org.wikipedia:id/page_save`, followed by exact action `Add to list` |
| Create-list dialog | `org.wikipedia:id/text_input` and `android:id/button1` (`OK`) |
| Saved navigation | `org.wikipedia:id/nav_tab_reading_lists` |
| Current Saved search | accessibility label `Filter my lists`; `org.wikipedia:id/menu_search_lists` remains a supported alternate ID |
| Saved promotion | `Discover articles picked just for you` with `No thanks` |
| Reading-list share coach mark | `Share this reading list with others` with `Got it` |
| Article in list | `org.wikipedia:id/page_list_item_title`, text `Artificial intelligence` |
| Article row | Outer `android.widget.FrameLayout` is `clickable=true` and `long-clickable=true`; long-press opens the article-actions bottom sheet |
| Remove article action | `org.wikipedia:id/reading_list_item_remove`; the displayed text is `Remove from AI Reading List` for this data row |

The list-header three-dot menu is intentionally not used to remove an article. Its `Remove all from offline` action only removes downloaded copies, while `Delete list` deletes the whole list.

## Source-supported behavior

| Behavior | Official source evidence |
|---|---|
| Bottom navigation IDs | `NavTab.kt` and `res/values/ids.xml` |
| Save and add-to-list workflow | `PageActionItem.kt`, reading-list dialogs, and `res/values/strings.xml` |
| Reading-list item long press | `ReadingListFragment.ReadingListPageItemCallback.onLongClick()` |
| Article-actions bottom sheet | `ReadingListItemActionsDialog.kt` and `ReadingListItemActionsView.kt` |
| Remove action ID | `view_reading_list_page_actions.xml`: `reading_list_item_remove` |
| Remove action text | `reading_list_remove_from_list`: `Remove from %s` |
| Duplicate feedback | `AddToReadingListDialog.commitChanges()` and `reading_list_article_already_exists_message` |

## Exact-title policy

The test data uses `Artificial Intelligence`, while the AUT canonical title is `Artificial intelligence`. The framework retrieves evidenced text nodes and performs an exact case-insensitive Java comparison. It does not use a partial/contains match.

The Search query input is explicitly excluded when selecting a search-result title. The search row itself is activated, rather than relying on an unreliable tap of its non-clickable title child.

## Synchronization policy

All waits and gesture durations are supplied by `Config` through `BasePage`. There are no `Thread.sleep()` calls or page-object timeout literals. Failure hooks save both a PNG screenshot and XML hierarchy under `target/failure-screenshots/`.
