import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { EmpRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { EmpVariationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-review-decision-group-summary/emp-variation-review-decision-group-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { VariationDetailsComponent } from './variation-details.component';

class Page extends BasePage<VariationDetailsComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('VariationDetailsComponent', () => {
  let component: VariationDetailsComponent;
  let fixture: ComponentFixture<VariationDetailsComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestActionTaskComponent, EmpVariationReviewDecisionGroupSummaryComponent, VariationDetailsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          empVariationDetails: {
            reason: 'reason',
            changes: ['EMISSION_FACTOR_VALUES', 'OTHER'],
          },
          reasonRegulatorLed: {
            type: 'FAILED_TO_COMPLY_OR_APPLY',
          },
        } as EmpRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(VariationDetailsComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Describe the changes');
    expect(page.summaryValues).toHaveLength(4);
    expect(page.summaryValues).toEqual([
      ['Significant changes', 'changing the emission factor values'],
      ['Non-significant changes', 'other changes'],
      ['Explain what you are changing and the reasons for the changes', 'reason'],
      [
        'Reason to include in the notice',
        'Aircraft operator failed to comply with a requirement in the plan, or to apply in accordance with conditions',
      ],
    ]);
  });
});
