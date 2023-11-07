import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { ProcedureFormPageHeaderDirective, ProcedureFormStepComponent } from '../../../../shared/procedure-form-step';
import { monitoringApproachQuery } from '../../monitoring-approach/store/monitoring-approach.selectors';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';

@Component({
  selector: 'app-management-procedures-risks',
  templateUrl: './management-procedures-risks.component.html',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref, ProcedureFormStepComponent, ProcedureFormPageHeaderDirective],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresRisksComponent implements OnInit, OnDestroy {
  private backLinkService = inject(BackLinkService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private formProvider = inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);
  private destroy$ = inject(DestroySubject);

  form = this.formProvider.assessAndControlRisksCtrl;

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
        this.store.pipe(monitoringApproachQuery.selectMonitoringApproach, takeUntil(this.destroy$)).subscribe((ma) => {
          if (ma.monitoringApproachType === 'FUEL_USE_MONITORING') {
            this.formProvider.addRiskAssessmentForm();
            this.router.navigate(['../risk-assessment'], { relativeTo: this.route });
          } else {
            this.formProvider.removeRiskAssessmentForm();
            this.router.navigate(['../environmental-management'], {
              relativeTo: this.route,
            });
          }
        });
      });
  }
}
