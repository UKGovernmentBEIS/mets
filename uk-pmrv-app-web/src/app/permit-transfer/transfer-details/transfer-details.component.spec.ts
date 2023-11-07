import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { PermitTransferStore } from '../store/permit-transfer.store';
import {
  mockPermitTransferDetailsConfirmation,
  mockPermitTransferSubmitPayload,
  mockRequestTaskAction,
} from '../testing/mock';
import { TransferDetailsComponent } from './transfer-details.component';

describe('TransferDetailsComponent', () => {
  let component: TransferDetailsComponent;
  let fixture: ComponentFixture<TransferDetailsComponent>;
  let store: PermitTransferStore;
  let page: Page;

  const tasksService = mockClass(TasksService);

  const setState = async (value?: any) => {
    store.setState({
      ...mockPermitTransferSubmitPayload,
      ...value,
      allowedRequestTaskActions: ['PERMIT_TRANSFER_B_SAVE_APPLICATION'],
    });
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(TransferDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  class Page extends BasePage<TransferDetailsComponent> {
    get detailsAcceptedRadioButton() {
      return this.queryAll<HTMLInputElement>('input[name$="detailsAccepted"]');
    }

    get regulatedActivitiesInOperationRadioButton() {
      return this.queryAll<HTMLInputElement>('input[name$="regulatedActivitiesInOperation"]');
    }

    get transferAcceptedRadioButton() {
      return this.queryAll<HTMLInputElement>('input[name$="transferAccepted"]');
    }

    set detailsRejectedReason(value: string) {
      this.setInputValue('#detailsRejectedReason', value);
    }

    set regulatedActivitiesNotInOperationReason(value: string) {
      this.setInputValue('#regulatedActivitiesNotInOperationReason', value);
    }

    get transferRejectedReason() {
      return this.getInputValue('#transferRejectedReason');
    }

    set transferRejectedReason(value: string) {
      this.setInputValue('#transferRejectedReason', value);
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TransferDetailsComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitTransferStore,
        },
      ],
    }).compileComponents();

    store = TestBed.inject(PermitTransferStore);
    setState();
    createComponent();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form when no data exists', () => {
    expect(page.errorSummary).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryErrorList).toEqual(['Select yes or no', 'Select yes or no', 'Select yes or no']);

    page.detailsAcceptedRadioButton[1].click();
    page.regulatedActivitiesInOperationRadioButton[1].click();
    page.transferAcceptedRadioButton[1].click();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryErrorList).toEqual(['Enter a reason', 'Enter a reason', 'Enter a reason']);

    page.detailsRejectedReason = 'Details rejected reason';
    page.regulatedActivitiesNotInOperationReason = 'Regulated activities reason';
    page.transferRejectedReason = 'Transfer rejected reason';

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(mockRequestTaskAction());
  });

  it('should submit a valid form when data exists', () => {
    setState({
      permitTransferDetailsConfirmation: mockPermitTransferDetailsConfirmation,
    });
    createComponent();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(page.transferRejectedReason).toEqual(mockPermitTransferDetailsConfirmation.transferRejectedReason);

    page.transferRejectedReason = '';

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryErrorList).toEqual(['Enter a reason']);
    jest.clearAllMocks();

    page.transferRejectedReason = 'New transfer rejected reason';
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockRequestTaskAction({
        ...mockPermitTransferDetailsConfirmation,
        transferRejectedReason: 'New transfer rejected reason',
      }),
    );
  });
});
