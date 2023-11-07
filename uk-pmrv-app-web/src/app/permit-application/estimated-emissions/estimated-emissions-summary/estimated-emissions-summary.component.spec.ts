import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { EstimatedEmissionsSummaryComponent } from './estimated-emissions-summary.component';

describe('EstimatedEmissionsSummaryComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: EstimatedEmissionsSummaryComponent;
  let fixture: ComponentFixture<EstimatedEmissionsSummaryComponent>;

  class Page extends BasePage<EstimatedEmissionsSummaryComponent> {
    get labels() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__key');
    }

    get values() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__value');
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EstimatedEmissionsSummaryComponent],
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

    store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(EstimatedEmissionsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the selected category', () => {
    expect(page.labels.map((el) => el.textContent.trim())).toEqual([
      'What is the installation’s estimated annual CO2e?',
      'Installation Category',
    ]);
    expect(page.values.map((el) => el.textContent.trim())).toEqual(['33 tonnes', 'Category A (Low emitter)']);
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });
});
