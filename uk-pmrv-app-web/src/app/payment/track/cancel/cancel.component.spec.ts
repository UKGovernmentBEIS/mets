import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { PaymentCancelRequestTaskActionPayload, TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../testing';
import { PaymentModule } from '../../payment.module';
import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { CancelComponent } from './cancel.component';

describe('CancelComponent', () => {
  let component: CancelComponent;
  let fixture: ComponentFixture<CancelComponent>;
  let store: PaymentStore;
  let page: Page;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<CancelComponent> {
    set reasonValue(value: string) {
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
    get confirmationTitle() {
      return this.query<HTMLHeadingElement>('h1.govuk-panel__title');
    }

    get confirmationHintTitle() {
      return this.query<HTMLHeadingElement>('h2.govuk-heading-m');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, PaymentModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for permit issuance', () => {
    beforeEach(() => {
      store = TestBed.inject(PaymentStore);
      store.setState({
        ...mockPaymentState,
        requestTaskItem: {
          ...mockPaymentState.requestTaskItem,
          allowedRequestTaskActions: ['PAYMENT_MARK_AS_RECEIVED', 'PAYMENT_CANCEL'],
          requestTask: {
            type: 'PERMIT_ISSUANCE_TRACK_PAYMENT',
          },
        },
        paymentDetails: {
          amount: '2500.2',
          paymentRefNum: 'AEM-323-1',
        },
        markedAsPaid: true,
      });
      fixture = TestBed.createComponent(CancelComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      jest.clearAllMocks();
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form and show cancel notification ', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.confirmationTitle).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter the reason that no payment is required']);

      page.reasonValue = 'A reason';

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PAYMENT_CANCEL',
        requestTaskId: 500,
        requestTaskActionPayload: {
          payloadType: 'PAYMENT_CANCEL_PAYLOAD',
          reason: 'A reason',
        } as PaymentCancelRequestTaskActionPayload,
      });

      expect(page.confirmationTitle).toBeTruthy();
      expect(page.confirmationTitle.textContent).toEqual('Payment task cancelled');
      expect(page.confirmationHintTitle.textContent).toEqual('What happens next');
    });
  });

  describe('for dre', () => {
    beforeEach(() => {
      store = TestBed.inject(PaymentStore);
      store.setState({
        ...mockPaymentState,
        competentAuthority: 'ENGLAND',
        requestType: 'DRE',
        requestTaskItem: {
          ...mockPaymentState.requestTaskItem,
          allowedRequestTaskActions: ['PAYMENT_MARK_AS_RECEIVED', 'PAYMENT_CANCEL'],
          requestTask: {
            ...mockPaymentState.requestTaskItem.requestTask,
            type: 'DRE_TRACK_PAYMENT',
          },
          requestInfo: {
            ...mockPaymentState.requestTaskItem.requestInfo,
            type: 'DRE',
            requestMetadata: {
              type: 'DRE',
              year: '2022',
              emissions: '9.00000',
            } as any,
          },
        },
        paymentDetails: {
          amount: '2500.2',
          paymentRefNum: 'AEM-323-1',
        },
        markedAsPaid: true,
      });
      fixture = TestBed.createComponent(CancelComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      jest.clearAllMocks();
      fixture.detectChanges();
    });

    it('should submit a valid form and show cancel notification ', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.confirmationTitle).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter the reason that no payment is required']);

      page.reasonValue = 'A reason';

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PAYMENT_CANCEL',
        requestTaskId: 500,
        requestTaskActionPayload: {
          payloadType: 'PAYMENT_CANCEL_PAYLOAD',
          reason: 'A reason',
        } as PaymentCancelRequestTaskActionPayload,
      });

      expect(page.confirmationTitle).toBeTruthy();
      expect(page.confirmationTitle.textContent).toEqual('Payment task cancelled');
      expect(page.confirmationHintTitle).toBeNull();
    });
  });
});
