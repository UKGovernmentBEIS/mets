import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  template: `
    <app-aer-task-review heading="Check your answers" returnToLink="../..">
      <app-misstatements-group
        [isEditable]="isEditable$ | async"
        [uncorrectedMisstatements]="uncorrectedMisstatements$ | async"
        baseChangeLink="..">
        <div *ngIf="(isEditable$ | async) === true" class="govuk-button-group">
          <button appPendingButton govukButton type="button" (click)="onConfirm()">Confirm and complete</button>
        </div>
      </app-misstatements-group>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements OnInit {
  isEditable$ = this.aerService.isEditable$;
  uncorrectedMisstatements$ = (
    this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
  ).pipe(map((payload) => payload.verificationReport.uncorrectedMisstatements));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly backLinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onConfirm() {
    this.aerService
      .postVerificationTaskSave(null, true, 'uncorrectedMisstatements')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
