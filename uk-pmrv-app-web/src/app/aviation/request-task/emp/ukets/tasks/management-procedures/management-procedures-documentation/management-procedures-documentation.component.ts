import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { RequestTaskStore } from '../../../../../../request-task/store';
import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { ProcedureFormPageHeaderDirective, ProcedureFormStepComponent } from '../../../../shared/procedure-form-step';
import { monitoringApproachQuery } from '../../monitoring-approach/store/monitoring-approach.selectors';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';

@Component({
  selector: 'app-management-procedures-documentation',
  templateUrl: './management-procedures-documentation.component.html',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref, ProcedureFormStepComponent, ProcedureFormPageHeaderDirective],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresDocumentationComponent implements OnInit, OnDestroy {
  private backLinkService = inject(BackLinkService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private formProvider = inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);
  private destroy$ = inject(DestroySubject);

  form = this.formProvider.recordKeepingAndDocumentationCtrl;

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    this.store.empUkEtsDelegate
      .saveEmp({ managementProcedures: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empUkEtsDelegate.setManagementProcedures(this.formProvider.getFormValue());
        // if monitoringApproachType is not set, then EUROCONTROL_SUPPORT_FACILITY is considered as the default value
        this.store.pipe(monitoringApproachQuery.selectMonitoringApproach, takeUntil(this.destroy$)).subscribe((ma) => {
          if (
            ma?.monitoringApproachType === 'EUROCONTROL_SMALL_EMITTERS' ||
            ma?.monitoringApproachType === 'FUEL_USE_MONITORING'
          ) {
            this.formProvider.addSmallEmittersForms();
            this.router.navigate(['../responsibilities'], { relativeTo: this.route });
          } else {
            this.formProvider.removeSmallEmittersForms();
            this.router.navigate(['../summary'], { relativeTo: this.route });
          }
        });
      });
  }
}
