import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { EtsComplianceRulesFormComponent } from '../ets-compliance-rules-form';
import { EtsComplianceRulesFormProvider } from '../ets-compliance-rules-form.provider';

@Component({
  selector: 'app-ets-compliance-rules-page',
  templateUrl: './ets-compliance-rules-page.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, EtsComplianceRulesFormComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class EtsComplianceRulesPageComponent {
  protected form = this.formProvider.form;

  readonly aerVerifyHeaderTaskMap = aerVerifyHeaderTaskMap;

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: EtsComplianceRulesFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    if (this.form.invalid) return;

    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate)
      .saveAerVerify({ etsComplianceRules: this.form.value }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyStoreDelegate).setEtsComplianceRules(this.form.value);
        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }
}
