import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { BlockHourProceduresFormProvider } from '../block-hour-procedures-form.provider';
import { FuelUpliftSupplierRecordsComponent } from './fuel-uplift-supplier-records.component';

describe('FuelUpliftSupplierRecordsComponent', () => {
  let result: RenderResult<FuelUpliftSupplierRecordsComponent>;

  beforeEach(async () => {
    result = await render(FuelUpliftSupplierRecordsComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: BlockHourProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
      imports: [ReactiveFormsModule, ReturnToLinkComponent],
      componentProperties: {
        form: new FormGroup({
          fuelUpliftSupplierRecordType: new FormControl<'FUEL_DELIVERY_NOTES' | 'FUEL_INVOICES'>(null),
        }),
      },
    });
  });

  it('should create', () => {
    const {
      fixture: { componentInstance: component },
    } = result;

    expect(component).toBeTruthy();
  });
});
