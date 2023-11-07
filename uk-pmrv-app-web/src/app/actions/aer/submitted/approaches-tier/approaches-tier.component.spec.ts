import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-submitted';
import { ApproachesTierComponent } from './approaches-tier.component';

describe('ApproachesTierComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ApproachesTierComponent;
  let fixture: ComponentFixture<ApproachesTierComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey: 'CALCULATION_CO2',
    },
  );

  class Page extends BasePage<ApproachesTierComponent> {
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
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(ApproachesTierComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('the reference Anthracite');

    expect(page.summaryListValues).toHaveLength(17);
    expect(page.summaryListValues).toEqual([
      ['Source stream', 'the reference Anthracite'],
      ['Emission sources', 'emission source 1 reference emission source 1 description'],
      ['Does the source stream contain biomass?', 'No'],
      ['Date range for this entry', 'the whole year'],
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
  });
});
