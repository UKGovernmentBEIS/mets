import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../testing';
import { SharedPermitModule } from '../../../permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '../../../permit-application/store/permit-application.store';
import { SharedModule } from '../../../shared/shared.module';
import { PermitVariationStore } from '../../store/permit-variation.store';
import { mockPermitVariationSubmitOperatorLedPayload } from '../../testing/mock';
import { ChangesComponent } from './changes.component';

describe('ChangesComponent', () => {
  let store: PermitVariationStore;
  let component: ChangesComponent;
  let fixture: ComponentFixture<ChangesComponent>;
  let page: Page;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ChangesComponent> {
    get changeCompany() {
      return this.query<HTMLInputElement>('#nonSignificantChanges-0');
    }
    get changeCategory() {
      return this.query<HTMLInputElement>('#significantChangesMonitoringPlan-0');
    }
    get modifications() {
      return this.query<HTMLInputElement>('#significantChangesMonitoringMethodologyPlan-0');
    }

    set fileValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }
    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ChangesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChangesComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitVariationStore,
        },
      ],
    }).compileComponents();
  });

  describe('for new changes', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        permitVariationDetails: null,
        permitVariationDetailsCompleted: false,
      });
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['You must select at least one change']);

      page.changeCompany.click();
      page.changeCategory.click();
      page.modifications.click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_VARIATION_SAVE_APPLICATION',
        requestTaskId: mockPermitVariationSubmitOperatorLedPayload.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_SAVE_APPLICATION_PAYLOAD',
          permit: mockPermitVariationSubmitOperatorLedPayload.permit,
          permitSectionsCompleted: mockPermitVariationSubmitOperatorLedPayload.permitSectionsCompleted,
          permitVariationDetails: {
            modifications: [
              {
                type: 'COMPANY_NAME_NOT_DUE_TO_OWNERSHIP_CHANGE',
              },
              {
                type: 'INSTALLATION_CATEGORY',
              },
              {
                type: 'INSTALLATION_SUB',
              },
            ],
          },
          permitVariationDetailsCompleted: false,
          reviewSectionsCompleted: {
            ...mockPermitVariationSubmitOperatorLedPayload.reviewSectionsCompleted,
            determination: false,
          },
        },
      });
    });
  });

  describe('for existing changes', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        permitVariationDetails: {
          reason: '',
          modifications: [
            {
              type: 'COMPANY_NAME_NOT_DUE_TO_OWNERSHIP_CHANGE',
            },
            {
              type: 'INSTALLATION_CATEGORY',
            },
            {
              type: 'INSTALLATION_SUB',
            },
          ],
        },
        permitVariationDetailsCompleted: false,
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      expect(page.changeCompany.checked).toBeTruthy();
      expect(page.changeCategory.checked).toBeTruthy();
      expect(page.modifications.checked).toBeTruthy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_VARIATION_SAVE_APPLICATION',
        requestTaskId: mockPermitVariationSubmitOperatorLedPayload.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_SAVE_APPLICATION_PAYLOAD',
          permit: mockPermitVariationSubmitOperatorLedPayload.permit,
          permitSectionsCompleted: mockPermitVariationSubmitOperatorLedPayload.permitSectionsCompleted,
          permitVariationDetails: {
            reason: '',
            modifications: [
              {
                type: 'COMPANY_NAME_NOT_DUE_TO_OWNERSHIP_CHANGE',
              },
              {
                type: 'INSTALLATION_CATEGORY',
              },
              {
                type: 'INSTALLATION_SUB',
              },
            ],
          },
          permitVariationDetailsCompleted: false,
          reviewSectionsCompleted: {
            ...mockPermitVariationSubmitOperatorLedPayload.reviewSectionsCompleted,
            determination: false,
          },
        },
      });
    });
  });
});
