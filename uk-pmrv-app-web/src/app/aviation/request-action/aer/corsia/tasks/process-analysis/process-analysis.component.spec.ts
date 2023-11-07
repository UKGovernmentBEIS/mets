import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import ProcessAnalysisComponent from '@aviation/request-action/aer/corsia/tasks/process-analysis/process-analysis.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerCorsiaRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<ProcessAnalysisComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('ProcessAnalysisComponent', () => {
  let component: ProcessAnalysisComponent;
  let fixture: ComponentFixture<ProcessAnalysisComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, ProcessAnalysisComponent],
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
            processAnalysis: {
              verificationActivities: 'My verification activities',
              strategicAnalysis: 'My strategic analysis',
              dataSampling: 'My data sampling',
              dataSamplingResults: 'My data sampling results',
              emissionsMonitoringPlanCompliance: 'My emissions monitoring plan compliance',
            },
          },
        } as AerCorsiaRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(ProcessAnalysisComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Process and analysis details');
    expect(page.summaryValues).toEqual([
      [
        'Describe the verification activities carried out and their corresponding results',
        'My verification activities',
      ],
      ['Strategic analysis and risk assessment', 'My strategic analysis'],
      ['Data sampling', 'My data sampling'],
      ['Results of data sampling', 'My data sampling results'],
      ['Compliance with the emissions monitoring plan', 'My emissions monitoring plan compliance'],
    ]);
  });
});
