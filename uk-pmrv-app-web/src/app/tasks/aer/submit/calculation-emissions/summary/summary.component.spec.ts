import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerModule } from '../../../aer.module';
import { mockState } from '../../testing/mock-aer-apply-action';
import { CalculationEmissionsModule } from '../calculation-emissions.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: CommonTasksStore;
  let page: Page;

  class Page extends BasePage<SummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get headings() {
      return this.queryAll<HTMLHeadingElement>('h2').map((el) => el.textContent.trim());
    }
  }

  const createComponent = () => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, CalculationEmissionsModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => createComponent());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the summary details', () => {
    expect(page.summaryListValues).toEqual([
      ['Source stream', 'the reference Anthracite'],
      ['Emission sources', 'emission source 1 reference emission source 1 description'],
      ['Does the source stream contain biomass?', 'No'],
      ['Date range for this entry', 'the whole year'],
      ['Are the emissions from this source stream exported to, or received from another installation?', 'Yes'],
      [
        'What direction is the transferred CO2 travelling?',
        'Exported to a long-term geological storage related facility',
      ],
      ['Installation emitter ID', '34'],
      ['Contact email address', 'permitsubmit1@trasys.gr'],
      ['Calculation method', 'Calculate the values manually'],
      ['Reason', 'Due to a data gap  reason'],
      ['Tier used', 'No tier'],
      ['Value', '3 tCO2/Nm3'],
      ['Tier used', 'No tier'],
      ['How do you want to calculate the activity data for this source stream?', 'Continuous metering'],
      ['Total fuel or material used', '2 normal cubic meter (Nm3)'],
      ['Tier used', 'No tier'],
      ['Value', '4 GJ/Nm3'],
      ['Tier used', 'No tier'],
      ['Value', '3'],
      ['Reportable emissions', '18 tonnes CO2e'],
      ['Are the calculated emissions correct?', 'Yes'],
    ]);

    expect(page.headings).toEqual([
      'Emission network',
      'Transferred CO2',
      'Parameter values',
      'Reason for not using monitoring plan tiers',
      'Emission factor',
      'Activity data',
      'Net calorific value',
      'Oxidation factor',
      'Calculated emissions',
    ]);
  });
});
