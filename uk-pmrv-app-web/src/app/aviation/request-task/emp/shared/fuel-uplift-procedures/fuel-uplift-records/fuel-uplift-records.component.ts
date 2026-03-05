import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { FuelUpliftProceduresFormProvider } from '../fuel-uplift-procedures-form.provider';
import { FuelUpliftRecordsFormComponent } from '../fuel-uplift-records-form';

@Component({
  selector: 'app-fuel-uplift-records',
  templateUrl: './fuel-uplift-records.component.html',
  standalone: true,
  imports: [SharedModule, GovukComponentsModule, ReturnToLinkComponent, NgIf, NgFor, FuelUpliftRecordsFormComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelUpliftRecordsComponent {
  form = new FormGroup({
    fuelUpliftSupplierRecordType: this.formProvider.fuelUpliftSupplierRecordTypeCtrl,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: FuelUpliftProceduresFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
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

        this.router.navigate(['../fuel-uplift-density'], {
          relativeTo: this.route,
        });
      });
  }
}
