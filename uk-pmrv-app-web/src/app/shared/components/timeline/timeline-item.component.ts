import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params } from '@angular/router';

import { RequestActionInfoDTO } from 'pmrv-api';

@Component({
  selector: 'app-timeline-item',
  standalone: false,
  template: `
    <h3 class="govuk-heading-s govuk-!-margin-bottom-1">{{ action | itemActionHeader: year }}</h3>
    <p class="govuk-body govuk-!-margin-bottom-1">{{ action.creationDate | govukDate: 'datetime' }}</p>
    <span *ngIf="link">
      <a
        [routerLink]="link"
        [state]="state"
        [queryParams]="queryParams"
        govukLink
        [hidden-text]="action | itemActionHeader: year">
        View details
      </a>
    </span>
    <hr class="govuk-!-margin-top-6" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TimelineItemComponent {
  @Input() action: RequestActionInfoDTO;
  @Input() link: any[];
  @Input() state: any;
  @Input() queryParams: Params;
  @Input() year: string | number;
}
