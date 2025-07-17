import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { AerMonitoringPlanVersionsComponent } from '@aviation/shared/components/aer/monitoring-plan-versions';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { OpinionStatementFormProvider } from '../opinion-statement-form.provider';

@Component({
  selector: 'app-opinion-statement-changes-form',
  templateUrl: './opinion-statement-changes-form.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, AerMonitoringPlanVersionsComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementChangesFormComponent {
  protected aerMonitoringPlanVersions = (
    this.store.getState().requestTaskItem.requestTask
      .payload as AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload
  ).aerMonitoringPlanVersions;

  protected notCoveredChangesProvided = (
    this.store.getState().requestTaskItem.requestTask
      .payload as AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload
  ).notCoveredChangesProvided;

  protected changesGroup = this.formProvider.changesGroup;
  protected additionalChangesNotCoveredCtrl = this.formProvider.additionalChangesNotCoveredCtrl;

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: OpinionStatementFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    if (this.changesGroup.invalid) return;

    const formValue = this.formProvider.getFormValue();

    (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate)
      .saveAerVerify({ opinionStatement: { ...formValue } }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setOpinionStatement({ ...formValue });
        this.router.navigate(['..', 'site-visit'], { relativeTo: this.route });
      });
  }
}
