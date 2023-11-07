import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-misstatements-list',
  template: `
    <app-aer-task-review heading="List the uncorrected misstatements" returnToLink="../..">
      <app-misstatements-group
        [isEditable]="isEditable$ | async"
        [uncorrectedMisstatements]="uncorrectedMisstatements$ | async"
        [showGuardQuestion]="false"
        [headingLarge]="false"
        [showCaption]="false"
        baseChangeLink=".."
      >
        <div class="govuk-button-group" *ngIf="isEditable$ | async">
          <button appPendingButton govukButton type="button" (click)="onContinue()">Continue</button>
        </div>
      </app-misstatements-group>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisstatementsListComponent implements PendingRequest {
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

  onContinue(): void {
    this.router.navigate(['../summary'], { relativeTo: this.route }).then();
  }
}
