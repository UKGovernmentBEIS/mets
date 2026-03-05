import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-opinion-statement',
  template: `
    <app-action-task header="Opinion statement" [breadcrumb]="true">
      <app-opinion-statement-group [payload]="payload$ | async"></app-opinion-statement-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OpinionStatementComponent {
  payload$ = (this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>).pipe(
    map((payload) => payload),
  );

  constructor(private readonly aerService: AerService) {}
}
