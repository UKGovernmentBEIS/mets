import { Component, DebugElement } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { GovukComponentsModule } from 'govuk-components';

import { HoldingCompanyFormComponent } from './holding-company-form.component';

@Component({
  selector: 'app-mock-parent',
  template: `
    <form [formGroup]="form">
      <app-holding-company-form formGroupName="holdingCompany"></app-holding-company-form>
    </form>
  `,
})
class MockParentComponent {
  form = this.fb.group({
    holdingCompany: this.fb.group({
      name: [null, Validators.required],
      registrationNumber: [null],
      address: this.fb.group({
        line1: [null],
        line2: [null],
        city: [null],
        postcode: [null],
      }),
    }),
  });

  constructor(private fb: FormBuilder) {}
}

describe('HoldingCompanyFormComponent', () => {
  let component: HoldingCompanyFormComponent;
  let fixture: ComponentFixture<MockParentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, FormsModule, ReactiveFormsModule],
      declarations: [MockParentComponent, HoldingCompanyFormComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MockParentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show holding company name field', () => {
    const labels = getFormFieldLabels(fixture);
    const text = labels[0].nativeElement.textContent;

    expect(text).toStrictEqual('Holding company name');
  });

  it('should show registration number field', () => {
    const labels = getFormFieldLabels(fixture);
    const text = labels[1].nativeElement.textContent;

    expect(text).toStrictEqual('Holding company registration number (optional)');
  });

  it('should show company address line 1 field', () => {
    const labels = getFormFieldLabels(fixture);
    const nameLabelText = labels[2].nativeElement.textContent;

    expect(nameLabelText).toStrictEqual('Address line 1');
  });

  it('should show company address line 2 field', () => {
    const labels = getFormFieldLabels(fixture);
    const text = labels[3].nativeElement.textContent;

    expect(text).toStrictEqual('Address line 2 (optional)');
  });

  it('should show city field', () => {
    const labels = getFormFieldLabels(fixture);
    const text = labels[4].nativeElement.textContent;

    expect(text).toStrictEqual('Town or city');
  });

  it('should show post code field', () => {
    const labels = getFormFieldLabels(fixture);
    const nameLabelText = labels[5].nativeElement.textContent;

    expect(nameLabelText).toStrictEqual('Post code');
  });
});

function getFormFieldLabels(fixture: ComponentFixture<MockParentComponent>): DebugElement[] {
  return fixture.debugElement.queryAll(By.css('label'));
}
