import { Component, viewChild } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup, FormsModule, NgForm, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import { GovukValidators } from '../error-message/govuk-validators';
import { TextInputComponent } from '../text-input/text-input.component';
import { ErrorSummaryComponent } from './error-summary.component';

describe('ErrorSummaryComponent', () => {
  @Component({
    imports: [ErrorSummaryComponent, ReactiveFormsModule, TextInputComponent, FormsModule],
    template: `
      @if (isTemplate) {
        <form #templateForm="ngForm">
          @if (showErrorSummary) {
            <govuk-error-summary [form]="templateForm" />
          }
          <select [(ngModel)]="selectValue" name="someField" required></select>
        </form>
      } @else {
        <form [formGroup]="form">
          @if (showErrorSummary) {
            <govuk-error-summary [form]="form" />
          }
          <div govuk-text-input inputType="text" formControlName="topLevelFirst"></div>
          <div formGroupName="secondLevelTopGroup">
            <div govuk-text-input inputType="text" formControlName="secondLevelFirst"></div>
            <div govuk-text-input inputType="text" formControlName="secondLevelSecond"></div>
          </div>
          <div formArrayName="secondLevelSimpleArrayTop">
            <div govuk-text-input inputType="text" [formControlName]="0"></div>
            <div govuk-text-input inputType="text" [formControlName]="1"></div>
          </div>
          <div formArrayName="secondLevelMixedArrayTop">
            <div [formGroupName]="0">
              <div govuk-text-input inputType="text" formControlName="nestedArrayControl1"></div>
            </div>
            <div govuk-text-input inputType="text" [formControlName]="1"></div>
            <div [formGroupName]="2">
              <div govuk-text-input inputType="text" formControlName="nestedArrayControl2"></div>
            </div>
          </div>
          <div govuk-text-input inputType="text" formControlName="topLevelLast"></div>
        </form>
      }
    `,
  })
  class TestComponent {
    public readonly testForm = viewChild<NgForm>('templateForm');

    form: FormGroup;
    isTemplate = false;
    selectValue: any;
    showErrorSummary = false;
  }

  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  const reactiveForm = new FormGroup({
    topLevelFirst: new FormControl('', [
      GovukValidators.required('Enter topLevelFirst'),
      GovukValidators.positiveNumber('Must be a positive number'),
    ]),
    topLevelLast: new FormControl(null, GovukValidators.required('Enter topLevelLast')),
    secondLevelTopGroup: new FormGroup({
      secondLevelFirst: new FormControl(null, GovukValidators.required('Enter secondLevelFirst')),
      secondLevelSecond: new FormControl(null, GovukValidators.required('Enter secondLevelSecond')),
    }),
    secondLevelSimpleArrayTop: new FormArray([
      new FormControl(null, GovukValidators.required('Enter 2ndLevelSimpleArray control 0')),
      new FormControl(null, GovukValidators.required('Enter 2ndLevelSimpleArray control 1')),
    ]),
    secondLevelMixedArrayTop: new FormArray([
      new FormGroup({
        nestedArrayControl1: new FormControl(
          null,
          GovukValidators.required('Enter 2ndLevelMixedArray 0 nestedControl1'),
        ),
      }),
      new FormControl(null, GovukValidators.required('Enter 2ndLevelMixedArray 1 arrayControl')),
      new FormGroup({
        nestedArrayControl2: new FormControl(
          null,
          GovukValidators.required('Enter 2ndLevelMixedArray 2 nestedControl2'),
        ),
      }),
    ]),
  });

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([]), TestComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
  });

  describe('with reactiveForm', () => {
    beforeEach(() => {
      hostComponent.form = reactiveForm;
      fixture.detectChanges();
      hostComponent.showErrorSummary = true;
      fixture.detectChanges();
    });

    it('should create', () => {
      const errorSummaryComponent = fixture.debugElement.query(By.directive(ErrorSummaryComponent)).componentInstance;
      expect(errorSummaryComponent).toBeTruthy();
    });

    it('should display reactive form errors in expected order', () => {
      const hostElement: HTMLElement = fixture.nativeElement;
      const errors = hostElement.querySelectorAll('a');

      expect(Array.from(errors).map((error) => error.href)).toEqual([
        'http://localhost/#l.topLevelFirst',
        'http://localhost/#l.topLevelFirst',
        'http://localhost/#l.topLevelLast',
        'http://localhost/#l.secondLevelTopGroup.secondLevelFirst',
        'http://localhost/#l.secondLevelTopGroup.secondLevelSecond',
        'http://localhost/#l.secondLevelSimpleArrayTop.0',
        'http://localhost/#l.secondLevelSimpleArrayTop.1',
        'http://localhost/#l.secondLevelMixedArrayTop.0.nestedArrayControl1',
        'http://localhost/#l.secondLevelMixedArrayTop.1',
        'http://localhost/#l.secondLevelMixedArrayTop.2.nestedArrayControl2',
      ]);

      expect(errors.length).toEqual(10);
      expect(Array.from(errors).map((error) => error.textContent.trim())).toEqual([
        'Must be a positive number',
        'Enter topLevelFirst',
        'Enter topLevelLast',
        'Enter secondLevelFirst',
        'Enter secondLevelSecond',
        'Enter 2ndLevelSimpleArray control 0',
        'Enter 2ndLevelSimpleArray control 1',
        'Enter 2ndLevelMixedArray 0 nestedControl1',
        'Enter 2ndLevelMixedArray 1 arrayControl',
        'Enter 2ndLevelMixedArray 2 nestedControl2',
      ]);
    });

    it('should prefix the title with Error', () => {
      expect(document.title).toContain('Error');
    });

    it('should focus on error container', () => {
      const hostElement: HTMLElement = fixture.nativeElement;
      const errorContainer = hostElement.querySelector<HTMLDivElement>('.govuk-error-summary');
      expect(document.activeElement).toBe(errorContainer);
    });
  });

  describe('with templateForm', () => {
    it('should display template form errors', fakeAsync(async () => {
      hostComponent.isTemplate = true;
      fixture.detectChanges();
      hostComponent.showErrorSummary = true;
      await fixture.whenStable();
      fixture.detectChanges();

      const hostElement: HTMLElement = fixture.nativeElement;

      expect(hostElement.querySelectorAll<HTMLAnchorElement>('a').length).toEqual(1);
    }));
  });
});
