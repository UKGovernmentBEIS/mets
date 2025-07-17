import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-non-compliances',
  template: `
    <app-action-task header="Uncorrected non-compliances" [breadcrumb]="true">
      <app-non-compliances-group
        [uncorrectedNonCompliances]="uncorrectedNonCompliances$ | async"></app-non-compliances-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonCompliancesComponent {
  uncorrectedNonCompliances$ = (
    this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
  ).pipe(map((payload) => payload.verificationReport.uncorrectedNonCompliances));

  constructor(private readonly aerService: AerService) {}
}
