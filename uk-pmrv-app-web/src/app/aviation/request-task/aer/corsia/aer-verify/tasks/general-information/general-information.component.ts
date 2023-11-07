import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { GeneralInformationFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/general-information/general-information-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-general-information',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, RouterLink],
  templateUrl: './general-information.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GeneralInformationComponent {
  form = this.formProvider.form;

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: GeneralInformationFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate)
      .saveAerVerify(
        {
          generalInformation: this.formProvider.getFormValue(),
        },
        'in progress',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
  }
}
