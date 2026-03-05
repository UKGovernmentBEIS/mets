import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { ProcedureFormPageHeaderDirective, ProcedureFormStepComponent } from '../../../../shared/procedure-form-step';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';

@Component({
  selector: 'app-management-procedures-uplift-quantity',
  templateUrl: './management-procedures-uplift-quantity.component.html',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref, ProcedureFormStepComponent, ProcedureFormPageHeaderDirective],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresUpliftQuantityComponent implements OnInit, OnDestroy {
  private readonly backLinkService = inject(BackLinkService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly pendingRequestService = inject(PendingRequestService);
  private readonly formProvider = inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER);
  private readonly store = inject(RequestTaskStore);

  form = this.formProvider.upliftQuantityCrossChecksCtrl;

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
        this.router.navigate(['../environmental-management'], { relativeTo: this.route });
      });
  }
}
