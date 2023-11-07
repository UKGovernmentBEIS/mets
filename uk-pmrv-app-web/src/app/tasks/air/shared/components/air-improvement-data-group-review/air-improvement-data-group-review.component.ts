import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { originalOrder } from '@shared/keyvalue-order';

import { AirApplicationSubmitRequestTaskPayload, AirImprovement } from 'pmrv-api';

@Component({
  selector: 'app-air-improvement-data-group-review',
  template: `
    <ng-container *ngFor="let airImprovement of airImprovements | keyvalue: originalOrder">
      <h2 class="govuk-heading-s govuk-!-margin-0">
        {{ airImprovement.value | airImprovementTitle: airImprovement.key }}
      </h2>
      <ul app-task-item-list>
        <li
          app-task-item
          link="{{ airImprovement.key }}/summary"
          linkText="Review information about this improvement"
          [status]="airRequestPayload | taskStatus: airImprovement.key"
          [hasContent]="true"
        >
          <app-air-operator-response-data-item
            [operatorImprovementResponse]="airRequestPayload?.operatorImprovementResponses?.[airImprovement.key]"
          ></app-air-operator-response-data-item>
        </li>
      </ul>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AirImprovementDataGroupReviewComponent {
  airImprovements: { [key: string]: AirImprovement };
  airRequestPayload: AirApplicationSubmitRequestTaskPayload;

  @Input() set airPayload(value: AirApplicationSubmitRequestTaskPayload) {
    this.airRequestPayload = value;
    this.airImprovements = this.airRequestPayload.airImprovements;
  }

  readonly originalOrder = originalOrder;
}
