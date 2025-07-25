import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormBuilder, FormGroup, FormGroupName } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { TemporarySuspension } from 'pmrv-api';

import { TemporarySuspensionComponent } from './temporary-suspension.component';

describe('TemporarySuspensionComponent', () => {
  let component: TemporarySuspensionComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  @Component({
    template: `
      <form [formGroup]="form">
        <app-temporary-suspension formGroupName="notification" [today]="today"></app-temporary-suspension>
      </form>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush,
  })
  class TestComponent {
    today = new Date();
    form = new FormGroup({
      notification: new FormBuilder().group(TemporarySuspensionComponent.controlsFactory(null)),
    });
  }

  const notification: TemporarySuspension = {
    description: 'description',
    type: 'TEMPORARY_SUSPENSION',
  };

  class Page extends BasePage<TestComponent> {
    set startDateDay(value: string) {
      this.setInputValue('#notification.startDateOfNonCompliance-day', value);
    }
    set startDateMonth(value: string) {
      this.setInputValue('#notification.startDateOfNonCompliance-month', value);
    }
    set startDateYear(value: string) {
      this.setInputValue('#notification.startDateOfNonCompliance-year', value);
    }
    set endDateDay(value: string) {
      this.setInputValue('#notification.endDateOfNonCompliance-day', value);
    }
    set endDateMonth(value: string) {
      this.setInputValue('#notification.endDateOfNonCompliance-month', value);
    }
    set endDateYear(value: string) {
      this.setInputValue('#notification.endDateOfNonCompliance-year', value);
    }

    get labels() {
      return this.queryAll<HTMLLabelElement>('label');
    }
    get inputs() {
      return this.queryAll<HTMLInputElement>('input');
    }
    get textAreas() {
      return this.queryAll<HTMLTextAreaElement>('textarea');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [TestComponent, TemporarySuspensionComponent],
    })
      .overrideComponent(TemporarySuspensionComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(TemporarySuspensionComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render form labels', () => {
    expect(page.labels.map((label) => label.textContent.trim())).toEqual([
      'Describe the regulated activities which are temporarily suspended',
      'Day',
      'Month',
      'Year',
      'Day',
      'Month',
      'Year',
    ]);
  });

  it('should update values on setValue', () => {
    const nowDay = new Date();
    const futureDate = new Date();
    futureDate.setFullYear(nowDay.getFullYear() + 1);

    hostComponent.form.get('notification.description').setValue(<any>notification.description);
    hostComponent.form.get('notification.startDateOfNonCompliance').setValue(<any>nowDay);
    hostComponent.form.get('notification.endDateOfNonCompliance').setValue(<any>futureDate);
    fixture.detectChanges();

    expect(page.inputs.map((input) => input.value)).toEqual([
      nowDay.getDate().toString(),
      (nowDay.getMonth() + 1).toString(),
      nowDay.getFullYear().toString(),
      futureDate.getDate().toString(),
      (futureDate.getMonth() + 1).toString(),
      futureDate.getFullYear().toString(),
    ]);

    expect(page.textAreas.map((input) => input.value)).toEqual([notification.description]);
  });

  it('should apply field validations', () => {
    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    expect(hostComponent.form.invalid).toBeTruthy();

    const description = hostComponent.form.get('notification.description');
    expect(description.errors).toEqual({ required: 'Enter the regulated activities which are temporarily suspended' });
    hostComponent.form.get('notification.description').setValue(<any>'a'.repeat(10001));
    fixture.detectChanges();
    expect(description.errors).toEqual({
      maxlength: 'Enter up to 10000 characters',
    });

    const startDate = hostComponent.form.get('notification.startDateOfNonCompliance');
    expect(startDate.errors).toEqual({ isEmpty: 'Enter a date' });

    const endDate = hostComponent.form.get('notification.endDateOfNonCompliance');
    expect(endDate.errors).toEqual({ isEmpty: 'Enter a date' });
  });
});
