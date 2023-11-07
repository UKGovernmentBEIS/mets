import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { FuelTypes } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';

import { AircraftTypeDetails, AircraftTypeInfo } from 'pmrv-api';

import { populateForm } from '../aircraft-type/aircraft-type.component.spec';
import { EmissionSourcesFormModel } from '../emission-sources-form.model';
import { EmissionSourcesFormProvider } from '../emission-sources-form.provider';
import { MonitoringMethodologyComponent } from './monitoring-methodology.component';

const mockAircraftTypeInfo: AircraftTypeInfo = {
  designatorType: 'example designatorType',
  manufacturer: 'example manufacturer',
  model: 'example model',
};
const mockAircraftType: AircraftTypeDetails = {
  aircraftTypeInfo: mockAircraftTypeInfo,
  fuelTypes: ['JET_GASOLINE'] as FuelTypes[],
  isCurrentlyUsed: true,
  numberOfAircrafts: 12,
  subtype: '',
};
@Component({
  selector: 'app-mock-parent',
  template: ` <app-monitoring-methodology></app-monitoring-methodology>`,
  standalone: true,
  imports: [ReactiveFormsModule, MonitoringMethodologyComponent],
  providers: [EmissionSourcesFormProvider],
})
class MockParentComponent {
  form = inject<FormGroup<EmissionSourcesFormModel>>(TASK_FORM_PROVIDER);
  route = inject(ActivatedRoute);
  store = inject(RequestTaskStore);
}
describe('EmissionSourcesPageComponent', () => {
  let fixture: ComponentFixture<MockParentComponent>;

  beforeEach(async () => {
    const tb = TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
    });
    const store = tb.inject(RequestTaskStore);
    await tb.compileComponents();
    fixture = TestBed.createComponent(MockParentComponent);
    populateForm(store, fixture.componentInstance.form, [mockAircraftType]);
  });

  it('smoke test', () => {
    expect(fixture).toBeTruthy();
    expect(screen.getByTestId('monitoring-methodology')).toBeInTheDocument();
  });
});
