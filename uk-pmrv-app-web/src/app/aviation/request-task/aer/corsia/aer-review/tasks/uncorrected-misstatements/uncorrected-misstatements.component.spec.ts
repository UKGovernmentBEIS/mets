import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

import { AviationAerCorsiaApplicationReviewRequestTaskPayload, AviationAerCorsiaVerificationReport } from 'pmrv-api';

import { UncorrectedMisstatementsComponent } from './uncorrected-misstatements.component';

describe('UncorrectedMisstatementsComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: UncorrectedMisstatementsComponent;
  let fixture: ComponentFixture<UncorrectedMisstatementsComponent>;

  class Page extends BasePage<UncorrectedMisstatementsComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UncorrectedMisstatementsComponent, RouterTestingModule],
      providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);

    store.setState({
      requestTaskItem: {
        requestInfo: { type: 'AVIATION_AER_CORSIA' },
        requestTask: {
          id: 19,
          type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
            verificationReport: {
              uncorrectedMisstatements: {
                exist: true,
                uncorrectedMisstatements: [{ reference: 'A1', explanation: 'explanation', materialEffect: true }],
              },
            } as AviationAerCorsiaVerificationReport,
          } as AviationAerCorsiaApplicationReviewRequestTaskPayload,
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(UncorrectedMisstatementsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.summaryValues).toEqual([
      ['Are there any misstatements that were not corrected before issuing this report?', 'Yes'],
    ]);
  });
});
