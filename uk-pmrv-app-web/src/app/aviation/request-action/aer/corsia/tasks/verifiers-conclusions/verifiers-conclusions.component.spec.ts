import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import VerifiersConclusionsComponent from '@aviation/request-action/aer/corsia/tasks/verifiers-conclusions/verifiers-conclusions.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerCorsiaRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<VerifiersConclusionsComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('VerifiersConclusionsComponent', () => {
  let component: VerifiersConclusionsComponent;
  let fixture: ComponentFixture<VerifiersConclusionsComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, VerifiersConclusionsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          verificationReport: {
            verifiersConclusions: {
              dataQualityMateriality: 'My quality of data',
              materialityThresholdType: 'THRESHOLD_2_PER_CENT',
              materialityThresholdMet: true,
              emissionsReportConclusion: 'My conclusion',
            },
          },
        } as AerCorsiaRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(VerifiersConclusionsComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Conclusions on data quality and materiality');
    expect(page.summaryValues).toEqual([
      ['Conclusions on data quality', 'My quality of data'],
      ['Materiality threshold', '2%'],
      ['Is this materiality threshold being met in the emissions report?', 'Yes'],
      [`Conclusion relating to the operator's emissions report`, 'My conclusion'],
    ]);
  });
});
