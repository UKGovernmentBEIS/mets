import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { FuelUpliftSupplierRecordTypePipe } from '@aviation/shared/pipes/fuel-uplift-records-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BlockHourProceduresFormProvider } from '../block-hour-procedures-form.provider';

@Component({
  selector: 'app-fuel-uplift-supplier-records',
  templateUrl: './fuel-uplift-supplier-records.component.html',
  standalone: true,
  imports: [SharedModule, GovukComponentsModule, NgIf, NgFor, ReturnToLinkComponent, FuelUpliftSupplierRecordTypePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelUpliftSupplierRecordsComponent {
  form = new FormGroup({
    fuelUpliftSupplierRecordType: this.formProvider.fuelUpliftSupplierRecordType,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: BlockHourProceduresFormProvider,
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
      .saveEmp({ blockHourMethodProcedures: data }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empDelegate.setBlockHourMethodProcedures(data);

        this.router.navigate(['../fuel-density'], {
          relativeTo: this.route,
        });
      });
  }
}
