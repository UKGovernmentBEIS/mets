import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { VerifierDetailsFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifier-details/verifier-details-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-conflict-interest',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule],
  templateUrl: './conflict-interest.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConflictInterestComponent {
  form = this.formProvider.interestConflictAvoidanceGroup;

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
            interestConflictAvoidance: this.form.value,
          },
        },
        'in progress',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route }));
  }
}
