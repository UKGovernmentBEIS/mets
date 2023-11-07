import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-non-conformities',
  template: `
    <app-action-task header="Uncorrected non-conformities" [breadcrumb]="true">
      <app-non-conformities-group
        [uncorrectedNonConformities]="uncorrectedNonConformities$ | async"
      ></app-non-conformities-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonConformitiesComponent {
  uncorrectedNonConformities$ = (
    this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
  ).pipe(map((payload) => payload.verificationReport.uncorrectedNonConformities));

  constructor(private readonly aerService: AerService) {}
}
