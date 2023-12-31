import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormBuilder, FormGroup, FormGroupName } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { TemporaryFactor } from 'pmrv-api';

import { BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { TemporaryFactorComponent } from './temporary-factor.component';

describe('TemporaryFactorComponent', () => {
  let component: TemporaryFactorComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  @Component({
    template: `
      <form [formGroup]="form">
        <app-temporary-factor formGroupName="notification" [today]="today"></app-temporary-factor>
      </form>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush,
  })
  class TestComponent {
    today = new Date();
    form = new FormGroup({
      notification: new FormBuilder().group(TemporaryFactorComponent.controlsFactory(null)),
    });
  }

  const notification: TemporaryFactor = {
    description: 'description',
    details: 'details',
    inRespectOfMonitoringMethodology: true,
    measures: 'measures',
    proof: 'proof',
    type: 'TEMPORARY_FACTOR',
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
    get inRespectRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="inRespectOfMonitoringMethodology"]');
    }
    get textAreas() {
      return this.queryAll<HTMLTextAreaElement>('textarea');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [TestComponent, TemporaryFactorComponent],
    })
      .overrideComponent(TemporaryFactorComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(TemporaryFactorComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render form labels', () => {
    expect(page.labels.map((label) => label.textContent.trim())).toEqual([
      'Describe the factors preventing compliance',
      'Day',
      'Month',
      'Year',
      'Day',
      'Month',
      'Year',
      'Yes',
      'Provide details of the interim monitoring and reporting methodology adopted',
      'Provide proof of the necessity for a change to the monitoring and reporting methodology',
      'Describe the measures taken to ensure prompt restoration of compliance',
      'No',
    ]);
  });

  it('should update values on setValue', () => {
    const nowDay = new Date();
    const futureDate = new Date();
    futureDate.setFullYear(nowDay.getFullYear() + 1);

    hostComponent.form.get('notification.description').setValue(<any>notification.description);
    hostComponent.form.get('notification.startDateOfNonCompliance').setValue(<any>nowDay);
    hostComponent.form.get('notification.endDateOfNonCompliance').setValue(<any>futureDate);
    page.inRespectRadios[0].click();
    fixture.detectChanges();

    hostComponent.form.get('notification.details').setValue(<any>notification.details);
    hostComponent.form.get('notification.proof').setValue(<any>notification.proof);
    hostComponent.form.get('notification.measures').setValue(<any>notification.measures);
    fixture.detectChanges();

    expect(page.inRespectRadios[0].checked).toBe(true);
    expect(page.inRespectRadios[1].checked).toBe(false);

    expect(page.inputs.map((input) => input.value)).toEqual([
      nowDay.getDate().toString(),
      (nowDay.getMonth() + 1).toString(),
      nowDay.getFullYear().toString(),
      futureDate.getDate().toString(),
      (futureDate.getMonth() + 1).toString(),
      futureDate.getFullYear().toString(),
      'true',
      'false',
    ]);

    expect(page.textAreas.map((input) => input.value)).toEqual([
      notification.description,
      notification.details,
      notification.proof,
      notification.measures,
    ]);
  });

  it('should apply field validations', () => {
    const nowDay = new Date();

    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    expect(hostComponent.form.invalid).toBeTruthy();

    const description = hostComponent.form.get('notification.description');
    expect(description.errors).toEqual({ required: 'Enter the factors preventing compliance' });
    hostComponent.form.get('notification.description').setValue(<any>'a'.repeat(10001));
    fixture.detectChanges();
    expect(description.errors).toEqual({
      maxlength: 'Enter up to 10000 characters',
    });

    const startDate = hostComponent.form.get('notification.startDateOfNonCompliance');
    expect(startDate.errors).toEqual({ isEmpty: 'Enter a date' });

    const endDate = hostComponent.form.get('notification.endDateOfNonCompliance');
    expect(endDate.errors).toEqual({ isEmpty: 'Enter a date' });

    page.endDateYear = nowDay.getFullYear() - 1 + '';
    page.endDateMonth = '1';
    page.endDateDay = '1';
    fixture.detectChanges();
    const endDateOfNonCompliance = hostComponent.form.get('notification.endDateOfNonCompliance');
    expect(endDateOfNonCompliance.errors).toBeFalsy();

    page.inRespectRadios[0].click();
    fixture.detectChanges();

    const details = hostComponent.form.get('notification.details');
    expect(details.errors).toEqual({
      required: 'Enter details of the interim monitoring and reporting methodology adopted',
    });
    hostComponent.form.get('notification.details').setValue(<any>'a'.repeat(10001));
    fixture.detectChanges();
    expect(details.errors).toEqual({
      maxlength: 'Enter up to 10000 characters',
    });

    const proof = hostComponent.form.get('notification.proof');
    expect(proof.errors).toEqual({
      required: 'Enter proof of the necessity for a change to the monitoring and reporting methodology',
    });
    hostComponent.form.get('notification.proof').setValue(<any>'a'.repeat(10001));
    fixture.detectChanges();
    expect(proof.errors).toEqual({
      maxlength: 'Enter up to 10000 characters',
    });

    const measures = hostComponent.form.get('notification.measures');
    expect(measures.errors).toEqual({
      required: 'Enter the measures taken to ensure prompt restoration of compliance',
    });
    hostComponent.form.get('notification.measures').setValue(<any>'a'.repeat(10001));
    fixture.detectChanges();
    expect(measures.errors).toEqual({
      maxlength: 'Enter up to 10000 characters',
    });
  });
});
