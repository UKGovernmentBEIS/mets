import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { VerifierDetailsFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifier-details/verifier-details-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { VerificationBodyDetailsInfoTemplateComponent } from '@aviation/shared/components/aer-verify/verification-body-details-info-template/verification-body-details-info-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-verifier-details',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, VerificationBodyDetailsInfoTemplateComponent],
  templateUrl: './verifier-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierDetailsComponent {
  form = this.formProvider.verificationTeamLeaderGroup;
  verificationBodyDetails$ = this.store.pipe(aerVerifyCorsiaQuery.selectVerificationBodyDetails);

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: VerifierDetailsFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate)
      .saveAerVerify(
        {
          verifierDetails: {
            ...this.formProvider.getFormValue(),
            verificationTeamLeader: this.form.value,
          },
        },
        'in progress',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['conflict-interest'], { relativeTo: this.route }));
  }
}
