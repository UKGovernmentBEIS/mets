import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { OperatorDetailsComponent } from '@aviation/request-task/aer/corsia/shared/tasks/operator-details/operator-details.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

import { OrganisationStructure } from 'pmrv-api';

describe('OperatorDetailsComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: OperatorDetailsComponent;
  let fixture: ComponentFixture<OperatorDetailsComponent>;

  class Page extends BasePage<OperatorDetailsComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorDetailsComponent, RouterTestingModule],
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
              operatorDetails: {
                operatorName: 'AviationName001',
                flightIdentification: {
                  icaoDesignators: 'D-CAPB\nD-CAWR',
                  flightIdentificationType: 'INTERNATIONAL_CIVIL_AVIATION_ORGANISATION',
                },
                organisationStructure: {
                  fullName: 'My name',
                  legalStatusType: 'INDIVIDUAL',
                  organisationLocation: {
                    city: 'town',
                    type: 'ONSHORE_STATE',
                    line1: 'My address 1',
                    line2: 'My address 2',
                    state: 'My state',
                    country: 'GR',
                    postcode: '11344',
                  },
                } as OrganisationStructure,
                airOperatingCertificate: {
                  certificateExist: false,
                },
              },
            },
          },
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(OperatorDetailsComponent);
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
      ['Aircraft operator name', 'AviationName001'],
      [
        'What call sign identification do you use for Air Traffic Control purposes?',
        'International Civil Aviation Organisation (ICAO) designators',
      ],
      ['ICAO designators used', 'D-CAPB\nD-CAWR'],
      ['Do you have an Air Operator Certificate (AOC) or equivalent?', 'No'],
      ['What is the legal status of your organisation?', 'Individual'],
      ['Full name', 'My name'],
      ['Contact address', 'My address 1  , My address 2 townMy state11344'],
    ]);
  });
});
