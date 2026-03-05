import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { ManagementProceduresDataFormComponent } from '../management-procedures-data-form';
import { ManagementProceduresCorsiaFormProvider } from '../management-procedures-form.provider';

@Component({
  selector: 'app-management-procedures-data',
  templateUrl: './management-procedures-data.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, ManagementProceduresDataFormComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresDataComponent {
  form = this.formProvider.dataManagementCtrl;

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: ManagementProceduresCorsiaFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    const file = this.form.get('dataFlowDiagram').value;
    this.store.empCorsiaDelegate
      .saveEmp({ managementProcedures: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empCorsiaDelegate.setManagementProcedures(this.formProvider.getFormValue());
        this.store.empCorsiaDelegate.addEmpAttachment({ [file.uuid]: file.file.name });
        this.router.navigate(['../documentation-record'], {
          relativeTo: this.route,
        });
      });
  }
}
