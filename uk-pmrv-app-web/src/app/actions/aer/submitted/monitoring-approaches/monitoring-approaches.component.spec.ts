import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-submitted';
import { MonitoringApproachesComponent } from './monitoring-approaches.component';

describe('MonitoringApproachesComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: MonitoringApproachesComponent;
  let fixture: ComponentFixture<MonitoringApproachesComponent>;

  class Page extends BasePage<MonitoringApproachesComponent> {
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
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(MonitoringApproachesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Monitoring approaches used during the reporting year');
    expect(page.summaryListValues).toHaveLength(1);
    expect(page.summaryListValues).toEqual([
      ['Approaches used', 'Calculation of CO2 Measurement of CO2 Fallback approach Inherent CO2 emissions'],
    ]);
  });
});
