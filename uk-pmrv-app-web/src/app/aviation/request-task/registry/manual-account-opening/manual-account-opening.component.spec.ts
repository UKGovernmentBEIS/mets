import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage, mockClass } from '@testing';

import { AviationAccountViewService } from 'pmrv-api';

import { ManualAccountOpeningComponent } from './manual-account-opening.component';

describe('ManualAccountOpeningComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: ManualAccountOpeningComponent;
  let fixture: ComponentFixture<ManualAccountOpeningComponent>;

  let mockedAviationAccountViewService: jest.Mocked<AviationAccountViewService>;

  class Page extends BasePage<ManualAccountOpeningComponent> {
    get detailsValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    mockedAviationAccountViewService = mockClass(AviationAccountViewService);

    mockedAviationAccountViewService.getAviationAccountViewForRegistry.mockReturnValue(
      of({
        operatorDetails: {
          emitterId: 'EM00044',
          operatorName: 'Neil Case 25',
          firstKnownAviationActivity: '1971-07-19',
          regulator: 'ENGLAND',
        },
        organisationDetails: {
          organisationLegalStatus: 'INDIVIDUAL',
          fullName: 'Tobias Browning',
          address: {
            type: 'ONSHORE_STATE',
            line1: 'Stephenson Lancaster Inc',
            line2: 'Briggs and Riggs Associates',
            city: 'Hood and Owen Plc',
            state: 'Byrd and Harmon Inc',
            postcode: '45646',
            country: 'CZ',
          },
        },
      } as any),
    );

    await TestBed.configureTestingModule({
      imports: [ManualAccountOpeningComponent, RouterTestingModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: AviationAccountViewService, useValue: mockedAviationAccountViewService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManualAccountOpeningComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);

    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 19,
          type: 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
          payload: {
            payloadType: 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
          },
        },
        requestInfo: {
          id: 'EMP00044',
          type: 'EMP_ISSUANCE_UKETS',
          competentAuthority: 'ENGLAND',
          accountId: 44,
          paymentCompleted: true,
          paymentAmount: '1260',
        },
      },
      isEditable: true,
    } as any);

    fixture.detectChanges();
  });

  afterEach(async () => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show details values', () => {
    expect(page.detailsValues).toEqual([
      ['Emitter ID', 'EM00044'],
      ['Operator name', 'Neil Case 25'],
      ['First known aviation activity', '19 Jul 1971'],
      ['Regulator', 'ENGLAND'],
      ['Organisation legal status', 'Individual'],
      ['Full name', 'Tobias Browning'],
      ['Address', 'Stephenson Lancaster Inc  , Briggs and Riggs Associates Hood and Owen PlcByrd and Harmon Inc45646'],
    ]);
  });
});
