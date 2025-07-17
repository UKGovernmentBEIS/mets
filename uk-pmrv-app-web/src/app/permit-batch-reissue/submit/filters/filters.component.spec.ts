import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { initialState } from '../store/permit-batch-reissue.state';
import { PermitBatchReissueStore } from '../store/permit-batch-reissue.store';
import { mockSubmitCompletedState } from '../testing/mock-data';
import { FiltersComponent } from './filters.component';

describe('FiltersComponent', () => {
  let component: FiltersComponent;
  let fixture: ComponentFixture<FiltersComponent>;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitBatchReissueStore;

  class Page extends BasePage<FiltersComponent> {
    get accountStatusesCheckboxes() {
      return this.queryAll<HTMLInputElement>('input[type="checkbox"][name="accountStatuses"]');
    }

    get emitterTypesCheckboxes() {
      return this.queryAll<HTMLInputElement>('input[type="checkbox"][name="emitterTypes"]');
    }

    get installationCategories() {
      return this.queryAll<HTMLInputElement>('input[type="checkbox"][name="installationCategories"]');
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
      store = TestBed.inject(PermitBatchReissueStore);
      store.setState({
        ...initialState,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit when waste config is disabled', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      expect(page.accountStatusesCheckboxes.length).toEqual(3);
      expect(page.emitterTypesCheckboxes.length).toEqual(2);
      expect(page.installationCategories.length).toEqual(4);
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select a status', 'Select a permit type', 'Select a category']);

      page.accountStatusesCheckboxes[0].click(); // LIVE

      page.emitterTypesCheckboxes[0].click(); // HSE selected
      fixture.detectChanges();
      expect(page.installationCategories.length).toEqual(0);

      page.emitterTypesCheckboxes[1].click(); // GHGE selected
      fixture.detectChanges();
      expect(page.installationCategories.length).toEqual(4);

      page.installationCategories[1].click(); // A

      page.submitButton.click();
      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'signatory'], { relativeTo: route });
      expect(store.getState()).toEqual({
        accountStatuses: ['LIVE'],
        emitterTypes: ['HSE', 'GHGE'],
        installationCategories: ['A'],
        signatory: undefined,
      });
    });

    it('should submit if HSE only selected and no category exists', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.accountStatusesCheckboxes[0].click(); // LIVE

      page.emitterTypesCheckboxes[0].click(); // HSE selected
      fixture.detectChanges();
      expect(page.installationCategories.length).toEqual(0);

      page.submitButton.click();
      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'signatory'], { relativeTo: route });
      expect(store.getState()).toEqual({
        accountStatuses: ['LIVE'],
        emitterTypes: ['HSE'],
        installationCategories: null,
        signatory: undefined,
      });
    });
  });

  describe('for existing filters', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitBatchReissueStore);
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
      expect(page.accountStatusesCheckboxes[1].checked).toBeTruthy();
      expect(page.emitterTypesCheckboxes[1].checked).toBeTruthy();
      expect(page.installationCategories[0].checked).toBeTruthy();
    });
  });
});
