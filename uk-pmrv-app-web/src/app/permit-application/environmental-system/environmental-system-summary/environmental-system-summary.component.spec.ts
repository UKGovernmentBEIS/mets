import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { EnvironmentalSystemSummaryComponent } from './environmental-system-summary.component';

describe('EnvironmentalSystemSummaryComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: EnvironmentalSystemSummaryComponent;
  let fixture: ComponentFixture<EnvironmentalSystemSummaryComponent>;

  class Page extends BasePage<EnvironmentalSystemSummaryComponent> {
    get summaryListValues() {
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
      declarations: [EnvironmentalSystemSummaryComponent],
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
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);

    fixture = TestBed.createComponent(EnvironmentalSystemSummaryComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the sumary page list', () => {
    const mockEnvironmentalManagementSystem = mockPermitApplyPayload.permit.environmentalManagementSystem;

    expect(page.summaryListValues).toEqual([
      ['Documented environmental management system', mockEnvironmentalManagementSystem.exist ? 'Yes' : 'No'],
      ['Externally certified', mockEnvironmentalManagementSystem.certified ? 'Yes' : 'No'],
      ['Certification standard', mockEnvironmentalManagementSystem.certificationStandard],
    ]);
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });
});
