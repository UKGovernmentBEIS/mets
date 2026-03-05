import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { DoalProceedToAuthorityDetermination } from 'pmrv-api';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../shared.module';

describe('DeterminationProceedAuthorityReasonTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-determination-proceed-authority-reason-template
        [determination]="determination"
        [editable]="editable"></app-determination-proceed-authority-reason-template>
    `,
  })
  class TestComponent {
    editable = true;
    determination: DoalProceedToAuthorityDetermination = {
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
      imports: [RouterTestingModule, SharedModule],
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
      'Article 6a of the Activity Level Changes Regulation (allocation adjustment under Article 5)',
      'reason',
    ]);
  });

  it('should display change links', () => {
    expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(2);
  });
});
