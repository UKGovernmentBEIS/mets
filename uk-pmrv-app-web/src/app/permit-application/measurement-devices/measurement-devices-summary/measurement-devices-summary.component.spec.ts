import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { MeasurementDevicesSummaryComponent } from './measurement-devices-summary.component';

describe('MeasurementDevicesSummaryComponent', () => {
  let component: MeasurementDevicesSummaryComponent;
  let fixture: ComponentFixture<MeasurementDevicesSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  class Page extends BasePage<MeasurementDevicesSummaryComponent> {
    get measurementDevices() {
      return this.queryAll<HTMLDListElement>('dl').map((measurementDevice) =>
        Array.from(measurementDevice.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeasurementDevicesSummaryComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(MeasurementDevicesSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of data', () => {
    expect(page.measurementDevices).toEqual([
      ['ref1', 'Ultrasonic meter', '3', 'litres', '± 2.0 %', 'north terminal'],
      ['ref2', 'Ultrasonic meter', '3', 'litres', 'None', 'north terminal'],
    ]);
  });
});
