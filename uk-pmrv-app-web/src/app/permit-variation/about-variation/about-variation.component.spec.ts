import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { SharedPermitModule } from '../../permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '../../permit-application/store/permit-application.store';
import { SharedModule } from '../../shared/shared.module';
import { PermitVariationStore } from '../store/permit-variation.store';
import { mockPermitVariationSubmitOperatorLedPayload } from '../testing/mock';
import { AboutVariationComponent } from './about-variation.component';

describe('AboutVariationComponent', () => {
  let page: Page;
  let store: PermitVariationStore;
  let component: AboutVariationComponent;
  let fixture: ComponentFixture<AboutVariationComponent>;

  const tasksService = mockClass(TasksService);
  class Page extends BasePage<AboutVariationComponent> {
    get reason() {
      return this.getInputValue('#reason');
    }

    set reason(value: string) {
      this.setInputValue('#reason', value);
    }

    get submitButton() {
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
    fixture = TestBed.createComponent(AboutVariationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AboutVariationComponent],
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

  describe('for new about the variation', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        permitVariationDetails: null,
        permitVariationDetailsCompleted: false,
      });
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
      expect(page.errorSummaryErrorList).toEqual(['Enter details of the change']);

      page.reason = 'Reason';

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
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
            reason: 'Reason',
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

  describe('for existing about the variation', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        permitVariationDetails: { reason: 'Reason', modifications: [] },
        permitVariationDetailsCompleted: false,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill the form from the store', () => {
      expect(page.reason).toEqual(store.getState().permitVariationDetails.reason);
    });

    it('should submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.reason = '';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter details of the change']);

      page.reason = 'New reason';

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      page.submitButton.click();
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
            reason: 'New reason',
            modifications: [],
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
