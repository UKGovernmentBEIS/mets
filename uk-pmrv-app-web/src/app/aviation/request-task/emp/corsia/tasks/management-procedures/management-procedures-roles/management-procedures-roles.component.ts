import { ChangeDetectionStrategy, Component, ElementRef, Inject, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { ManagementProceduresCorsiaFormProvider } from '../management-procedures-form.provider';
import { ManagementProceduresRolesFormComponent } from '../management-procedures-roles-form';

@Component({
  selector: 'app-management-procedures-roles',
  templateUrl: './management-procedures-roles.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, ManagementProceduresRolesFormComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresRolesComponent {
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;

  form = new FormGroup({
    monitoringReportingRoles: this.formProvider.monitoringReportingRolesCtrl,
  });

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
        this.router.navigate(['data-management'], {
          relativeTo: this.route,
        });
      });
  }

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }
}
