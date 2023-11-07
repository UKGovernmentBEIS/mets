import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CountryService } from '@core/services/country.service';
import { BasePage, CountryServiceStub } from '@testing';

import { LocationOnShoreDTO } from 'pmrv-api';

import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../shared/shared.module';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockState } from '../testing/mock-state';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let page: Page;
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let store: PermitApplicationStore<PermitApplicationState>;

  class Page extends BasePage<DetailsComponent> {
    get summaryPairText() {
      return Array.from(this.queryAll<HTMLDivElement>('.govuk-summary-list__row'))
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .filter(([, data]) => !!data)
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DetailsComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: CountryService, useClass: CountryServiceStub },
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

    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary', () => {
    const location = mockState.installationOperatorDetails.installationLocation as LocationOnShoreDTO;

    expect(page.summaryPairText).toEqual([
      ['Installation name', mockState.installationOperatorDetails.installationName],
      ['Site name', mockState.installationOperatorDetails.siteName],
      [
        'Installation address',
        `${location.gridReference} ${location.address.line1}, ${location.address.line2}${location.address.city}${location.address.postcode}Greece`,
      ],

      ['Operator name', mockState.installationOperatorDetails.operator],
      ['Legal status', 'Limited Company'],
      ['Company registration number', mockState.installationOperatorDetails.companyReferenceNumber],
      [
        'Operator address',
        `${mockState.installationOperatorDetails.operatorDetailsAddress.line1}, ${mockState.installationOperatorDetails.operatorDetailsAddress.line2} ${mockState.installationOperatorDetails.operatorDetailsAddress.city}${mockState.installationOperatorDetails.operatorDetailsAddress.postcode}Greece`,
      ],
    ]);
  });
});
