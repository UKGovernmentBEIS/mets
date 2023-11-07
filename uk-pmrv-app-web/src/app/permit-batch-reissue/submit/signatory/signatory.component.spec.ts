import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { SharedModule } from '../../../shared/shared.module';
import { initialState } from '../store/permit-batch-reissue.state';
import { PermitBatchReissueStore } from '../store/permit-batch-reissue.store';
import { mockSubmitCompletedState, regulators } from '../testing/mock-data';
import { SignatoryComponent } from './signatory.component';

describe('SignatoryComponent', () => {
  let component: SignatoryComponent;
  let fixture: ComponentFixture<SignatoryComponent>;

  let page: Page;
  let router: Router;
  let store: PermitBatchReissueStore;

  const activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
    regulators,
  });

  class Page extends BasePage<SignatoryComponent> {
    get signatoryOptions() {
      return this.queryAll<HTMLOptionElement>('select[name="signatory"] option');
    }
    get signatorySelect(): string {
      return this.getInputValue('#signatory');
    }
    set signatorySelect(value: string) {
      this.setInputValue('#signatory', value);
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
    fixture = TestBed.createComponent(SignatoryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SignatoryComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [DestroySubject, { provide: ActivatedRoute, useValue: activatedRouteStub }],
    }).compileComponents();
  });

  afterEach(() => jest.clearAllMocks());

  describe('for visiting for first time', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitBatchReissueStore);
      store.setState({
        ...initialState,
        ...mockSubmitCompletedState,
        signatory: undefined,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.signatoryOptions.map((opt) => opt.value)).toEqual(['0: 1reg', '1: 2reg', '2: 3reg']);

      expect(page.signatorySelect).toBeNull();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select a name to appear on the official notice document.']);

      page.signatorySelect = regulators.caUsers[1].userId;
      page.submitButton.click();
      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'summary'], { relativeTo: activatedRouteStub });
      expect(store.getState()).toEqual({
        ...mockSubmitCompletedState,
        signatory: regulators.caUsers[1].userId,
      });
    });
  });

  describe('for existing signatory', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitBatchReissueStore);
      store.setState({
        ...initialState,
        ...mockSubmitCompletedState,
        signatory: regulators.caUsers[1].userId,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.signatorySelect).toEqual(regulators.caUsers[1].userId);

      page.signatorySelect = regulators.caUsers[2].userId;
      page.submitButton.click();
      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..', 'summary'], { relativeTo: activatedRouteStub });
      expect(store.getState()).toEqual({
        ...mockSubmitCompletedState,
        signatory: regulators.caUsers[2].userId,
      });
    });
  });
});
