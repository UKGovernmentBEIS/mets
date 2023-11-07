import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-misstatements',
  template: `
    <app-action-task header="Uncorrected misstatements" [breadcrumb]="true">
      <app-misstatements-group [uncorrectedMisstatements]="uncorrectedMisstatements$ | async">
      </app-misstatements-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisstatementsComponent {
  uncorrectedMisstatements$ = (
    this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>
  ).pipe(map((payload) => payload.verificationReport.uncorrectedMisstatements));

  constructor(private readonly aerService: AerService) {}
}
