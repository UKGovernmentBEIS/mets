import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { ReturnOfAllowancesService } from '../../core/return-of-allowances.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  isEditable$ = this.returnOfAllowancesService.isEditable$;
  payload$ = this.returnOfAllowancesService.getPayload().pipe(
    first(),
    map(
      (payload) =>
        (payload as ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload).returnOfAllowancesReturned,
    ),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly returnOfAllowancesService: ReturnOfAllowancesService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.returnOfAllowancesService
      .saveReturnOfAllowancesReturnedSectionStatus(true)
      .pipe(
        switchMap(() =>
          this.returnOfAllowancesService.submitReturnOfAllowancesReturned().pipe(this.pendingRequest.trackRequest()),
        ),
      )
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route }));
  }
}
