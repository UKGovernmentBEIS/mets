import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

import { AviationAerCorsiaApplicationReviewRequestTaskPayload, AviationAerCorsiaVerificationReport } from 'pmrv-api';

import { VerifierDetailsComponent } from './verifier-details.component';

describe('VerifierDetailsComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: VerifierDetailsComponent;
  let fixture: ComponentFixture<VerifierDetailsComponent>;

  class Page extends BasePage<VerifierDetailsComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifierDetailsComponent, RouterTestingModule],
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
              verificationBodyDetails: {
                name: 'Verification body company',
                address: {
                  line1: 'Korinthou 4, Neo Psychiko',
                  line2: 'line 2 legal test',
                  city: 'Athens',
                  country: 'GR',
                  postcode: '15452',
                },
              },
              verificationBodyId: 1,
              verifierDetails: {
                verificationTeamLeader: {
                  name: 'My name',
                  role: 'My role',
                  email: 'test@pmrv.com',
                  position: 'My position',
                },
                interestConflictAvoidance: {
                  sixVerificationsConducted: false,
                  impartialityAssessmentResult: 'My results',
                },
              },
            } as AviationAerCorsiaVerificationReport,
          } as AviationAerCorsiaApplicationReviewRequestTaskPayload,
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(VerifierDetailsComponent);
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
      ['Company name', 'Verification body company'],
      ['Address', 'Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452'],
      ['Name', 'My name'],
      ['Position', 'My position'],
      ['Role and expertise', 'My role'],
      ['Email', 'test@pmrv.com'],
      ['Has the team leader made six or more annual verifications for this aeroplane operator?', 'No'],
      ['Results of the impartiality and conflict of interest assessment', 'My results'],
    ]);
  });
});
