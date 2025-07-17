import { Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { EmissionSourcesFormModel } from '../../emission-sources-form.model';
import { EmissionSourcesFormProvider } from '../../emission-sources-form.provider';
import { AircraftTypeFormProvider } from '../aircraft-type-form.provider';
import { AircraftTypeFormComponent } from './aircraft-type-form.component';

@Component({
  selector: 'app-mock-parent',
  template: `
    <form [formGroup]="form">
      <app-aircraft-type-form></app-aircraft-type-form>
    </form>
  `,
  standalone: true,
  imports: [ReactiveFormsModule, RouterTestingModule, AircraftTypeFormComponent],
  providers: [
    EmissionSourcesFormProvider,
    AircraftTypeFormProvider,
    { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
  ],
})
class MockParentComponent {
  form = inject<FormGroup<EmissionSourcesFormModel>>(TASK_FORM_PROVIDER);
}
describe('AircraftTypeForm', () => {
  let component: RenderResult<MockParentComponent>;
  beforeEach(async () => {
    component = await render(MockParentComponent);
  });
  it('should empty form header', () => {
    expect(component).toBeTruthy();
    expect(screen.getByText('Aircraft type details')).toBeInTheDocument();
  });
  it('should have no control filled', () => {
    expect(screen.getByText('Choose an aircraft type')).toBeInTheDocument();
    expect(getSubtype()).toHaveValue('');
    expect(getNumberOfAircraft()).toHaveValue('');
    getFuelTypeRadios().forEach((e) => {
      expect(e).not.toBeChecked();
    });
  });
  function getSubtype(): HTMLElement {
    return component.container.querySelector('#subtype');
  }
  function getNumberOfAircraft(): HTMLElement {
    return component.container.querySelector('#numberOfAircrafts');
  }
  function getFuelTypeRadios(): HTMLElement[] {
    const els = [];
    component.container.querySelectorAll<HTMLElement>("input[type='checkbox']").forEach((e) => els.push(e));
    return els;
  }
});
