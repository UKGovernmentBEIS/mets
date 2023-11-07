import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { BlockHourProceduresFormProvider } from '../block-hour-procedures-form.provider';
import { SpecificBurnFuelCalculationComponent } from './specific-burn-fuel-calculation.component';

describe('SpecificBurnFuelCalculationComponent', () => {
  let result: RenderResult<SpecificBurnFuelCalculationComponent>;

  beforeEach(async () => {
    result = await render(SpecificBurnFuelCalculationComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: BlockHourProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
      imports: [ReactiveFormsModule, ReturnToLinkComponent],
      componentProperties: {
        form: new FormGroup({
          fuelBurnCalculationTypes: new FormControl<Array<'CLEAR_DISTINGUISHION' | 'NOT_CLEAR_DISTINGUISHION'>>([
            'NOT_CLEAR_DISTINGUISHION',
          ]),
          clearDistinguishionIcaoAircraftDesignators: new FormControl<string>('All'),
          notClearDistinguishionIcaoAircraftDesignators: new FormControl<string>('All'),
          assignmentAndAdjustment: new FormControl<string>('Assignment'),
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
