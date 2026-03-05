import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-compliance-ets',
  template: `
    <app-action-task header="Compliance with ETS rules" [breadcrumb]="true">
      <app-compliance-ets-group [etsComplianceRules]="etsComplianceRules$ | async"></app-compliance-ets-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComplianceEtsComponent {
  etsComplianceRules$ = (this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>).pipe(
    map((payload) => payload.verificationReport.etsComplianceRules),
  );

  constructor(private readonly aerService: AerService) {}
}
