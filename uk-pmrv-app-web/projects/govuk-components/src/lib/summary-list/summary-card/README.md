## Summary Card

If you’re showing multiple summary lists on a page, you can show each list within a summary card. This lets you visually separate each summary list and give each a title and some actions.

Use summary cards when you need to show:
- multiple summary lists that all describe the same type of thing, such as people
- actions that will apply to all the items in a list

Summary cards are often used in case working systems to help users quickly view a set of information and related actions.

Do not use summary cards if you only need to show a small amount of related information. Use summary lists instead, and structure them with headings if needed.

If you’re showing summary cards at the end of a longer journey, you might want to familiarise the user with them earlier on – such as when the user reviews individual sections.

Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/summary-list/#summary-cards).

The summary card component accepts template for card actions.
Provide the template with `#actions` key.

A reminder that govukLinks provided in that template must equal to `govukLink="summaryAction"`


### Inputs

- `title` - The summary card title.

### Examples

```html

<div govuk-summary-card [title]="'University of Gloucestershire'">
  <ng-template #actions>
    <a govukLink="summaryAction" routerLink="/change">Change</a>
    <a govukLink="summaryAction" routerLink="/delete">Delete</a>
  </ng-template>
  <dl govuk-summary-list [hasBorders]="true" [hasBottomMargin]="false">
    @for (item of [{ id: 'Name', value: 'John Doe' }, { id: 'id', value: 'Mary Jane' }]; track item.id) {
    <div govukSummaryListRow>
      <dt govukSummaryListRowKey>{{ item.id }}</dt>
      <dd govukSummaryListRowValue>{{ item.value }}</dd>
      <dd govukSummaryListRowActions>
        <a govukLink [routerLink]="'edit'">Change</a> |
        <a govukLink [routerLink]="'delete'">Delete</a>
      </dd>
    </div>
    }
  </dl>
</div>
```
