import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { addDays, format } from 'date-fns';

import { DreSummaryComponent } from './dre-summary-template.component';

describe('DreSummaryComponent', () => {
  let component: DreSummaryComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-dre-summary-template [dre]="dre" [isEditable]="isEditable"></app-dre-summary-template>
    `,
  })
  class TestComponent {
    isEditable = false;
    dre = {
      determinationReason: {
        type: 'VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER',
        operatorAskedToResubmit: true,
        regulatorComments: 'dfdf',
        supportingDocuments: ['2b587c89-1973-42ba-9682-b3ea5453b9dd'],
      },
      officialNoticeReason: 'fdfdf',
      monitoringApproachReportingEmissions: {
        FALLBACK: {
          type: 'FALLBACK',
          emissions: {
            reportableEmissions: 1,
            sustainableBiomass: 2,
          },
        } as any,
      },
      informationSources: ['fgf'],
      fee: {
        feeDetails: {
          dueDate: format(addDays(new Date(), 1), 'yyyy-MM-dd'),
          hourlyRate: '3',
          totalBillableHours: '34',
        },
        chargeOperator: true,
      },
    };
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    component = fixture.debugElement.query(By.directive(DreSummaryComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the rebortable emissions summary sections', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLElement>('.govuk-heading-m')).map((el) => el.textContent.trim()),
    ).toEqual([
      'Reason for determining the reportable emissions',
      'Monitoring approaches',
      'Reportable emissions',
      'Sources of information used',
      'Operator fee',
    ]);
  });
});
