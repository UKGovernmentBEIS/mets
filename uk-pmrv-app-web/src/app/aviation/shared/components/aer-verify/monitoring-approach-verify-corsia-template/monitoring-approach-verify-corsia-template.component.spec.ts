import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MonitoringApproachVerifyCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/monitoring-approach-verify-corsia-template/monitoring-approach-verify-corsia-template.component';
import { BasePage } from '@testing';

describe('MonitoringApproachVerifyCorsiaTemplateComponent', () => {
  let page: Page;
  let component: MonitoringApproachVerifyCorsiaTemplateComponent;
  let fixture: ComponentFixture<MonitoringApproachVerifyCorsiaTemplateComponent>;

  class Page extends BasePage<MonitoringApproachVerifyCorsiaTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonitoringApproachVerifyCorsiaTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MonitoringApproachVerifyCorsiaTemplateComponent);
    component = fixture.componentInstance;

    component.opinionStatement = {
      fuelTypes: ['JET_KEROSENE', 'JET_GASOLINE'],
      monitoringApproachType: 'CERT_MONITORING',
      emissionsCorrect: false,
      manuallyInternationalFlightsProvidedEmissions: '1000',
      manuallyOffsettingFlightsProvidedEmissions: '2000',
    };
    component.totalEmissionsProvided = '1500';
    component.totalOffsetEmissionsProvided = '1200';

    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(7);
    expect(page.summaryValues).toEqual([
      [
        'Standard fuels and emission factors',
        `Jet kerosene (Jet A1 or Jet A) at 3.16 tCO2 per tonne of fuelJet gasoline (Jet B) at 3.10 tCO2 per tonne of fuel`,
      ],
      ['Monitoring approach', 'CERT only'],
      ['Emissions from all flights', '1500'],
      ['Emissions from offset flights', '1200'],
      ['Are the reported emissions correct?', 'No'],
      ['Total verified emissions for the scheme year', '1000'],
      ['Total verified emissions from offset flights for the scheme year', '2000'],
    ]);
  });
});
