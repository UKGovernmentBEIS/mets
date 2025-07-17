import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';
import { addDays, formatISO, setHours } from 'date-fns';

import { Dre } from 'pmrv-api';

import { FeeSummaryTemplateComponent } from './fee-summary-template.component';

describe('FeeSummaryTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
  let hostComponent: TestComponent;

  @Component({
    template: `
      <app-fee-summary-template [fee]="dre.fee" [editable]="editable"></app-fee-summary-template>
    `,
  })
  class TestComponent {
    editable = false;
    dre: Dre = {
      determinationReason: {
        type: 'VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER',
        operatorAskedToResubmit: true,
        regulatorComments: 'dfdf',
        supportingDocuments: ['2b587c89-1973-42ba-9682-b3ea5453b9dd'],
      },
      officialNoticeReason: '',
      monitoringApproachReportingEmissions: {},
      informationSources: [],
      fee: {
        feeDetails: {
          dueDate: formatISO(addDays(new Date(), 1), { representation: 'date' }),
          hourlyRate: '3',
          totalBillableHours: '34',
        },
        chargeOperator: true,
      },
    };
  }

  class Page extends BasePage<TestComponent> {
    get feeValues() {
      return this.queryAll<HTMLElement>('app-fee-summary-template .govuk-summary-list .govuk-summary-list__value');
    }

    get actions() {
      return this.queryAll<HTMLElement>('app-fee-summary-template .govuk-summary-list .govuk-summary-list__actions');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, FeeSummaryTemplateComponent],
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display emissions values', () => {
    const dateFormatOptions: Intl.DateTimeFormatOptions = {
      timeZone: 'Europe/London',
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    };
    const formatter = Intl.DateTimeFormat('en-GB-u-hc-h12', dateFormatOptions);
    const dueDateString = formatter
      .formatToParts(new Date(setHours(addDays(new Date(), 1), 12)))
      .map(({ value }, index) => (index === 9 ? '' : value))
      .join('');

    expect(page.feeValues.map((el) => el.textContent.trim())).toEqual([
      'Yes',
      '34 hours',
      '£3 per hour',
      dueDateString,
      '£102',
    ]);

    expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(0);
  });

  it('should display Change links', () => {
    hostComponent.editable = true;
    fixture.detectChanges();

    expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(5);
  });
});
