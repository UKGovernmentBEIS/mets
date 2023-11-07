import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  styles: [
    `
      .float-right {
        float: right;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class SummaryComponent {
  isEditable$ = this.aerService.isEditable$;
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm(): void {
    combineLatest([this.payload$, this.index$])
      .pipe(
        first(),
        switchMap(([payload, index]) => {
          return this.aerService.postTaskSave(
            payload.aer.monitoringApproachEmissions,
            undefined,
            getCompletionStatus('CALCULATION_PFC', payload, index, true),
            'CALCULATION_PFC',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
  }

  onDelete() {
    const nextStep = '../delete';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }

  private navigateNext() {
    const nextStep = '../..';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }
}
