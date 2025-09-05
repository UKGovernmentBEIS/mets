import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { GovukComponentsModule } from 'govuk-components';

import { PageHeadingComponent } from '../page-heading/page-heading.component';
import { WizardStepComponent } from './wizard-step.component';

describe('WizardStepComponent', () => {
  let component: WizardStepComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-wizard-step
        [formGroup]="formGroup"
        heading="Some form"
        caption="Some caption"
        [cancelLink]="cancelLink"
        (formSubmit)="onSubmit($event)">
        <div govuk-text-input formControlName="date" label="Date"></div>
        <div govuk-text-input formControlName="text" label="Text"></div>
      </app-wizard-step>
    `,
  })
  class TestComponent {
    formGroup = new FormGroup({
      date: new FormControl('', [Validators.required]),
      text: new FormControl(''),
    });
    cancelLink: string;
    onSubmit: (form: FormGroup) => any | jest.SpyInstance<void, [FormGroup]>;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, GovukComponentsModule, RouterTestingModule],
      declarations: [WizardStepComponent, TestComponent, PageHeadingComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(WizardStepComponent)).componentInstance;
    hostComponent.onSubmit = jest.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the nested form', () => {
    expect(element.querySelector('.govuk-caption-l')).toBeTruthy();
    expect(element.querySelector('.govuk-heading-l')).toBeTruthy();
    expect(element.querySelector('button[type="submit"]')).toBeTruthy();
    expect(element.querySelectorAll('input').length).toEqual(2);
  });

  it('should render errors on submit', () => {
    expect(element.querySelector('.govuk-error-summary')).toBeFalsy();

    element.querySelector<HTMLButtonElement>('button[type="submit"]').click();

    fixture.detectChanges();

    expect(element.querySelector('.govuk-error-summary')).toBeTruthy();
    expect(hostComponent.onSubmit).not.toHaveBeenCalled();
  });

  it('should render the cancel button when cancelLink is provided', () => {
    hostComponent.cancelLink = '/cancel-path';
    fixture.detectChanges();

    const cancelLink = element.querySelector('div.govuk-button-group > a.govuk-link');
    expect(cancelLink).toBeTruthy();
    expect(cancelLink.getAttribute('href')).toBe('/cancel-path');
  });

  it('should not render the cancel button when cancelLink is not provided', () => {
    hostComponent.cancelLink = undefined;
    fixture.detectChanges();

    const cancelLink = element.querySelector('div.govuk-button-group > a.govuk-link');
    expect(cancelLink).toBeFalsy();
  });
});
