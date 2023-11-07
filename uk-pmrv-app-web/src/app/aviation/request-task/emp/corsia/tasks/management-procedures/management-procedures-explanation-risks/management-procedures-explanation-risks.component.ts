import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { ManagementProceduresCorsiaFormProvider } from '../management-procedures-form.provider';

@Component({
  selector: 'app-management-procedures-explanation-risks',
  templateUrl: './management-procedures-explanation-risks.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresExplanationRisksComponent {
  form = this.formProvider.riskExplanationCtrl;

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: ManagementProceduresCorsiaFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store.empCorsiaDelegate
      .saveEmp({ managementProcedures: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empCorsiaDelegate.setManagementProcedures(this.formProvider.getFormValue());
        this.router.navigate(['../revision-emissions-monitoring-plan'], {
          relativeTo: this.route,
        });
      });
  }
}
