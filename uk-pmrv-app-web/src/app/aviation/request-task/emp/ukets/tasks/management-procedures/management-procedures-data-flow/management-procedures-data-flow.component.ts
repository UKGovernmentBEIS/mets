import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { TASK_FORM_PROVIDER } from '../../../../../../request-task/task-form.provider';
import { ManagementProceduresDataFlowFormComponent } from '../management-procedures-data-flow-form/management-procedures-data-flow-form.component';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';

@Component({
  selector: 'app-management-procedures-data-flow',
  templateUrl: './management-procedures-data-flow.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, ManagementProceduresDataFlowFormComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresDataFlowComponent implements OnInit, OnDestroy {
  private backLinkService = inject(BackLinkService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private formProvider = inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);

  form = new FormGroup({
    dataFlowActivities: this.formProvider.dataFlowActivitiesCtrl,
  });

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    const file = this.form.value.dataFlowActivities.diagramAttachmentId;
    this.store.empUkEtsDelegate
      .saveEmp({ managementProcedures: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empUkEtsDelegate.setManagementProcedures(this.formProvider.getFormValue());
        this.store.empUkEtsDelegate.addEmpAttachment({ [file.uuid]: file.file.name });
        this.router.navigate(['../quality-assurance'], { relativeTo: this.route });
      });
  }
}
