import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormBuilder, FormGroup, FormGroupName } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { CessationNotification } from 'pmrv-api';

import { PermanentCessationComponent } from './permanent-cessation.component';

describe('PermanentCessationComponent', () => {
  let component: PermanentCessationComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  @Component({
    template: `
      <form [formGroup]="form">
        <app-permanent-cessation formGroupName="notification" [today]="today" [form]="form"></app-permanent-cessation>
      </form>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush,
  })
  class TestComponent {
    today = new Date();
    form = new FormGroup({
      notification: new FormBuilder().group(PermanentCessationComponent.controlsFactory(null)),
    });
  }

  const notification: CessationNotification = {
    description: 'description',
    isTemporary: true,
    type: 'CESSATION',
    technicalCapabilityDetails: {
      technicalCapability: 'RESUME_REG_ACTIVITIES_WITHOUT_PHYSICAL_CHANGES',
      details: 'details',
    },
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
      declarations: [TestComponent, PermanentCessationComponent],
    })
      .overrideComponent(PermanentCessationComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(PermanentCessationComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render form labels', () => {
    expect(page.labels.map((label) => label.textContent.trim())).toEqual([
      'Describe the cessation of regulated activities',
      'Day',
      'Month',
      'Year',
      'Yes',
      'No',
    ]);
  });

  it('should update values on setValue', () => {
    const nowDay = new Date();
    const futureDate = new Date();
    futureDate.setFullYear(nowDay.getFullYear() + 1);

    hostComponent.form.get('notification.description').setValue(<any>notification.description);
    hostComponent.form.get('notification.startDateOfNonCompliance').setValue(<any>nowDay);
    fixture.detectChanges();

    expect(page.inputs.map((input) => input.value)).toEqual([
      nowDay.getDate().toString(),
      (nowDay.getMonth() + 1).toString(),
      nowDay.getFullYear().toString(),
      'true',
      'false',
    ]);

    expect(page.textAreas.map((input) => input.value)).toEqual([notification.description]);
  });

  it('should show the endDateOfNonCompliance on click yes', () => {
    const nowDay = new Date();
    const futureDate = new Date();
    futureDate.setFullYear(nowDay.getFullYear() + 1);

    hostComponent.form.get('notification.description').setValue(<any>notification.description);
    hostComponent.form.get('notification.startDateOfNonCompliance').setValue(<any>nowDay);
    hostComponent.form.get('notification.endDateOfNonCompliance').setValue(<any>futureDate);
    page.inputs[3].click();
    fixture.detectChanges();

    hostComponent.form
      .get('notification.technicalCapabilityDetails.RESUME_REG_ACTIVITIES_WITHOUT_PHYSICAL_CHANGES_details')
      .setValue(<never>notification.technicalCapabilityDetails.details);
    fixture.detectChanges();

    expect(page.inputs.map((input) => input.value)).toEqual([
      nowDay.getDate().toString(),
      (nowDay.getMonth() + 1).toString(),
      nowDay.getFullYear().toString(),
      'true',
      'false',
      futureDate.getDate().toString(),
      (futureDate.getMonth() + 1).toString(),
      futureDate.getFullYear().toString(),
      'RESUME_REG_ACTIVITIES_WITHOUT_PHYSICAL_CHANGES',
      'RESTORE_TECHNICAL_CAPABILITY_TO_RESUME_REG_ACTIVITIES',
    ]);

    expect(page.textAreas.map((input) => input.value)).toEqual([
      notification.description,
      notification.technicalCapabilityDetails.details,
      '',
    ]);
  });

  it('should apply field validations', () => {
    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    expect(hostComponent.form.invalid).toBeTruthy();

    const description = hostComponent.form.get('notification.description');
    expect(description.errors).toEqual({ required: 'Enter a description of the cessation' });
    hostComponent.form.get('notification.description').setValue(<any>'a'.repeat(10001));
    fixture.detectChanges();
    expect(description.errors).toEqual({
      maxlength: 'Enter up to 10000 characters',
    });

    page.inputs[3].click();
    fixture.detectChanges();

    const startDate = hostComponent.form.get('notification.startDateOfNonCompliance');
    expect(startDate.errors).toEqual({ isEmpty: 'Enter the date of cessation' });

    const endDate = hostComponent.form.get('notification.endDateOfNonCompliance');
    expect(endDate.errors).toEqual({ isEmpty: 'Enter the date that activities are expected to resume' });
  });
});
