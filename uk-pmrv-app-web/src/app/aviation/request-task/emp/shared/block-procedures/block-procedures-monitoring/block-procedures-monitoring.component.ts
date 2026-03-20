import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpBlockOnBlockOffMethodProcedures } from 'pmrv-api';

import { ProcedureFormPageHeaderDirective, ProcedureFormStepComponent } from '../../procedure-form-step';
import { BlockProceduresFormProvider } from '../block-procedures-form.provider';

@Component({
  selector: 'app-block-procedures-monitoring',
  imports: [SharedModule, GovukComponentsModule, ProcedureFormPageHeaderDirective, ProcedureFormStepComponent],
  templateUrl: './block-procedures-monitoring.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BlockProceduresMonitoringComponent {
  form = this.formProvider.form;

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: BlockProceduresFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private backLinkService: BackLinkService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    const data: EmpBlockOnBlockOffMethodProcedures = {
      fuelConsumptionPerFlight: this.form.value as EmpBlockOnBlockOffMethodProcedures['fuelConsumptionPerFlight'],
    };

    this.store.empDelegate
      .saveEmp({ blockOnBlockOffMethodProcedures: data }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empDelegate.setblockOnBlockOffMethodProcedures(data);
        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }
}
