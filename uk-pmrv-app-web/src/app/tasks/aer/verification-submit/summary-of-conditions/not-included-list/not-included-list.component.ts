import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { summaryOfConditionsWizardComplete } from '@tasks/aer/verification-submit/summary-of-conditions/summary-of-conditions.wizard';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-not-included-list',
  template: `
    <app-aer-task-review heading="Approved changes not included in a re-issued permit" returnToLink="../..">
      <app-summary-of-conditions-list
        [isEditable]="isEditable$ | async"
        [list]="list$ | async"
        baseChangeLink="../not-included-list">
        <div *ngIf="(isEditable$ | async) === true" class="govuk-button-group">
          <button (click)="onSubmit()" govukButton type="button">Continue</button>
        </div>
      </app-summary-of-conditions-list>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotIncludedListComponent implements OnInit {
  isEditable$ = this.aerService.isEditable$;
  list$ = this.aerService
    .getPayload()
    .pipe(
      map(
        (payload) =>
          (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.summaryOfConditions
            .approvedChangesNotIncluded,
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
    this.aerService
      .getPayload()
      .pipe(
        first(),
        map(
          (payload) =>
            (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.summaryOfConditions,
        ),
      )
      .subscribe((summaryOfConditionsInfo) => {
        if (summaryOfConditionsWizardComplete(summaryOfConditionsInfo)) {
          this.router.navigate(['../summary'], { relativeTo: this.route });
        } else {
          this.router.navigate(['../identified-changes'], { relativeTo: this.route });
        }
      });
  }
}
