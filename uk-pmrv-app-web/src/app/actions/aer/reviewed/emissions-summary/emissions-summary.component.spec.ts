import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { EmissionsSummaryComponent } from './emissions-summary.component';

describe('EmissionsSummaryComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: EmissionsSummaryComponent;
  let fixture: ComponentFixture<EmissionsSummaryComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'EMISSIONS_SUMMARY',
    },
  );

  class Page extends BasePage<EmissionsSummaryComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get emissions() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [AerModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockStateCompleted);

    fixture = TestBed.createComponent(EmissionsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Emissions summary');
    expect(page.summaryListValues).toHaveLength(2);
    expect(page.summaryListValues).toEqual([
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);

    expect(page.emissions).toEqual([
      [],
      ['Calculation of combustion', '18  t', '0  t'],
      ['Measurement of CO2', '0  t', '0  t'],
      ['Measurement of transferred CO2', '-37.026  t', '0  t'],
      ['Fallback', '9.9  t  (includes 8.8 t non-sustainable biomass)', '3.3  t'],
      ['Total', '-9.126  tCO2e', '3.3  tCO2e'],
      ['Inherent CO2', '-4  t', '0  t'],
    ]);
  });
});
