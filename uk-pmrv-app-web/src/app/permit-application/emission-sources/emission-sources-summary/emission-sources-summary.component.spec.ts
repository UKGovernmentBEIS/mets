import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { EmissionSourceTableComponent } from '@shared/components/emission-sources/emission-source-table/emission-source-table.component';

import { BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { EmissionSourcesSummaryComponent } from './emission-sources-summary.component';

describe('EmissionSourcesSummaryComponent', () => {
  let component: EmissionSourcesSummaryComponent;
  let fixture: ComponentFixture<EmissionSourcesSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  class Page extends BasePage<EmissionSourcesSummaryComponent> {
    get emissionSources() {
      return this.queryAll<HTMLDListElement>('tr').map((emisisonSource) =>
        Array.from(emisisonSource.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmissionSourcesSummaryComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, EmissionSourceTableComponent],
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
    fixture = TestBed.createComponent(EmissionSourcesSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of data', () => {
    expect(page.emissionSources).toEqual([[], ['S1', 'Boiler', '', ''], ['S2', 'Boiler 2', '', '']]);
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });
});
