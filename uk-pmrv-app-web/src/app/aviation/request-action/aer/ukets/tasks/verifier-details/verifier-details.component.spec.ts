import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import VerifierDetailsComponent from './verifier-details.component';

class Page extends BasePage<VerifierDetailsComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('VerifierDetailsComponent', () => {
  let fixture: ComponentFixture<VerifierDetailsComponent>;
  let store: RequestActionStore;
  let page: Page;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, VerifierDetailsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED',
        creationDate: '2023-09-20T12:18:46.714Z',
        payload: {
          verificationReport: {
            verificationBodyDetails: {
              name: 'VB Company',
              accreditationReferenceNumber: '1313',
              address: {
                city: 'City',
                country: 'GR',
                line1: 'street 1',
                line2: 'street 2',
                postcode: '111 80',
              },
              emissionTradingSchemes: ['UK_ETS_AVIATION', 'EU_ETS_INSTALLATIONS', 'CORSIA', 'UK_ETS_INSTALLATIONS'],
            },
            verificationTeamDetails: {
              authorisedSignatoryName: 'authorised signatory name',
              etsAuditors: 'ets auditors',
              etsTechnicalExperts: 'ets technical experts',
              independentReviewer: 'independent reviewer',
              leadEtsAuditor: 'lead ets auditor',
              technicalExperts: 'technical experts',
            },
            verifierContact: {
              email: 'test@test.com',
              name: 'Verifier Name',
              phoneNumber: '6691423232',
            },
          },
        } as AerRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(VerifierDetailsComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Verifier details');

    expect(page.summaryValues).toEqual([
      ['Company name', 'VB Company'],
      ['Address', 'street 1  , street 2 City111 80'],
      ['Accreditation number', '1313'],
      ['National accreditation body', 'UK ETS Aviation EU ETS Installations CORSIA UK ETS Installations'],
      ['Name', 'Verifier Name'],
      ['Email', 'test@test.com'],
      ['Telephone number', '6691423232'],
      ['Lead ETS Auditor', 'lead ets auditor'],
      ['ETS Auditors', 'ets auditors'],
      ['Technical Experts (ETS Auditor)', 'ets technical experts'],
      ['Independent Reviewer', 'independent reviewer'],
      ['Technical Experts (Independent Review)', 'technical experts'],
      ['Name of authorised signatory', 'authorised signatory name'],
    ]);
  });
});
