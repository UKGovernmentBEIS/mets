import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { empQuery } from '../../emp.selectors';
import { FuelUpliftProceduresFormProvider } from '../fuel-uplift-procedures-form.provider';

@Component({
  selector: 'app-fuel-uplift-assignment',
  templateUrl: './fuel-uplift-assignment.component.html',
  standalone: true,
  imports: [SharedModule, GovukComponentsModule, ReturnToLinkComponent, NgIf, NgFor],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelUpliftAssignmentComponent {
  form = new FormGroup({
    zeroFuelUplift: this.formProvider.zeroFuelUpliftCtrl,
  });
  isCorsia = toSignal(this.store.pipe(empQuery.selectIsCorsia));

  constructor(
    @Inject(TASK_FORM_PROVIDER) private readonly formProvider: FuelUpliftProceduresFormProvider,
    private readonly store: RequestTaskStore,
    private readonly pendingRequestService: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    const data = {
      ...this.formProvider.getFormValue(),
      ...this.form.value,
    };

    this.store.empDelegate
      .saveEmp({ fuelUpliftMethodProcedures: data }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.formProvider.setFormValue(data);
        this.store.empDelegate.setFuelUpliftMethodProcedures(data);

        this.router.navigate(['../fuel-uplift-records'], {
          relativeTo: this.route,
        });
      });
  }
}
