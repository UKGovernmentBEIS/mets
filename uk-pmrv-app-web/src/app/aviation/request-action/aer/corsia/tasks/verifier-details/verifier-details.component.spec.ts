import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import VerifierDetailsComponent from '@aviation/request-action/aer/corsia/tasks/verifier-details/verifier-details.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerCorsiaRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

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
  let component: VerifierDetailsComponent;
  let fixture: ComponentFixture<VerifierDetailsComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

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
        type: 'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
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
          },
        } as AerCorsiaRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(VerifierDetailsComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Verifier details and impartiality');
    expect(page.summaryValues).toEqual([
      ['Company name', 'Verification body company'],
      ['Address', `Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452`],
      ['Name', 'My name'],
      ['Position', 'My position'],
      ['Role and expertise', 'My role'],
      ['Email', 'test@pmrv.com'],
      ['Have you conducted six or more annual verifications as a team leader for this aeroplane operator?', 'No'],
      ['Describe the main results of impartiality and avoidance of conflict of interest assessment', 'My results'],
    ]);
  });
});
