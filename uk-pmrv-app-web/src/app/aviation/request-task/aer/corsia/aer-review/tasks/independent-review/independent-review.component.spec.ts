import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

import {
  AviationAerCorsiaApplicationReviewRequestTaskPayload,
  AviationAerCorsiaIndependentReview,
  AviationAerCorsiaVerificationReport,
} from 'pmrv-api';

import { IndependentReviewComponent } from './independent-review.component';

describe('IndependentReviewComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: IndependentReviewComponent;
  let fixture: ComponentFixture<IndependentReviewComponent>;

  class Page extends BasePage<IndependentReviewComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IndependentReviewComponent, RouterTestingModule],
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
              independentReview: {
                reviewResults: 'My review results',
                name: 'My name',
                position: 'My position',
                email: 'test@pmrv.com',
                line1: 'Some street 4',
                city: 'Athens',
                country: 'GR',
              } as AviationAerCorsiaIndependentReview,
            } as AviationAerCorsiaVerificationReport,
            aerAttachments: {
              randomUUID1: 'reportingObligationFile.png',
            },
          } as AviationAerCorsiaApplicationReviewRequestTaskPayload,
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(IndependentReviewComponent);
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
      ['Results of the independent review', 'My review results'],
      ['Name', 'My name'],
      ['Position', 'My position'],
      ['Email', 'test@pmrv.com'],
      ['Address', 'Some street 4 Athens'],
    ]);
  });
});
