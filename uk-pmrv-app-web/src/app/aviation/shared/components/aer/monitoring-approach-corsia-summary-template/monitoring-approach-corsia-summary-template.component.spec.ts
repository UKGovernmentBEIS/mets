import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';

import { AviationAerCorsiaMonitoringApproach } from 'pmrv-api';

import { MonitoringApproachCorsiaSummaryTemplateComponent } from './monitoring-approach-corsia-summary-template.component';

describe('MonitoringApproachCorsiaSummaryTemplateComponent', () => {
  let component: MonitoringApproachCorsiaSummaryTemplateComponent;
  let fixture: ComponentFixture<MonitoringApproachCorsiaSummaryTemplateComponent>;
  let page: Page;

  const activatedRouteStub = new ActivatedRouteStub();
  const monitoringApproach = {
    certUsed: false,
    fuelUseMonitoringDetails: {
      fuelDensityType: 'ACTUAL_DENSITY',
      identicalToProcedure: true,
      blockHourUsed: true,
      aircraftTypeDetails: [
        {
          designator: 'C560',
          subtype: 'test',
          fuelBurnRatio: '1.2',
        },
        {
          designator: 'A320',
          subtype: 'test',
          fuelBurnRatio: '1',
        },
      ],
    },
  } as AviationAerCorsiaMonitoringApproach;

  class Page extends BasePage<MonitoringApproachCorsiaSummaryTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get appAircraftFuelBurnRatioTable() {
      return this.query<HTMLDivElement>('app-aircraft-fuel-burn-ratio-table');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonitoringApproachCorsiaSummaryTemplateComponent, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteStub }],
    }).compileComponents();

    fixture = TestBed.createComponent(MonitoringApproachCorsiaSummaryTemplateComponent);
    component = fixture.componentInstance;

    component.data = monitoringApproach;

    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(4);
    expect(page.summaryValues).toEqual([
      [
        'Other than for data gaps, did you use the CORSIA CO2 estimation and reporting tool (CERT) for your monitoring approach?',
        'No',
      ],
      ['Which fuel density type was used to determine fuel uplift in the reporting year?', 'Actual density'],
      [
        'Is the application of density data identical to the procedure used for operational and safety reasons in the emissions monitoring plan?',
        'Yes',
      ],
      ['Was fuel allocation by block hour used during the reporting year?', 'Yes'],
    ]);
    expect(page.appAircraftFuelBurnRatioTable).toBeTruthy();
  });
});
