import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../shared/shared.module';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../testing/mock-state';
import { EnvironmentalSystemComponent } from './environmental-system.component';

describe('EnvironmentalSystemComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let activatedRoute: ActivatedRoute;
  let component: EnvironmentalSystemComponent;
  let fixture: ComponentFixture<EnvironmentalSystemComponent>;

  const tasksService = mockClass(TasksService);
  const mockEnvironmentalManagementSystem = mockPermitApplyPayload.permit.environmentalManagementSystem;

  class Page extends BasePage<EnvironmentalSystemComponent> {
    get existsOption() {
      return this.query<HTMLInputElement>('#exist-option0');
    }

    get certifiedOption() {
      return this.query<HTMLInputElement>('#certified-option0');
    }

    get certificationStandardValue() {
      return this.getInputValue('#certificationStandard');
    }

    set certificationStandardValue(value: string) {
      this.setInputValue('#certificationStandard', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryText() {
      return this.errorSummary.querySelectorAll('a')[0].textContent.trim();
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EnvironmentalSystemComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(EnvironmentalSystemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for new environmental system', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(mockStateBuild({ environmentalManagementSystem: null }));
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryText).toBe('Select yes or no');

      page.existsOption.click();
      fixture.detectChanges();
      page.submitButton.click();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryText).toBe('Select yes or no');

      page.certifiedOption.click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryText).toBe('Enter a certification standard');

      page.certificationStandardValue = mockEnvironmentalManagementSystem.certificationStandard;

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(undefined, { environmentalManagementSystem: [true] }),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });

  describe('for existing environmental system', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should filled the form with the correct values from store', () => {
      expect(page.existsOption.checked).toBeTruthy();
      expect(page.certifiedOption.checked).toBeTruthy();
      expect(page.certificationStandardValue).toBe(mockEnvironmentalManagementSystem.certificationStandard);

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(undefined, { environmentalManagementSystem: [true] }),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });
});
