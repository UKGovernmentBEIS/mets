import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export interface ViewModel {
  isEditable: boolean;
  hideSubmit: boolean;
  overallDecision: OverallVerificationAssessment;
}
@Component({
  selector: 'app-alr-overall-decision-summary',
  standalone: true,
  imports: [AlrTaskSharedModule, SharedModule],
  template: `
    <app-alr-task-review
      returnLink="../../"
      [breadcrumb]="true"
      *ngIf="vm() as vm"
      heading="Check your answers"
      caption="Overall decision">
      <app-shared-overall-decision-summary-template
        [isEditable]="vm.isEditable"
        [overallDecision]="vm.overallDecision"></app-shared-overall-decision-summary-template>
      <div *ngIf="vm.isEditable && !vm.hideSubmit" class="govuk-button-group">
        <button appPendingButton govukButton type="button" (click)="onConfirm()">Confirm and complete</button>
      </div>
    </app-alr-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionSummaryComponent {
  isEditable: Signal<boolean> = this.alrService.isEditable;
  payload: Signal<ALRApplicationVerificationSubmitRequestTaskPayload> = this.alrService.payload;

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
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    this.alrService
      .postVerificationTaskSave(null, true, 'overallDecision')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
