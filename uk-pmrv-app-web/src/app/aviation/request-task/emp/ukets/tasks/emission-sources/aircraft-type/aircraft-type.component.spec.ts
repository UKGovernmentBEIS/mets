import { FormControl, FormGroup } from '@angular/forms';

import { render, RenderResult } from '@testing-library/angular';

import { AircraftTypeDetails } from 'pmrv-api';

import { EmpRequestTaskPayloadUkEts, initialState, RequestTaskStore } from '../../../../../../request-task/store';
import { EmissionSourcesFormModel } from '../emission-sources-form.model';
import { EmissionSourcesFormProvider } from '../emission-sources-form.provider';
import { AircraftTypeComponent } from './aircraft-type.component';

export function populateForm(
  store: RequestTaskStore,
  form: FormGroup<EmissionSourcesFormModel>,
  aicraftTypes: AircraftTypeDetails[],
): void {
  store.setState({
    ...initialState,
    requestTaskItem: {
      requestTask: {
        payload: {
          emissionsMonitoringPlan: {
            emissionSources: {
              aircraftTypes: aicraftTypes,
            },
          },
        } as EmpRequestTaskPayloadUkEts,
      },
    },
  });
  aicraftTypes.forEach((at) => {
    form.controls.aircraftTypes.push(
      new FormGroup({
        aircraftTypeInfo: new FormControl(at.aircraftTypeInfo),
        fuelTypes: new FormControl(at.fuelTypes),
        isCurrentlyUsed: new FormControl(at.isCurrentlyUsed),
        numberOfAircrafts: new FormControl(at.numberOfAircrafts),
        subtype: new FormControl(at.subtype),
      }),
    );
  });
}

describe('AircraftTypeComponent', () => {
  let result: RenderResult<AircraftTypeComponent>;

  beforeEach(async () => {
    result = await render(AircraftTypeComponent, {
      providers: [EmissionSourcesFormProvider],
    });
  });

  it('smoke test', () => {
    const {
      fixture: { componentInstance: component },
    } = result;
    expect(component).toBeTruthy();
  });
});
