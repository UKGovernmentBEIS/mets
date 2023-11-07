import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerModule } from '../../../aer.module';
import { mockState } from '../../testing/mock-aer-apply-action';
import { PfcModule } from '../pfc.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: CommonTasksStore;
  let page: Page;
  const route = new ActivatedRouteStub({}, {}, null);
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
      imports: [SharedModule, AerModule, RouterTestingModule, PfcModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
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
      ['Method to calculate emissions', 'Method A (Slope)'],
      ['Are you using a mass balance approach to identify the activity data?', 'Yes'],
      ['Date range for this entry', 'the whole year'],
      ['Reason', 'Due to a data gap  erte'],
      ['Tier applied in the monitoring plan', 'Tier 1'],
      ['Tier used', 'Tier 1'],
      ['Total production of primary aluminium', '34 tonnes'],
      ['Tier applied in the monitoring plan', 'Tier 2'],
      ['Tier used', 'Tier 1'],
      ['Amount of anode effects per cell-day', '1'],
      ['Average duration of anode effects, in minutes', '2'],
      ['Slope emission factor of CF4', '3'],
      ['Weight fraction of C2F6', '4'],
      ['Collection efficiency', '3'],
      ['Amount of CF4', '0.204'],
      ['Global warming potential', '7390 tonnes CO2 / tonnes CF4'],
      ['Total CF4 emissions', '1507.56'],
      ['Amount of C2F6', '27.744'],
      ['Global warming potential', '12,000 tonnes CO2/ tonnes C2F6'],
      ['Total CF4 emissions', '338476.8'],
      ['Reportable emissions', '11332812 tonnes CO2e'],
      ['Are the calculated emissions correct?', 'Yes'],
    ]);

    expect(page.headings).toEqual([
      'Emission network',
      'Reason for not using monitoring plan tiers',
      'Activity data',
      'Emission factor',
      'Calculation data',
      'CF4 (Tetrafluoromethane)',
      'C2F6 (Hexafluoroethane)',
      'Calculated emissions',
    ]);
  });
});
