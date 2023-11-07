import { ChangeDetectionStrategy, Component } from '@angular/core';

import { AirService } from '@tasks/air/shared/services/air.service';

@Component({
  selector: 'app-review-wait',
  template: `
    <app-base-task-container-component
      [header]="title$ | async"
      [customContentTemplate]="customContentTemplate"
      expectedTaskType="AIR_WAIT_FOR_REVIEW"
    >
    </app-base-task-container-component>

    <ng-template #customContentTemplate>
      <govuk-warning-text>Waiting for the regulator to review your report</govuk-warning-text>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewWaitComponent {
  title$ = this.airService.title$;

  constructor(private readonly airService: AirService) {}
}
