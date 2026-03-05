import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { EmpEmissionsReductionClaim } from 'pmrv-api';

import { RequestTaskStore } from '../../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { empHeaderTaskMap } from '../../../../shared/util/emp.util';
import { EmissionsReductionClaimFormComponent } from '../emissions-reduction-claim-form';
import { EmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';

@Component({
  selector: 'app-emissions-reduction-claim-page',
  standalone: true,
  imports: [SharedModule, EmissionsReductionClaimFormComponent, ReturnToLinkComponent],
  templateUrl: './emissions-reduction-claim-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class EmissionsReductionClaimPageComponent implements OnInit, OnDestroy {
  private backLinkService = inject(BackLinkService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private formProvider = inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);

  form = new FormGroup({
    exist: this.formProvider.existCtrl,
  });
  emissionsReduction: EmpEmissionsReductionClaim;
  readonly empHeaderTaskMap = empHeaderTaskMap;

  ngOnInit(): void {
    this.backLinkService.show('..');
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    this.emissionsReduction = this.store.empUkEtsDelegate.payload.emissionsMonitoringPlan.emissionsReductionClaim;

    if (this.form.get('exist').value) {
      this.store.empUkEtsDelegate
        .saveEmp({ emissionsReductionClaim: { ...this.emissionsReduction, exist: true } }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.formProvider.setFormValue({ ...this.emissionsReduction, exist: true });
          this.store.empUkEtsDelegate.setEmissionsReductionClaim({ ...this.emissionsReduction, exist: true });
          this.formProvider.addProcedureForms();
          this.router.navigate(['saf-monitoring-systems-processes'], {
            relativeTo: this.route,
            queryParamsHandling: 'merge',
          });
        });
    } else {
      this.store.empUkEtsDelegate
        .saveEmp({ emissionsReductionClaim: { exist: false } }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.formProvider.setFormValue({ exist: false });
          this.store.empUkEtsDelegate.setEmissionsReductionClaim({ exist: false });
          this.formProvider.removeProcedureForms();
          this.router.navigate(['summary'], {
            relativeTo: this.route,
          });
        });
    }
  }
}
