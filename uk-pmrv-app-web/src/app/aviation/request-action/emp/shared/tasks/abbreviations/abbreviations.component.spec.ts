import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AbbreviationsComponent } from '@aviation/request-action/emp/shared/tasks/abbreviations/abbreviations.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { EmpRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<AbbreviationsComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDListElement>('dd');
  }
}

describe('AbbreviationsComponent', () => {
  let component: AbbreviationsComponent;
  let fixture: ComponentFixture<AbbreviationsComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SharedModule,
        RequestActionTaskComponent,
        EmpReviewDecisionGroupSummaryComponent,
        AbbreviationsComponent,
      ],
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
            abbreviations: {
              exist: true,
              abbreviationDefinitions: [
                {
                  abbreviation: 'Mr',
                  definition: 'Mister',
                },
                {
                  abbreviation: 'Ms',
                  definition: 'Miss',
                },
              ],
            },
          },
        } as EmpRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(AbbreviationsComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Abbreviations and definitions');
    expect(page.summaryValues.map((dd) => dd.textContent)).toEqual(['Mr', 'Mister', 'Ms', 'Miss']);
  });
});
