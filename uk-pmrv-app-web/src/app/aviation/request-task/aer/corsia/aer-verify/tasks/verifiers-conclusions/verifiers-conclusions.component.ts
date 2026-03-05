import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { VerifiersConclusionsFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifiers-conclusions/verifiers-conclusions-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { MaterialityThresholdTypePipe } from '@aviation/shared/pipes/materiality-threshold-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-verifiers-conclusions',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, RouterLink, MaterialityThresholdTypePipe],
  templateUrl: './verifiers-conclusions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifiersConclusionsComponent {
  form = new FormGroup(
    {
      dataQualityMateriality: this.formProvider.dataQualityMaterialityCtrl,
      materialityThresholdType: this.formProvider.materialityThresholdTypeCtrl,
      materialityThresholdMet: this.formProvider.materialityThresholdMetCtrl,
    },
    { updateOn: 'change' },
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: VerifiersConclusionsFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate)
      .saveAerVerify(
        {
          verifiersConclusions: {
            ...this.formProvider.getFormValue(),
            ...this.form.getRawValue(),
          },
        },
        'in progress',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['emissions-report'], { relativeTo: this.route }));
  }
}
