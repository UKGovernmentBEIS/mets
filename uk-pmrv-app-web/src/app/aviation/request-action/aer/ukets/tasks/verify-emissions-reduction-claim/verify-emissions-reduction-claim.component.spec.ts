import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import VerifyEmissionsReductionClaimComponent from './verify-emissions-reduction-claim.component';

class Page extends BasePage<VerifyEmissionsReductionClaimComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('VerifyEmissionsReductionClaimComponent', () => {
  let fixture: ComponentFixture<VerifyEmissionsReductionClaimComponent>;
  let store: RequestActionStore;
  let page: Page;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, VerifyEmissionsReductionClaimComponent],
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
          aer: {
            aerMonitoringPlanChanges: {
              notCoveredChangesExist: false,
              details: 'details',
            },
          },
          verificationReport: {
            emissionsReductionClaimVerification: {
              safBatchClaimsReviewed: true,
              batchReferencesNotReviewed: 'reason',
              dataSampling: 'dataSampling',
              reviewResults: 'reviewResults',
              noDoubleCountingConfirmation: 'noDoubleCountingConfirmation',
              evidenceAllCriteriaMetExist: true,
              noCriteriaMetIssues: 'noCriteriaMetIssues',
              complianceWithUkEtsRequirementsExist: true,
              notCompliedWithUkEtsRequirementsAspects: 'notCompliedWithUkEtsRequirementsAspects',
            },
          },
        } as AerRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(VerifyEmissionsReductionClaimComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Verify the emissions reduction claim');

    expect(page.summaryValues).toEqual([
      ['Have you reviewed all the aircraft operator’s sustainable aviation fuel (SAF) batch claims?', 'Yes'],
      ['Results of your review', 'reviewResults'],
      ['Confirmation of no double-counting', 'noDoubleCountingConfirmation'],
      [
        'Do all of the batch claims reviewed contain evidence that shows the sustainability, purchase and delivery criteria were met?',
        'Yes',
      ],
      ['Compliance with UK ETS requirements', 'Yes'],
    ]);
  });
});
