import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-submitted';
import { EmissionsSummaryComponent } from './emissions-summary.component';

describe('EmissionsSummaryComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: EmissionsSummaryComponent;
  let fixture: ComponentFixture<EmissionsSummaryComponent>;

  class Page extends BasePage<EmissionsSummaryComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get emissions() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [AerModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

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
