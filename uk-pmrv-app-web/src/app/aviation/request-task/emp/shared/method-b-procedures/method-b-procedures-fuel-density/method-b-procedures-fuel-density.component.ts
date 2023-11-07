import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { ProcedureFormStepComponent } from '../../procedure-form-step';
import { MethodBProceduresFormProvider } from '../method-b-procedures-form.provider';

@Component({
  selector: 'app-method-b-procedures-fuel-density',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, ProcedureFormStepComponent],
  templateUrl: './method-b-procedures-fuel-density.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class MethodBProceduresFuelDensityComponent implements OnInit, OnDestroy {
  private backLinkService = inject(BackLinkService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private formProvider = inject<MethodBProceduresFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);

  form = this.formProvider.fuelDensityCtrl;

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onContinue() {
    this.store.empDelegate
      .saveEmp({ methodBProcedures: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empDelegate.setMethodBProcedures(this.formProvider.getFormValue());

        this.router.navigate(['../summary'], {
          relativeTo: this.route,
        });
      });
  }
}
