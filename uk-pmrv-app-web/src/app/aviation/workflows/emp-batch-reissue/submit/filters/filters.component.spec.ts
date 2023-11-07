import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { initialState } from '@aviation/workflows/emp-batch-reissue/submit/store/emp-batch-reissue.state';
import { EmpBatchReissueStore } from '@aviation/workflows/emp-batch-reissue/submit/store/emp-batch-reissue.store';
import { mockSubmitCompletedState } from '@aviation/workflows/emp-batch-reissue/submit/testing/mock-data';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { FiltersComponent } from './filters.component';

describe('FiltersComponent', () => {
  let component: FiltersComponent;
  let fixture: ComponentFixture<FiltersComponent>;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: EmpBatchReissueStore;

  class Page extends BasePage<FiltersComponent> {
    get reportingStatusesCheckboxes() {
      return this.queryAll<HTMLInputElement>('input[type="checkbox"][name="reportingStatuses"]');
    }

    get emissionTradingSchemesCheckboxes() {
      return this.queryAll<HTMLInputElement>('input[type="checkbox"][name="emissionTradingSchemes"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(FiltersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FiltersComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [DestroySubject],
    }).compileComponents();
  });

  afterEach(() => jest.clearAllMocks());

  describe('for visiting filters for first time', () => {
    beforeEach(() => {
      store = TestBed.inject(EmpBatchReissueStore);
      store.setState({
        ...initialState,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      expect(page.reportingStatusesCheckboxes.length).toEqual(3);
      expect(page.emissionTradingSchemesCheckboxes.length).toEqual(2);
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select a status', 'Select a scheme']);

      page.reportingStatusesCheckboxes[0].click(); // Required to report
      page.reportingStatusesCheckboxes[1].click(); // Exempt (commercial)

      page.emissionTradingSchemesCheckboxes[0].click(); // UK ETS
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'signatory'], { relativeTo: route });
      expect(store.getState()).toEqual({
        reportingStatuses: ['REQUIRED_TO_REPORT', 'EXEMPT_COMMERCIAL'],
        emissionTradingSchemes: ['UK_ETS_AVIATION'],
        signatory: undefined,
      });
    });
  });

  describe('for existing filters', () => {
    beforeEach(() => {
      store = TestBed.inject(EmpBatchReissueStore);
      store.setState({
        ...initialState,
        ...mockSubmitCompletedState,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should populate form', () => {
      expect(page.reportingStatusesCheckboxes[0].checked).toBeTruthy();
      expect(page.reportingStatusesCheckboxes[1].checked).toBeTruthy();
      expect(page.emissionTradingSchemesCheckboxes[0].checked).toBeTruthy();
    });
  });
});
