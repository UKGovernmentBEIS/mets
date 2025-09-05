import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { DeterminationProceedAuthoritySummaryTemplateComponent } from './determination-proceed-authority-summary-template.component';

describe('DeterminationProceedAuthoritySummaryTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-alr-determination-proceed-authority-summary-template
        [determination]="determination"
        [editable]="editable"></app-alr-determination-proceed-authority-summary-template>
    `,
  })
  class TestComponent {
    editable = true;
    determination: any = {
      type: 'PROCEED_TO_AUTHORITY',
      reason: 'reason',
      articleReasonGroupType: 'ARTICLE_6A_REASONS',
      articleReasonItems: ['ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5'],
      hasWithholdingOfAllowances: true,
      withholdingAllowancesNotice: {
        noticeIssuedDate: '2022-08-10',
        withholdingOfAllowancesComment: 'withholdingOfAllowancesComment',
      },
      needsOfficialNotice: true,
    };
  }

  class Page extends BasePage<TestComponent> {
    get values() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__value');
    }

    get actions() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__actions');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent],
      imports: [SharedModule, DeterminationProceedAuthoritySummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display data', () => {
    expect(page.values.map((el) => el.textContent.trim())).toEqual([
      'Article 6a reasons  1.  Article 6a of the Activity Level Changes Regulation (allocation adjustment under Article 5)',
      'reason',
      'Yes',
      '10 Aug 2022',
      'withholdingOfAllowancesComment',
      'Yes',
    ]);
  });

  it('should display change links', () => {
    expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(6);
  });
});
