import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { EmissionsReductionClaimComponent } from '@aviation/request-action/emp/ukets/tasks/emissions-reduction-claim/emissions-reduction-claim.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { EmpRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<EmissionsReductionClaimComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('EmissionsReductionClaimComponent', () => {
  let component: EmissionsReductionClaimComponent;
  let fixture: ComponentFixture<EmissionsReductionClaimComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestActionTaskComponent, EmpReviewDecisionGroupSummaryComponent, EmissionsReductionClaimComponent],
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
          emissionsMonitoringPlan: {
            emissionsReductionClaim: {
              exist: false,
            },
          },
        } as EmpRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(EmissionsReductionClaimComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Emissions reduction claim');
    expect(page.summaryValues).toHaveLength(1);
    expect(page.summaryValues).toEqual([['Are you intending to make an emissions reduction claim?', 'No']]);
  });
});
