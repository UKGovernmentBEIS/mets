import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { VirApplicationReviewedRequestActionPayload } from 'pmrv-api';

import { VirService } from '../../core/vir.service';

@Component({
  selector: 'app-response-list',
  template: `
    <app-action-task [header]="virTitle$ | async" [breadcrumb]="true">
      <ng-container *ngFor="let item of operatorImprovementResponses$ | async | keyvalue">
        <h2 class="govuk-heading-s govuk-!-margin-0">{{ item.key | verificationReferenceTitle }}</h2>
        <ul app-task-item-list>
          <li app-task-item link="{{ item.key }}/summary" linkText="Respond to operator" [hasContent]="true">
            <app-operator-response-data-item
              [operatorImprovementResponse]="item.value"></app-operator-response-data-item>
          </li>
        </ul>
      </ng-container>
      <h2 class="govuk-heading-s govuk-!-margin-0">Create report summary</h2>
      <ul app-task-item-list>
        <li app-task-item link="report-summary" linkText="Create summary" [hasContent]="true"></li>
      </ul>
    </app-action-task>
  `,

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResponseListComponent {
  virPayload$ = this.virService.payload$ as Observable<VirApplicationReviewedRequestActionPayload>;
  virTitle$ = this.virPayload$.pipe(
    map((payload) => payload.reportingYear + ' verifier improvement report decision submitted'),
  );
  operatorImprovementResponses$ = this.virPayload$.pipe(map((payload) => payload?.operatorImprovementResponses));

  constructor(private readonly virService: VirService) {}
}
