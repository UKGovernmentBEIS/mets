import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SummaryOfConditionsGroupComponent } from '@shared/components/review-groups/summary-of-conditions-group/summary-of-conditions-group.component';
import { SharedModule } from '@shared/shared.module';

describe('SummaryOfConditionsGroupComponent', () => {
  let component: SummaryOfConditionsGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  const mockSummaryOfConditionsInfo = {
    changesNotIncludedInPermit: true,
    approvedChangesNotIncluded: [{ reference: 'A1', explanation: 'Explanation A1' }],
    changesIdentified: true,
    notReportedChanges: [{ reference: 'B1', explanation: 'Explanation B1' }],
  };

  function getRows() {
    return Array.from(element.querySelectorAll('tr')).map((el) =>
      Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
    );
  }

  function getSummaryListValues() {
    return Array.from(element.querySelectorAll('dl')).map((el) => [
      el.querySelector('dt').textContent.trim(),
      Array.from(el.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
    ]);
  }

  @Component({
    template: `
      <app-summary-of-conditions-group
        [isEditable]="isEditable"
        [summaryOfConditionsInfo]="summaryOfConditionsInfo"
      ></app-summary-of-conditions-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    summaryOfConditionsInfo = {
      changesNotIncludedInPermit: true,
      approvedChangesNotIncluded: [{ reference: 'A1', explanation: 'Explanation A1' }],
      changesIdentified: true,
      notReportedChanges: [{ reference: 'B1', explanation: 'Explanation B1' }],
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
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(SummaryOfConditionsGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the summary', () => {
    expect(Array.from(element.querySelectorAll('h2')).map((el) => el.textContent.trim())).toEqual([
      'Approved changes not included in a re-issued permit',
      'Changes not reported to the regulator by the end of the reporting year',
    ]);

    expect(getRows()).toEqual([[], ['A1', 'Explanation A1', '', ''], [], ['B1', 'Explanation B1', '', '']]);
    expect(getSummaryListValues()).toEqual([
      ['Were there any changes approved by the regulator that are not included in a re-issued permit?', ['Yes', '']],
      [
        'Were there any changes identified during your review that were not reported to the regulator by the end of the reporting period?',
        ['Yes', ''],
      ],
    ]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(getRows()).toEqual([
      [],
      ['A1', 'Explanation A1', 'Change', 'Remove'],
      [],
      ['B1', 'Explanation B1', 'Change', 'Remove'],
    ]);
    expect(getSummaryListValues()).toEqual([
      [
        'Were there any changes approved by the regulator that are not included in a re-issued permit?',
        ['Yes', 'Change'],
      ],
      [
        'Were there any changes identified during your review that were not reported to the regulator by the end of the reporting period?',
        ['Yes', 'Change'],
      ],
    ]);

    hostComponent.summaryOfConditionsInfo = {
      ...mockSummaryOfConditionsInfo,
      notReportedChanges: null,
    };
    fixture.detectChanges();

    expect(getRows()).toEqual([[], ['A1', 'Explanation A1', 'Change', 'Remove'], []]);
    expect(getSummaryListValues()).toEqual([
      [
        'Were there any changes approved by the regulator that are not included in a re-issued permit?',
        ['Yes', 'Change'],
      ],
      [
        'Were there any changes identified during your review that were not reported to the regulator by the end of the reporting period?',
        ['Yes', 'Change'],
      ],
    ]);

    hostComponent.summaryOfConditionsInfo = {
      ...mockSummaryOfConditionsInfo,
      changesIdentified: false,
      notReportedChanges: null,
    };
    fixture.detectChanges();

    expect(getRows()).toEqual([[], ['A1', 'Explanation A1', 'Change', 'Remove']]);
    expect(getSummaryListValues()).toEqual([
      [
        'Were there any changes approved by the regulator that are not included in a re-issued permit?',
        ['Yes', 'Change'],
      ],
      [
        'Were there any changes identified during your review that were not reported to the regulator by the end of the reporting period?',
        ['No', 'Change'],
      ],
    ]);

    hostComponent.summaryOfConditionsInfo = {
      ...mockSummaryOfConditionsInfo,
      changesIdentified: undefined,
      notReportedChanges: null,
    };
    fixture.detectChanges();

    expect(getRows()).toEqual([[], ['A1', 'Explanation A1', 'Change', 'Remove']]);
    expect(getSummaryListValues()).toEqual([
      [
        'Were there any changes approved by the regulator that are not included in a re-issued permit?',
        ['Yes', 'Change'],
      ],
    ]);

    hostComponent.summaryOfConditionsInfo = {
      ...mockSummaryOfConditionsInfo,
      approvedChangesNotIncluded: null,
      changesIdentified: undefined,
      notReportedChanges: null,
    };
    fixture.detectChanges();

    expect(getRows()).toEqual([[]]);
    expect(getSummaryListValues()).toEqual([
      [
        'Were there any changes approved by the regulator that are not included in a re-issued permit?',
        ['Yes', 'Change'],
      ],
    ]);

    hostComponent.summaryOfConditionsInfo = {
      changesNotIncludedInPermit: false,
      approvedChangesNotIncluded: null,
      changesIdentified: undefined,
      notReportedChanges: null,
    };
    fixture.detectChanges();

    expect(getRows()).toEqual([]);
    expect(getSummaryListValues()).toEqual([
      [
        'Were there any changes approved by the regulator that are not included in a re-issued permit?',
        ['No', 'Change'],
      ],
    ]);

    hostComponent.summaryOfConditionsInfo = {
      changesNotIncludedInPermit: undefined,
      approvedChangesNotIncluded: null,
      changesIdentified: undefined,
      notReportedChanges: null,
    };
    fixture.detectChanges();

    expect(getRows()).toEqual([]);
    expect(getSummaryListValues()).toEqual([]);

    hostComponent.summaryOfConditionsInfo = undefined;
    fixture.detectChanges();

    expect(getRows()).toEqual([]);
    expect(getSummaryListValues()).toEqual([]);
  });
});
