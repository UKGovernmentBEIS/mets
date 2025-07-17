import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-list',
  template: `
    <app-aer-task-review heading="Non-conformities with the approved monitoring plan" returnToLink="../../..">
      <app-non-conformities-per-plan-group
        [isEditable]="isEditable$ | async"
        [uncorrectedNonConformities]="uncorrectedNonConformities$ | async"
        [showGuardQuestion]="false"
        [headingLarge]="false"
        [showCaption]="false"
        baseLink="../.."></app-non-conformities-per-plan-group>
      <div class="govuk-button-group" *ngIf="isEditable$ | async">
        <button appPendingButton govukButton type="button" (click)="onContinue()">Continue</button>
      </div>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListComponent {
  isEditable$ = this.aerService.isEditable$;
  uncorrectedNonConformities$ = (
    this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
  ).pipe(map((payload) => payload.verificationReport.uncorrectedNonConformities));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly backLinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    this.router.navigate(['../../previous-year'], { relativeTo: this.route }).then();
  }
}
