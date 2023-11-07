import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { OperatorEmissionsReductionClaimComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-emissions-reduction-claim/operator/operator-emissions-reduction-claim.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';

describe('SummaryComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: OperatorEmissionsReductionClaimComponent;
  let fixture: ComponentFixture<OperatorEmissionsReductionClaimComponent>;

  class Page extends BasePage<OperatorEmissionsReductionClaimComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorEmissionsReductionClaimComponent, RouterTestingModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
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
            aer: {
              emissionsReductionClaim: {
                exist: true,
                emissionsReductionClaimDetails: {
                  cefFiles: ['randomUUID1'],
                  totalEmissions: '1000',
                  noDoubleCountingDeclarationFiles: ['randomUUID2'],
                },
              },
            },
            aerAttachments: {
              randomUUID1: 'cefFile.png',
              randomUUID2: 'declarationFile.png',
            },
          },
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(OperatorEmissionsReductionClaimComponent);
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
      ['Will you be making a claim for emissions reductions from the use of CORSIA eligible fuels?', 'Yes'],
      ['CEF template', 'cefFile.png'],
      ['Total emissions reduction claimed, from the template', '1000'],
      ['Declaration of no double claiming', 'declarationFile.png'],
    ]);
  });
});
