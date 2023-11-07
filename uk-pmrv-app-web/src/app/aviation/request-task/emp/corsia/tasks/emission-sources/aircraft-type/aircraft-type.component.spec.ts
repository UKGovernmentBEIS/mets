import { FormControl, FormGroup } from '@angular/forms';

import { EmpRequestTaskPayloadCorsia, initialState, RequestTaskStore } from '@aviation/request-task/store';
import { render, RenderResult } from '@testing-library/angular';

import { AircraftTypeDetailsCorsia } from 'pmrv-api';

import { EmissionSourcesFormModelCorsia } from '../emission-sources-form.model';
import { EmissionSourcesCorsiaFormProvider } from '../emission-sources-form.provider';
import { AircraftTypeComponent } from './aircraft-type.component';

export function populateForm(
  store: RequestTaskStore,
  form: FormGroup<EmissionSourcesFormModelCorsia>,
  aicraftTypes: AircraftTypeDetailsCorsia[],
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
        } as EmpRequestTaskPayloadCorsia,
      },
    },
  });
  aicraftTypes.forEach((at) => {
    form.controls.aircraftTypes.push(
      new FormGroup({
        aircraftTypeInfo: new FormControl(at.aircraftTypeInfo),
        fuelTypes: new FormControl(at.fuelTypes),
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
      providers: [EmissionSourcesCorsiaFormProvider],
    });
  });

  it('smoke test', () => {
    const {
      fixture: { componentInstance: component },
    } = result;
    expect(component).toBeTruthy();
  });
});
