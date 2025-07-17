import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { VerifyEmissionReductionClaimFormComponent } from '../verify-emission-reduction-claim-form';
import { VerifyEmissionsReductionClaimFormProvider } from '../verify-emissions-reduction-claim-form.provider';

@Component({
  selector: 'app-verify-emissions-reduction-claim-page',
  templateUrl: './verify-emissions-reduction-claim-page.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, VerifyEmissionReductionClaimFormComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VerifyEmissionsReductionClaimPageComponent {
  protected form = this.formProvider.form;

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: VerifyEmissionsReductionClaimFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    if (this.form.invalid) return;

    (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate)
      .saveAerVerify({ emissionsReductionClaimVerification: this.form.value }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setEmissionsReductionClaimVerification(
          this.form.value,
        );
        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }
}
