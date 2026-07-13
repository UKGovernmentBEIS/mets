import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { RadioComponent } from './radio.component';
import { RadioOptionComponent } from './radio-option/radio-option.component';

describe('RadioComponent', () => {
  @Component({
    imports: [RadioComponent, ReactiveFormsModule, RadioOptionComponent],
    template: `
      <div govuk-radio [formControl]="control">
        @for (option of options; track option) {
          <govuk-radio-option [value]="option.value" [label]="option.label" />
        }
      </div>

      <form [formGroup]="form">
        <div govuk-radio formControlName="radio">
          @for (option of options; track option) {
            <govuk-radio-option [value]="option.value" [label]="option.label" />
          }
        </div>
      </form>
    `,
  })
  class TestComponent {
    control = new FormControl();
    options = [
      { label: 'First', value: 1 },
      { label: 'Second', value: 2 },
      { label: 'Third', value: 3 },
    ];
    form = new FormGroup(
      { radio: new FormControl(null, { validators: [() => ({ error: true })] }) },
      { updateOn: 'submit' },
    );
  }

  let component: RadioComponent<number>;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [ControlContainer],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    element = fixture.nativeElement;
    component = fixture.debugElement.query(By.directive(RadioComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should disable the radio', () => {
    hostComponent.control.disable();
    hostComponent.form.disable();
    fixture.detectChanges();

    const inputs = element.querySelectorAll('input');

    inputs.forEach((input) => expect(input.disabled).toBeTruthy());
  });

  it('should assign value', () => {
    hostComponent.control.patchValue(1);
    fixture.detectChanges();

    const inputs = element.querySelectorAll('input');

    expect(inputs[0].checked).toEqual(true);
    expect(inputs[1].checked).toEqual(false);
  });

  it('should emit value', () => {
    const inputs = fixture.debugElement.queryAll(By.css('input[type="radio"]'));
    inputs[2].triggerEventHandler('change', { target: inputs[2].nativeElement });
    fixture.detectChanges();

    expect(hostComponent.control.value).toEqual(3);
  });

  it('should display errors only if form is submitted', () => {
    const inputs = fixture.debugElement.queryAll(By.css('input[type="radio"]'));
    inputs[2].triggerEventHandler('input', { target: inputs[2].nativeElement });
    fixture.detectChanges();

    expect(hostComponent.form.value).toEqual({ radio: null });
    expect(element.querySelector('.govuk-error-message')).toBeFalsy();

    element.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(hostComponent.form.get('radio').errors).toBeTruthy();
    expect(element.querySelector('.govuk-error-message')).toBeTruthy();
  });
});
