import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { mockAerApplyPayload } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { InherentCO2Emissions } from 'pmrv-api';

import { AerInherentSummaryTemplateComponent } from './aer-inherent-summary-template.component';

describe('AerInherentSummaryTemplateComponent', () => {
  let component: AerInherentSummaryTemplateComponent;
  let fixture: ComponentFixture<AerInherentSummaryTemplateComponent>;
  let page: Page;

  class Page extends BasePage<AerInherentSummaryTemplateComponent> {
    get inherentSummaries() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(AerInherentSummaryTemplateComponent);
    component = fixture.componentInstance;
  };

  describe('show summary page', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.inherentInstallations = (
        mockAerApplyPayload.aer.monitoringApproachEmissions.INHERENT_CO2 as InherentCO2Emissions
      ).inherentReceivingTransferringInstallations.map((item) => item.inherentReceivingTransferringInstallation);

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the list of data', () => {
      expect(page.inherentSummaries).toEqual([
        ['Item', ''],
        ['Direction of travel', 'Exported to an ETS installation'],
        ['Installation emitter ID', 'EM12345'],
        ['Contact email address', '1@o.com'],
        ['Measurement devices used', 'Instruments belonging to your installation'],
        ['Reportable emissions', '3 tCO2e'],
        ['Item', ''],
        ['Direction of travel', 'Exported to a non-ETS consumer'],
        ['Installation name', 'Test installation'],
        ['Installation address', 'Test street Berlin54555'],
        ['Measurement devices used', 'Instruments belonging to the other installation'],
        ['Reportable emissions', '1 tCO2e'],
      ]);
    });
  });
});
