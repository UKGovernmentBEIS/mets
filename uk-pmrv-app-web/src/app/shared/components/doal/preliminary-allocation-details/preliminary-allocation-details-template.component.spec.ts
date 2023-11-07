import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { PreliminaryAllocationDetailsTemplateComponent } from '@shared/components/doal/preliminary-allocation-details/preliminary-allocation-details-template.component';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

describe('PreliminaryAllocationDetailsTemplateComponent', () => {
  let component: PreliminaryAllocationDetailsTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-preliminary-allocation-details-template
        [form]="formGroup"
        isEditable="true"
        [isEditing]="isEditing"
      ></app-preliminary-allocation-details-template>
    `,
  })
  class TestComponent {
    isEditing = true;
    formGroup = new FormGroup({
      subInstallationName: new FormControl('ALUMINIUM'),
      year: new FormControl(2024),
      allowances: new FormControl(200),
    });
    onSubmit: (form: FormGroup) => any | jest.SpyInstance<void, [FormGroup]>;
  }

  class Page extends BasePage<TestComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }

    get inputForm() {
      return this.query<HTMLFormElement>('form');
    }
    get subInstallationName() {
      return this.inputForm.querySelector<HTMLInputElement>('#subInstallationName');
    }
    get allowances() {
      return this.inputForm.querySelector<HTMLInputElement>('#allowances');
    }
    get yearSelect(): string {
      return this.getInputValue('#year');
    }
    get yearOptions() {
      return this.queryAll<HTMLOptionElement>('select[name="year"] option');
    }

    get submitButton(): HTMLButtonElement {
      return this.query('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    page = new Page(fixture);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(
      By.directive(PreliminaryAllocationDetailsTemplateComponent),
    ).componentInstance;
    hostComponent.onSubmit = jest.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the nested form', () => {
    expect(page.heading.textContent.trim()).toEqual('Allocation');
    expect(page.submitButton).toBeTruthy();
    expect(page.subInstallationName.value).toEqual('ALUMINIUM');
    expect(page.allowances.value).toEqual('200');
    expect(page.yearSelect).toEqual(2024);
    expect(page.yearOptions.map((opt) => opt.value)).toEqual([
      '0: 2021',
      '1: 2022',
      '2: 2023',
      '3: 2024',
      '4: 2025',
      '5: 2026',
      '6: 2027',
      '7: 2028',
      '8: 2029',
      '9: 2030',
      '10: 2031',
      '11: 2032',
      '12: 2033',
      '13: 2034',
      '14: 2035',
    ]);
  });
});
