import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { EmissionsReductionClaimComponent } from '@aviation/request-task/aer/corsia/shared/tasks/emissions-reduction-claim/emissions-reduction-claim.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

describe('EmissionsReductionClaimComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: EmissionsReductionClaimComponent;
  let fixture: ComponentFixture<EmissionsReductionClaimComponent>;

  class Page extends BasePage<EmissionsReductionClaimComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmissionsReductionClaimComponent, RouterTestingModule],
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

    fixture = TestBed.createComponent(EmissionsReductionClaimComponent);
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
      ['Total emissions reduction claimed, from the template', '1000 tonnes CO2'],
      ['Declaration of no double claiming', 'declarationFile.png'],
    ]);
  });
});
