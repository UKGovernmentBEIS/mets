import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-recommended-improvements',
  template: `
    <app-action-task header="Recommended improvements" [breadcrumb]="true">
      <app-recommended-improvements-group
        [recommendedImprovements]="recommendedImprovements$ | async"
      ></app-recommended-improvements-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendedImprovementsComponent {
  recommendedImprovements$ = (
    this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
  ).pipe(map((payload) => payload.verificationReport.recommendedImprovements));

  constructor(private readonly aerService: AerService) {}
}
