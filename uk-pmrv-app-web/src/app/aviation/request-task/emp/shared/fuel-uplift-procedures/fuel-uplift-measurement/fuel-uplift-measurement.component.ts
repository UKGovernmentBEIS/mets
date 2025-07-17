import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { empQuery } from '../../emp.selectors';
import { ProcedureFormPageHeaderDirective, ProcedureFormStepComponent } from '../../procedure-form-step';
import { FuelUpliftProceduresFormProvider } from '../fuel-uplift-procedures-form.provider';

@Component({
  selector: 'app-fuel-uplift-measurement',
  templateUrl: './fuel-uplift-measurement.component.html',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgIf,
    NgFor,
    ProcedureFormPageHeaderDirective,
    ProcedureFormStepComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelUpliftMeasurementComponent {
  form = this.formProvider.blockHoursPerFlightCtrl;

  isCorsia$ = this.store.pipe(empQuery.selectIsCorsia);

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: FuelUpliftProceduresFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store.empDelegate
      .saveEmp({ fuelUpliftMethodProcedures: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empDelegate.setFuelUpliftMethodProcedures(this.formProvider.getFormValue());

        this.router.navigate(['assignment-and-adjustment'], {
          relativeTo: this.route,
        });
      });
  }
}
