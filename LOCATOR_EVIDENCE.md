# Android locator evidence

The Android locators in this repository were checked against the official
`wikimedia/apps-android-wikipedia` source tree at commit
`88c72a95d098fc266f85acd2c54cf75701b9c5ef` (upstream `main`, audited
2026-08-15).

| Framework locator / text | Upstream evidence |
|---|---|
| `nav_tab_home`, `nav_tab_search`, `nav_tab_reading_lists` | `app/src/main/java/org/wikipedia/navtab/NavTab.kt` and `res/values/ids.xml` |
| `search_card` | `HistoryFragment.kt` and `res/layout/view_history_header_with_search.xml` |
| `search_src_text` | `CabSearchView.kt` / AndroidX SearchView merged resource |
| `page_web_view`, `page_actions_tab_layout` | `res/layout/fragment_page.xml` |
| `page_save` | `PageActionItem.kt` and `res/values/ids.xml` |
| `Add to list` | `reading_list_add_to_list_button` in `res/values/strings.xml` |
| `create_button`, `list_of_lists` | `res/layout/dialog_add_to_reading_list.xml` |
| `text_input`, `android:id/button1` / `OK` | `res/layout/dialog_text_input.xml`, `TextInputDialog.kt` |
| `menu_search_lists` | `res/menu/menu_main.xml`, `MainFragment.kt` |
| `Collections` | `reading_lists_tab_collections` and `ReadingListsScreen.kt` |
| `menu_delete_selected` and confirmation | `menu_action_mode_reading_list_articles.xml`, `ReadingListBehaviorsUtil.kt` |
| `Add to another reading list` | `menu_reading_list_page_toggle.xml`, `LongPressMenu.kt`, `reading_list_add_to_other_list` |
| `already contains` confirmation | `AddToReadingListDialog.commitChanges()` and `reading_list_article_already_exists_message` |

The Android 16 smoke-run evidence for Wikipedia `50600-r-2026-07-28` showed a
blocking first-run Search promotion titled `A Faster way to Search` and an
accessibility action named `Close`. Those exact values are now used to dismiss that
specific modal before interacting with the Search card.

The emulator evidence supplied on 2026-08-15 also showed that Wikipedia renders the
canonical result as `Artificial intelligence` even when the test data uses
`Artificial Intelligence`. Article-title locators therefore use exact
case-insensitive matching; this is not a partial/contains match.

Source inspection is not a substitute for an Appium page-source capture from a
specific installed APK. Before submission, run the suite against the evaluator's
exact Play Store build. On any mismatch, use the automatically saved XML under
`target/failure-screenshots`; do not replace a locator without evidence.
