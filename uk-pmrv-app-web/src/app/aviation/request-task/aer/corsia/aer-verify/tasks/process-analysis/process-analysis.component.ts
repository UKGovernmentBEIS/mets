import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { ProcessAnalysisFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/process-analysis/process-analysis-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-process-analysis',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, RouterLink],
  templateUrl: './process-analysis.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcessAnalysisComponent {
  form = new FormGroup(
    {
      verificationActivities: this.formProvider.verificationActivitiesCtrl,
    },
    { updateOn: 'change' },
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: ProcessAnalysisFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate)
      .saveAerVerify(
        {
          processAnalysis: {
            ...this.formProvider.getFormValue(),
            ...this.form.getRawValue(),
          },
        },
        'in progress',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['strategic-analysis'], { relativeTo: this.route }));
  }
}
