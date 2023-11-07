import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-submitted';
import { MeasurementTierComponent } from './measurement-tier.component';

describe('MeasurementTierComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: MeasurementTierComponent;
  let fixture: ComponentFixture<MeasurementTierComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey: 'MEASUREMENT_CO2',
    },
  );

  class Page extends BasePage<MeasurementTierComponent> {
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
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [AerModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(MeasurementTierComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('EP1');

    expect(page.summaryListValues).toHaveLength(19);
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
      ['Tier used', 'Tier 3'],
      ['Reason', 'Due to a data gap  reason'],
      ['Annual hourly average amount of CO2 in the flue gas', '33 hours'],
      ['Annual hourly average flue gas flow', '34 (1000/Nm3)'],
      ['Annual flue gas flow', '10 (1000/Nm3)'],
      ['Global warming potential of the greenhouse gas', '1 tCO2/GHG'],
      ['Annual fossil amount of greenhouse gas', '37.026 tonnes'],
      ['Reportable emissions', '37.026 tonnes CO2e'],
      ['Sustainable biomass', '0 tonnes CO2e'],
      ['Are the calculated emissions correct?', 'Yes'],
    ]);
  });
});
