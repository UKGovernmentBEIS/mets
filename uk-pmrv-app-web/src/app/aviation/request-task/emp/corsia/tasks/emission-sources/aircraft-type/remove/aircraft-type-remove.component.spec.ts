import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { FuelTypesCorsia } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { fireEvent } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { AircraftTypeDetailsCorsia, AircraftTypeInfo } from 'pmrv-api';

import { RequestTaskStore } from '../../../../../../../request-task/store';
import { EmissionSourcesFormModelCorsia } from '../../emission-sources-form.model';
import { EmissionSourcesCorsiaFormProvider } from '../../emission-sources-form.provider';
import { populateForm } from '../aircraft-type.component.spec';
import { AircraftTypeRemoveComponent } from './aircraft-type-remove.component';

const mockAircraftTypeInfo: AircraftTypeInfo = {
  designatorType: 'example designatorType',
  manufacturer: 'example manufacturer',
  model: 'example model',
};
const mockAircraftType: AircraftTypeDetailsCorsia = {
  aircraftTypeInfo: mockAircraftTypeInfo,
  fuelTypes: ['JET_GASOLINE'] as FuelTypesCorsia[],
  numberOfAircrafts: 12,
  subtype: '',
};
@Component({
  selector: 'app-mock-parent',
  template: `
    <form [formGroup]="form">
      <app-aircraft-type-remove></app-aircraft-type-remove>
    </form>
  `,
  standalone: true,
  imports: [ReactiveFormsModule, AircraftTypeRemoveComponent],
  providers: [EmissionSourcesCorsiaFormProvider, { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
})
class MockParentComponent {
  form = inject<FormGroup<EmissionSourcesFormModelCorsia>>(TASK_FORM_PROVIDER);
  route = inject(ActivatedRoute);
  store = inject(RequestTaskStore);
}
describe('AircraftTypeRemove', () => {
  let fixture: ComponentFixture<MockParentComponent>;
  beforeEach(async () => {
    const tb = TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: new Map([['aircraftTypeIndex', 0]]),
            },
          },
        },
      ],
    });
    const store = tb.inject(RequestTaskStore);
    await tb.compileComponents();
    fixture = TestBed.createComponent(MockParentComponent);
    populateForm(store, fixture.componentInstance.form, [mockAircraftType]);
  });
  it('smoke test', () => {
    expect(fixture).toBeTruthy();
  });
  it('should have appropriate title header', () => {
    expect(screen.getByText('Are you sure you want to delete this item?')).toBeInTheDocument();
  });
  it('should have appropriate remove button', () => {
    expect(getRemoveBtn()).toBeInTheDocument();
  });
  it('should remove first aircraft type', () => {
    expect(fixture.componentInstance.form.getRawValue().aircraftTypes.length).toEqual(1);
    fireEvent.click(getRemoveBtn());
    expect(fixture.componentInstance.form.getRawValue().aircraftTypes.length).toEqual(0);
  });
  function getRemoveBtn(): HTMLElement {
    return fixture.debugElement.nativeElement.querySelector('button');
  }
});
