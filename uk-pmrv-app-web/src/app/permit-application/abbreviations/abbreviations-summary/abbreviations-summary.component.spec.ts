import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { AbbreviationsSummaryComponent } from './abbreviations-summary.component';

describe('AbbreviationsSummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: AbbreviationsSummaryComponent;
  let fixture: ComponentFixture<AbbreviationsSummaryComponent>;

  class Page extends BasePage<AbbreviationsSummaryComponent> {
    get abbreviationDefinitions() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }

    get summaryHeader() {
      return this.query<HTMLElement>('h2 span');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AbbreviationsSummaryComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(AbbreviationsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  describe('abbreviation definitions summary', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(mockState);
      router = TestBed.inject(Router);
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the abbreviation definitions', () => {
      expect(page.abbreviationDefinitions).toHaveLength(4);
      expect(page.abbreviationDefinitions).toEqual([
        ['Abbreviation, acronym or terminology', 'Mr'],
        ['Definition', 'Mister'],
        ['Abbreviation, acronym or terminology', 'Ms'],
        ['Definition', 'Miss'],
      ]);
      expect(page.notificationBanner).toBeTruthy();
      expect(page.summaryHeader.textContent).toEqual('Items that need definition');
      expect(page.summaryHeader.classList).not.toContain('govuk-visually-hidden');
    });
  });
});
