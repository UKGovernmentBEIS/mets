import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { InherentCo2GroupComponent } from './inherent-co2-group.component';

describe('InherentCo2GroupComponent', () => {
  let page: Page;
  let component: InherentCo2GroupComponent;
  let fixture: ComponentFixture<InherentCo2GroupComponent>;

  class Page extends BasePage<InherentCo2GroupComponent> {
    get summaryList(): string[] {
      return this.queryAll<HTMLDListElement>('dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InherentCo2GroupComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InherentCo2GroupComponent);
    component = fixture.componentInstance;
    component.inherentInstallations = [
      {
        inherentCO2Direction: 'EXPORTED_TO_ETS_INSTALLATION',
        installationDetailsType: 'INSTALLATION_EMITTER',
        inherentReceivingTransferringInstallationDetailsType: {
          installationEmitter: {
            emitterId: 'EM12345',
            email: '1@o.com',
          },
        },
        measurementInstrumentOwnerTypes: ['INSTRUMENTS_BELONGING_TO_YOUR_INSTALLATION'],
        totalEmissions: '3',
      },
    ];
    page = new Page(fixture);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.summaryList).toEqual([
      'Direction of travel',
      'Exported to an ETS installation',
      'Installation emitter ID',
      'EM12345',
      'Contact email address',
      '1@o.com',
      'Measurement devices used',
      'Instruments belonging to your installation',
      'Reportable emissions',
      '3 tCO2e',
    ]);
  });
});
