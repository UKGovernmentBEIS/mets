import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { InherentCO2MonitoringApproach } from 'pmrv-api';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { SharedPermitModule } from '../../../shared/shared-permit.module';
import { mockState } from '../../../testing/mock-state';
import { InherentSummaryTemplateComponent } from './inherent-summary-template.component';

describe('InherentSummaryTemplateComponent', () => {
  let component: InherentSummaryTemplateComponent;
  let fixture: ComponentFixture<InherentSummaryTemplateComponent>;
  let page: Page;

  class Page extends BasePage<InherentSummaryTemplateComponent> {
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
      declarations: [InherentSummaryTemplateComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(InherentSummaryTemplateComponent);
    component = fixture.componentInstance;
  };

  describe('show summary page', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.data = (
        mockState.permit.monitoringApproaches.INHERENT_CO2 as InherentCO2MonitoringApproach
      ).inherentReceivingTransferringInstallations;

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
        ['Direction of travel', 'Exported to a non-ETS consumer'],

        ['Installation name', 'Angela Mcdowell'],
        ['Installation address', '997 Rocky Fabien Avenue, Qui consequat Non r Est libero nulla cuQuaerat in amet qua'],
        ['Measurement devices used', 'Instruments belonging to your installation'],
        ['Estimated emissions', '1'],
      ]);
    });
  });
});
