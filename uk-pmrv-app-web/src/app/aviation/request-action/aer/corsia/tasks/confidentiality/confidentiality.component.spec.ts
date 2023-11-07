import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerCorsiaRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import ConfidentialityComponent from './confidentiality.component';

class Page extends BasePage<ConfidentialityComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('ConfidentialityComponent', () => {
  let component: ConfidentialityComponent;
  let fixture: ComponentFixture<ConfidentialityComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, ConfidentialityComponent],
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
          aer: {
            confidentiality: {
              totalEmissionsPublished: false,
              aggregatedStatePairDataPublished: false,
            },
          },
        } as AerCorsiaRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(ConfidentialityComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Request for data not to be published by ICAO');
    expect(page.summaryValues).toEqual([
      ['Do you want to request ICAO not to publish your total annual emissions data at the operator level?', 'No'],
      ['Do you want to request ICAO not to publish your aggregated state pair data?', 'No'],
    ]);
  });
});
