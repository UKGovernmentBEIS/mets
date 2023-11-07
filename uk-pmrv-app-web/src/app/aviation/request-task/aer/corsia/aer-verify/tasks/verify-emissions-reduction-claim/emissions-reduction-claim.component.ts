import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';

import { EmissionsReductionClaimFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-emissions-reduction-claim/emissions-reduction-claim-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-emissions-reduction-claim',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, RouterLink, RouterLinkActive],
  templateUrl: './emissions-reduction-claim.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimComponent {
  form = this.formProvider.form;

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: EmissionsReductionClaimFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate)
      .saveAerVerify(
        {
          emissionsReductionClaimVerification: {
            ...this.formProvider.getFormValue(),
          },
        },
        'in progress',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
  }
}
