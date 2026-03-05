import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-request-action-heading',
  template: `
    <app-page-heading>{{ headerText }}</app-page-heading>
    <p class="govuk-caption-m">{{ timelineCreationDate | govukDate: 'datetime' }}</p>
    <ng-content></ng-content>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestActionHeadingComponent {
  @Input() headerText: string;
  @Input() timelineCreationDate: string;
}
