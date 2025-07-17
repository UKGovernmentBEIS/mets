import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

import { AviationAerCorsiaApplicationReviewRequestTaskPayload, AviationAerCorsiaVerificationReport } from 'pmrv-api';

import { VerifyEmissionsReductionClaimComponent } from './verify-emissions-reduction-claim.component';

describe('VerifyEmissionsReductionClaimComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: VerifyEmissionsReductionClaimComponent;
  let fixture: ComponentFixture<VerifyEmissionsReductionClaimComponent>;

  class Page extends BasePage<VerifyEmissionsReductionClaimComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifyEmissionsReductionClaimComponent, RouterTestingModule],
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
              emissionsReductionClaimVerification: {
                reviewResults: 'My review results',
              },
            } as AviationAerCorsiaVerificationReport,
          } as AviationAerCorsiaApplicationReviewRequestTaskPayload,
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(VerifyEmissionsReductionClaimComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.summaryValues).toEqual([['Describe the results of your review', 'My review results']]);
  });
});
