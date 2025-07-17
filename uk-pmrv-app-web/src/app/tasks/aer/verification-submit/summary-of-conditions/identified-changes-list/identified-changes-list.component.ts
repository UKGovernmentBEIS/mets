import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-identified-changes-list',
  template: `
    <app-aer-task-review
      heading="Changes not reported to the regulator by the end of the reporting period?"
      returnToLink="../..">
      <p class="govuk-body">
        You should list anything that has been agreed in letters, emails or phone calls but not written into the plan.
        This could include things like new technical units, new processes or notification of closures.
      </p>
      <app-summary-of-conditions-list
        [isEditable]="isEditable$ | async"
        [list]="list$ | async"
        baseChangeLink="../identified-changes-list">
        <div *ngIf="(isEditable$ | async) === true" class="govuk-button-group">
          <button (click)="onSubmit()" govukButton type="button">Continue</button>
        </div>
      </app-summary-of-conditions-list>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IdentifiedChangesListComponent implements OnInit {
  isEditable$ = this.aerService.isEditable$;
  list$ = this.aerService
    .getPayload()
    .pipe(
      map(
        (payload) =>
          (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.summaryOfConditions
            .notReportedChanges,
      ),
    );

  constructor(
    private readonly aerService: AerService,
    private readonly backLinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit(): void {
    this.router.navigate(['../summary'], { relativeTo: this.route });
  }
}
