import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { ProcedureFormPageHeaderDirective, ProcedureFormStepComponent } from '../../../../shared/procedure-form-step';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';

@Component({
  selector: 'app-management-procedures-data-validation',
  templateUrl: './management-procedures-data-validation.component.html',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref, ProcedureFormStepComponent, ProcedureFormPageHeaderDirective],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresDataValidationComponent implements OnInit, OnDestroy {
  private backLinkService = inject(BackLinkService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private formProvider = inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);

  form = this.formProvider.dataValidationCtrl;

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
        this.router.navigate(['../corrections'], { relativeTo: this.route });
      });
  }
}
