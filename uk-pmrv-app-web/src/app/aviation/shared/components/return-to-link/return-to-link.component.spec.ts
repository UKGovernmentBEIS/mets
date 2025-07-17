import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub } from '@testing';
import { screen } from '@testing-library/angular';
import produce from 'immer';

import { RequestTaskStore } from '../../../request-task/store';
import { ReturnToLinkComponent } from './return-to-link.component';

describe('ReturnToLinkComponent', () => {
  let fixture: ComponentFixture<ReturnToLinkComponent>;
  let store: RequestTaskStore;

  const route = new ActivatedRouteStub({ taskId: '10' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReturnToLinkComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    fixture = TestBed.createComponent(ReturnToLinkComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it(`should display correct link text for review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Review emissions monitoring plan application')).toBeInTheDocument();
  });

  it(`should display correct link text for review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW',
          },
        };
      }),
    );

    fixture.detectChanges();
    expect(screen.getByText('Return to: Review emissions monitoring plan application')).toBeInTheDocument();
  });

  it(`should display correct link text for wait for peer review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(
      screen.getByText('Return to: Emissions monitoring plan application sent to peer reviewer'),
    ).toBeInTheDocument();
  });

  it(`should display correct link text for application returned to operator`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(
      screen.getByText('Return to: Emissions monitoring plan application returned to operator'),
    ).toBeInTheDocument();
  });

  it(`should display correct link text for peer review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Peer review emissions monitoring plan application')).toBeInTheDocument();
  });

  it(`should display correct link text for peer review CORSIA`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW',
          },
        };
      }),
    );

    fixture.detectChanges();
    expect(screen.getByText('Return to: Peer review emissions monitoring plan application')).toBeInTheDocument();
  });

  it(`should display correct link text for wait for peer review CORSIA`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW',
          },
        };
      }),
    );

    fixture.detectChanges();
    expect(
      screen.getByText('Return to: Emissions monitoring plan application sent to peer reviewer'),
    ).toBeInTheDocument();
  });

  it(`should display correct link text for aer`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Emissions report')).toBeInTheDocument();
  });

  it(`should display correct link text for aer verification`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Verify emissions report')).toBeInTheDocument();
  });

  it(`should display correct link text for aer corsia verification`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Verify emissions report')).toBeInTheDocument();
  });

  it(`should display correct link text for aer ukets amends verification`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Verify emissions report')).toBeInTheDocument();
  });

  it(`should display correct link text for aer corsia amends verification`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Verify emissions report')).toBeInTheDocument();
  });

  it(`should display correct link text for aer review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Review emissions report')).toBeInTheDocument();
  });

  it(`should display correct link text for aer emissions report completed`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_COMPLETED',
          } as any,
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Emissions report reviewed')).toBeInTheDocument();
  });

  it(`should display correct link text for aer emissions report returned to operator`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_WAIT_FOR_AMENDS',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Emissions report returned to operator')).toBeInTheDocument();
  });

  it(`should display correct link text for aer corsia emissions report returned to operator`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_CORSIA_WAIT_FOR_AMENDS',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Emissions report returned to operator')).toBeInTheDocument();
  });

  it(`should display correct link text for aer amend your emissions report`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Amend your emissions report')).toBeInTheDocument();
  });

  it(`should display correct link text for aer corsia amend your emissions report`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Amend your emissions report')).toBeInTheDocument();
  });

  it(`should display correct link text for aer amends sent to verifier`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER',
          } as any,
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Changes submitted')).toBeInTheDocument();
  });

  it(`should display correct link text for aer corsia amends sent to verifier`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER',
          } as any,
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Changes submitted')).toBeInTheDocument();
  });

  it(`should display correct link text for aer amends submitted`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED',
          } as any,
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Changes submitted')).toBeInTheDocument();
  });

  it(`should display correct link text for aer corsia amends submitted`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED',
          } as any,
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Changes submitted')).toBeInTheDocument();
  });

  it(`should display correct link text for emp`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Apply for an emissions monitoring plan')).toBeInTheDocument();
  });

  it(`should display correct link text for emp operators amends`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Amend your emissions monitoring plan')).toBeInTheDocument();
  });

  it(`should display correct link text for emp corsia operators amends`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT',
          },
        };
      }),
    );

    fixture.detectChanges();
    expect(screen.getByText('Return to: Amend your emissions monitoring plan')).toBeInTheDocument();
  });

  it(`should display correct link text for application returned to operator`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS',
          },
        };
      }),
    );

    fixture.detectChanges();
    expect(
      screen.getByText('Return to: Emissions monitoring plan application returned to operator'),
    ).toBeInTheDocument();
  });

  it(`should display correct link text for emp variation`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Apply to vary your emissions monitoring plan')).toBeInTheDocument();
  });

  it(`should display correct link text for dre emissions`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Determine aviation emissions report')).toBeInTheDocument();
  });

  it(`should display correct link text for dre emissions send for peer review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Peer review aviation emissions report determination')).toBeInTheDocument();
  });

  it(`should display correct link text for dre emissions wait for peer review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(
      screen.getByText('Return to: Aviation emissions report determination sent to peer reviewer'),
    ).toBeInTheDocument();
  });

  it(`should display correct link text for review variation`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_UKETS_APPLICATION_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Review emissions monitoring plan variation')).toBeInTheDocument();
  });

  it(`should display correct link text for review variation corsia`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Review emissions monitoring plan variation')).toBeInTheDocument();
  });

  it(`should display correct link text for review variation`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_UKETS_WAIT_FOR_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Emissions monitoring plan variation sent to regulator')).toBeInTheDocument();
  });

  it(`should display correct link text for review variation`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Emissions monitoring plan variation sent to regulator')).toBeInTheDocument();
  });

  it(`should display correct link text for emp variation returned to operator`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_UKETS_WAIT_FOR_AMENDS',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Emissions monitoring plan variation returned to operator')).toBeInTheDocument();
  });

  it(`should display correct link text for emp corsia variation returned to operator`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Emissions monitoring plan variation returned to operator')).toBeInTheDocument();
  });

  it(`should display correct link text for wait for peer review of variation`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(
      screen.getByText('Return to: Emissions monitoring plan variation sent to peer reviewer'),
    ).toBeInTheDocument();
  });

  it(`should display correct link text for wait for peer review of Corsia variation`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(
      screen.getByText('Return to: Emissions monitoring plan variation sent to peer reviewer'),
    ).toBeInTheDocument();
  });

  it(`should display correct link text for peer review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Peer review emissions monitoring plan variation')).toBeInTheDocument();
  });

  it(`should display correct link text for Corsia variation peer review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Peer review emissions monitoring plan variation')).toBeInTheDocument();
  });

  it(`should display correct link text for emp variation review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED' as any,
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Submitted')).toBeInTheDocument();
  });

  it(`should display correct link text for emp corsia variation amends`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED' as any,
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Submitted')).toBeInTheDocument();
  });

  it(`should display correct link text for ukets regulator led variation`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Vary the emissions monitoring plan')).toBeInTheDocument();
  });

  it(`should display correct link text for corsia regulator led variation`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Vary the emissions monitoring plan')).toBeInTheDocument();
  });

  it(`should display correct link text for regulator led variation wait peer review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Vary the emissions monitoring plan sent to peer reviewer')).toBeInTheDocument();
  });

  it(`should display correct link text for regulator led variation peer review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Peer review vary the emissions monitoring plan')).toBeInTheDocument();
  });

  it(`should display correct link text for vir submit`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_VIR_APPLICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Verifier improvement report')).toBeInTheDocument();
  });

  it(`should display correct link text for vir review`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_VIR_APPLICATION_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Review verifier improvement report')).toBeInTheDocument();
  });

  it(`should display correct link text for vir respond to regulator comments`, () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Verifier improvement report')).toBeInTheDocument();
  });

  it('should display correct link text for aviation corsia annual offsetting requirements', () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Calculate annual offsetting requirements')).toBeInTheDocument();
  });

  it('should display correct link text for aviation corsia 3 year period offsetting requirements submit', () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Calculate 3-year period offsetting requirements')).toBeInTheDocument();
  });

  it('should display correct link text for aviation corsia 3 year period offsetting requirements peer review', () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Peer review 3-year period offsetting requirements')).toBeInTheDocument();
  });

  it('should display correct link text for aviation doe corsia submit', () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Estimate emissions')).toBeInTheDocument();
  });

  it('should display correct link text for aviation doe corsia wait for peer review', () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_DOE_CORSIA_WAIT_FOR_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(
      screen.getByText('Return to: Aviation emissions report estimation sent to peer reviewer'),
    ).toBeInTheDocument();
  });

  it('should display correct link text for aviation doe corsia peer review decision', () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW',
          },
        };
      }),
    );
    fixture.detectChanges();
    expect(screen.getByText('Return to: Peer review emissions estimation')).toBeInTheDocument();
  });
});
