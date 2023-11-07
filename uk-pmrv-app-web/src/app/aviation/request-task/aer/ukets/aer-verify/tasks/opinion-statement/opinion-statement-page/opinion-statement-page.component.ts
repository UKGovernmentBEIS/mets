import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import OpinionStatementEmissionsFormComponent from '../opinion-statement-emissions-form/opinion-statement-emissions-form.component';
import { OpinionStatementFormProvider } from '../opinion-statement-form.provider';

@Component({
  selector: 'app-opinion-statement-page',
  templateUrl: './opinion-statement-page.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, OpinionStatementEmissionsFormComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementPageComponent {
  protected totalEmissionsProvided = (
    this.store.getState().requestTaskItem.requestTask
      .payload as AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload
  ).totalEmissionsProvided;

  protected emissionsGroup = this.formProvider.emissionsGroup;

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: OpinionStatementFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    if (this.emissionsGroup.invalid) return;

    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate)
      .saveAerVerify({ opinionStatement: { ...this.emissionsGroup.value } }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyStoreDelegate).setOpinionStatement({ ...this.emissionsGroup.value });
        this.router.navigate(['changes-not-covered'], { relativeTo: this.route });
      });
  }
}
