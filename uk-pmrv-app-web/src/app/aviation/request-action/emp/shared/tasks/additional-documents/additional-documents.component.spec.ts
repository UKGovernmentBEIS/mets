import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AdditionalDocumentsComponent } from '@aviation/request-action/emp/shared/tasks/additional-documents/additional-documents.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { EmpRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<AdditionalDocumentsComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDListElement>('dd');
  }
}

describe('AdditionalDocumentsComponent', () => {
  let component: AdditionalDocumentsComponent;
  let fixture: ComponentFixture<AdditionalDocumentsComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SharedModule,
        RequestActionTaskComponent,
        EmpReviewDecisionGroupSummaryComponent,
        AdditionalDocumentsComponent,
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
            additionalDocuments: {
              exist: false,
            },
          },
        } as EmpRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(AdditionalDocumentsComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Additional documents and information');
    expect(page.summaryValues.map((dd) => dd.textContent)).toEqual(['No']);
  });
});
