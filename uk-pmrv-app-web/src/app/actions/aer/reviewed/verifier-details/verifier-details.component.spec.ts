import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { AerService } from '@actions/aer/core/aer.service';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { VerifierDetailsComponent } from './verifier-details.component';

describe('VerifierDetailsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: VerifierDetailsComponent;
  let fixture: ComponentFixture<VerifierDetailsComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'VERIFIER_DETAILS',
    },
  );

  class Page extends BasePage<VerifierDetailsComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        KeycloakService,
        provideRouter([]),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: route },
      ],
      imports: [AerModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockStateCompleted);

    const aerService = TestBed.inject(AerService);
    jest.spyOn(aerService, 'getEmissionsTradingSchemeSignal').mockReturnValue(signal('UK_ETS_INSTALLATIONS'));

    fixture = TestBed.createComponent(VerifierDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should expose the emissions trading scheme from the service', () => {
    expect(component.emissionsTradingScheme()).toBe('UK_ETS_INSTALLATIONS');
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Verifier details');
    expect(page.summaryListValues).toHaveLength(15);
    expect(page.summaryListValues).toEqual([
      ['Company name', 'My Verification Body'],
      ['Address', 'line town1231'],
      ['Accreditation number', 'Accreditation ref num'],
      ['National accreditation body', 'Accreditation name'],
      ['Name', 'VerifierAdminFirst VerifierAdminLast'],
      ['Email', 'verifieradmin@xx.gr'],
      ['Telephone number', '6995286257'],
      ['Lead ETS Auditor', 'Lead ETS Auditor'],
      ['ETS Auditors', 'ETS Auditors'],
      ['Technical Experts (ETS Auditor)', 'ETS Experts'],
      ['Independent Reviewer', 'reviewer'],
      ['Technical Experts (Independent Review)', 'Reviewer Experts'],
      ['Name of authorised signatory', 'Authorised signatory'],
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);
  });
});
