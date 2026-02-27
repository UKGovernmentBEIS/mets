import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { AlrActionService } from '@actions/alr/core/alr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import {
  ALRApplicationAcceptedRequestActionPayload,
  ALRApplicationAcceptedWithCorrectionsRequestActionPayload,
  ALRApplicationAuthorityReviewOutcome,
  ALRApplicationRejectedRequestActionPayload,
} from 'pmrv-api';

interface ViewModel {
  submissionDate: ALRApplicationAuthorityReviewOutcome['submissionDate'];
}

@Component({
  selector: 'app-alr-action-date-submitted',
  imports: [ActionSharedModule, SharedModule, NgIf],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-action-task header="Provide the date application was submitted to UK authorities" [breadcrumb]="true">
        <dl govuk-summary-list>
          <div govukSummaryListRow>
            <dt govukSummaryListRowKey>When was the relevant information submitted to the authority?</dt>
            <dd govukSummaryListRowValue>{{ vm.submissionDate | govukDate }}</dd>
          </div>
        </dl>
      </app-action-task>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrActionDateSubmittedComponent {
  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const submissionDate = payload.authorityReviewOutcome.submissionDate;

    return { submissionDate };
  });

  private readonly payload = this.alrActionService.payload as Signal<
    | ALRApplicationAcceptedRequestActionPayload
    | ALRApplicationAcceptedWithCorrectionsRequestActionPayload
    | ALRApplicationRejectedRequestActionPayload
  >;

  constructor(private readonly alrActionService: AlrActionService) {}
}
