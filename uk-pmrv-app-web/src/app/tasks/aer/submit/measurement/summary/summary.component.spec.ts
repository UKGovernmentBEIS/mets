import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerModule } from '../../../aer.module';
import { mockState } from '../../testing/mock-aer-apply-action';
import { MeasurementModule } from '../measurement.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: CommonTasksStore;
  let page: Page;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey: 'MEASUREMENT_CO2',
    },
  );
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
      imports: [SharedModule, AerModule, RouterTestingModule, MeasurementModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => createComponent());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the summary details', () => {
    expect(page.summaryListValues).toEqual([
      ['Emission point', 'EP1 west side chimney'],
      ['Source streams', 'the reference Anthracite'],
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
      ['Tier applied in the monitoring plan', 'Tier 3'],
      ['Tier used', 'Tier 3'],
      ['Reason', 'Due to a data gap  reason'],
      ['Annual hourly average amount of CO2 in the flue gas', '33 hours'],
      ['Annual hourly average flue gas flow', '34 (1000/Nm3)'],
      ['Annual flue gas flow', '8 (1000/Nm3)'],
      ['Global warming potential of the greenhouse gas', '1 tCO2/GHG'],
      ['Annual fossil amount of greenhouse gas', '37.026 tonnes'],
      ['Reportable emissions', '37.026 tonnes CO2e'],
      ['Sustainable biomass', '0 tonnes CO2e'],
      ['Are the calculated emissions correct?', 'Yes'],
    ]);

    expect(page.headings).toEqual([
      'Emission network',
      'Transferred CO2',
      'Tiers',
      'Reason for not using monitoring plan tiers',
      'Measurement data',
      'Calculated emissions',
    ]);
  });
});
