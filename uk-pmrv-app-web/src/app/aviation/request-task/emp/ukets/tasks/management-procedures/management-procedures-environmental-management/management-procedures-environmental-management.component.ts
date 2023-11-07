import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { EmpManagementProcedures } from 'pmrv-api';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { ManagementProceduresEnvironmentalManagementFormComponent } from '../management-procedures-environmental-management-form';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';

@Component({
  selector: 'app-management-procedures-environmental-management',
  templateUrl: './management-procedures-environmental-management.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, ManagementProceduresEnvironmentalManagementFormComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresEnvironmentalManagementComponent implements OnInit, OnDestroy {
  private backLinkService = inject(BackLinkService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private store = inject(RequestTaskStore);
  formProvider = inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER);

  form = new FormGroup({
    environmentalManagementSystem: this.formProvider.environmentalManagementSystemCtrl,
  });
  managementProcedures: EmpManagementProcedures;

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    this.formProvider.form.updateValueAndValidity();
    this.managementProcedures = this.store.empUkEtsDelegate.payload.emissionsMonitoringPlan.managementProcedures;
    this.store.empUkEtsDelegate
      .saveEmp(
        {
          managementProcedures: {
            ...this.managementProcedures,
            environmentalManagementSystem: this.form.value.environmentalManagementSystem,
          } as any,
        },
        'in progress',
      )
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empUkEtsDelegate.setManagementProcedures({
          ...this.managementProcedures,
          environmentalManagementSystem: this.form.value.environmentalManagementSystem,
        });
        this.router.navigate(['../summary'], { relativeTo: this.route });
      });
  }
}
