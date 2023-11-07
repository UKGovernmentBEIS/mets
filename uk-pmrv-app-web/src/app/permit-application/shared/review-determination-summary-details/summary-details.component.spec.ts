import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockState } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { ReviewDeterminationSummaryDetailsComponent } from './summary-details.component';

describe('SummaryDetailsComponent', () => {
  let component: ReviewDeterminationSummaryDetailsComponent;
  let fixture: ComponentFixture<ReviewDeterminationSummaryDetailsComponent>;
  let page: Page;
  let store: PermitIssuanceStore;

  class Page extends BasePage<ReviewDeterminationSummaryDetailsComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ReviewDeterminationSummaryDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule, SharedModule],
      declarations: [ReviewDeterminationSummaryDetailsComponent],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();

    store = TestBed.inject(PermitIssuanceStore);
  });

  describe('for determination GRANTED', () => {
    beforeEach(() => {
      store.setState({
        ...mockState,
        determination: {
          type: 'GRANTED',
          reason: 'requirements are fulfilled',
          activationDate: new Date('2030-01-01T00:00:00Z').toISOString(),
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the determination summary', () => {
      expect(page.summaryDefinitions).toEqual([
        'Grant',
        'Change',
        'requirements are fulfilled',
        'Change',
        '1 Jan 2030',
        'Change',
      ]);
    });
  });
});
