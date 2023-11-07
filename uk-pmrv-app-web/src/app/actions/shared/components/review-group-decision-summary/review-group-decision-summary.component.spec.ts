import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AerDataReviewDecision } from 'pmrv-api';

import { ActionSharedModule } from '../../action-shared-module';

describe('ReviewGroupDecisionSummaryComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: ` <app-review-group-decision-summary [decisionData]="decisionData"></app-review-group-decision-summary> `,
  })
  class TestComponent {
    decisionData: AerDataReviewDecision = {
      type: 'ACCEPTED',
      details: {
        notes: 'Fuels and equipment inventory Notes',
      },
      reviewDataType: 'AER_DATA',
    };
  }

  class Page extends BasePage<TestComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h2').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, ActionSharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(hostComponent).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Decision Summary');
    expect(page.summaryListValues).toHaveLength(2);
    expect(page.summaryListValues).toEqual([
      ['Decision status', 'Accepted'],
      ['Notes', 'Fuels and equipment inventory Notes'],
    ]);
  });
});
