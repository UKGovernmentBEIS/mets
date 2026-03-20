import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';

import { BDRS2ApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export interface ViewModel {
  isEditable: boolean;
  hideSubmit: boolean;
  overallDecision: OverallVerificationAssessment;
}
@Component({
  selector: 'app-bdrs2-overall-decision-summary',
  imports: [BdrS2TaskSharedModule, SharedModule],
  standalone: true,
  template: `
    <app-bdrs2-task-review
      returnToLink="../../"
      [breadcrumb]="true"
      *ngIf="vm() as vm"
      heading="Check your answers"
      caption="Overall decision">
      @if (vm.overallDecision.type === 'VERIFIED_WITH_COMMENTS') {
        <p class="govuk-body">
          You have conducted a verification of the B data reported by this operator in its annual emissions report. On
          the basis of your verification work these data are fairly stated, with the exception of the following reasons.
        </p>
      }
      <app-shared-overall-decision-summary-template
        [isEditable]="vm.isEditable"
        [overallDecision]="vm.overallDecision"></app-shared-overall-decision-summary-template>
      <div *ngIf="vm.isEditable && !vm.hideSubmit" class="govuk-button-group">
        <button appPendingButton govukButton type="button" (click)="onConfirm()">Confirm and complete</button>
      </div>
    </app-bdrs2-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionSummaryComponent {
  isEditable: Signal<boolean> = this.bdrs2Service.isEditable;
  payload: Signal<BDRS2ApplicationVerificationSubmitRequestTaskPayload> = this.bdrs2Service.payload;

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const payload = this.payload();

    return {
      isEditable,
      hideSubmit: !isEditable || payload.verificationSectionsCompleted?.['overallDecision']?.[0],
      overallDecision: payload.verificationReport.overallAssessment as OverallVerificationAssessment,
    };
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    this.bdrs2Service
      .postVerificationTaskSave(null, true, 'overallDecision')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
