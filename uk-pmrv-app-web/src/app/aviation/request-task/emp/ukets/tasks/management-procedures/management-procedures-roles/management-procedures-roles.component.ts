import {
  AfterContentChecked,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  inject,
  ViewChild,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { EmpManagementProcedures, EmpMonitoringReportingRoles } from 'pmrv-api';

import { RequestTaskStore } from '../../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';
import { ManagementProceduresRolesFormComponent } from '../management-procedures-roles-form/management-procedures-roles-form.component';

@Component({
  selector: 'app-management-procedures-roles',
  templateUrl: './management-procedures-roles.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, ManagementProceduresRolesFormComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresRolesComponent implements AfterContentChecked {
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;

  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private formProvider = inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);

  form = new FormGroup({
    monitoringReportingRoles: this.formProvider.monitoringReportingRolesCtrl,
  });
  managementProcedures: EmpManagementProcedures;

  constructor(private cd: ChangeDetectorRef) {}

  ngAfterContentChecked(): void {
    this.cd.detectChanges();
  }

  onSubmit() {
    this.managementProcedures = this.store.empUkEtsDelegate.payload.emissionsMonitoringPlan.managementProcedures;
    this.store.empUkEtsDelegate
      .saveEmp(
        {
          managementProcedures: {
            ...this.managementProcedures,
            monitoringReportingRoles: this.form.value as EmpMonitoringReportingRoles,
          } as any,
        },
        'in progress',
      )
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empUkEtsDelegate.setManagementProcedures({
          ...this.managementProcedures,
          monitoringReportingRoles: this.form.value as EmpMonitoringReportingRoles,
        });
        this.router.navigate(['documentation'], { relativeTo: this.route });
      });
  }

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }
}
