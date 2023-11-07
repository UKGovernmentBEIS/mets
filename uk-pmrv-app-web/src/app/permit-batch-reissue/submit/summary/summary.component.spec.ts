import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { expectBusinessErrorToBe } from '@error/testing/business-error';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RequestsService } from 'pmrv-api';

import {
  anotherInProgressError,
  noMatchingEmittersError,
} from '../../../shared/components/batch-reissue-submit/errors/business-errors';
import { initialState } from '../store/permit-batch-reissue.state';
import { PermitBatchReissueStore } from '../store/permit-batch-reissue.store';
import { mockSubmitCompletedState, regulators } from '../testing/mock-data';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  let page: Page;
  let store: PermitBatchReissueStore;
  const requestService = mockClass(RequestsService);

  const activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
    regulators,
  });

  class Page extends BasePage<SummaryComponent> {
    get values() {
      return this.queryAll<HTMLElement>(
        'dl.govuk-summary-list > div.govuk-summary-list__row >  dd.govuk-summary-list__value',
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }

    get confirmationMessage() {
      return this.query<HTMLParagraphElement>('div.govuk-panel--confirmation div.govuk-panel__body p');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: RequestsService, useValue: requestService },
      ],
    }).compileComponents();
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitBatchReissueStore);
    store.setState({
      ...initialState,
      ...mockSubmitCompletedState,
      installationCategories: ['A_LOW_EMITTER', 'B'],
    });
  });
  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display values', () => {
    expect(page.values.map((val) => val.textContent.trim())).toEqual([
      'fn3 ln3',
      'Awaiting revocation',
      'GHGE',
      'A (low emitter)B',
    ]);
  });

  it('should submit values and display confirmation message', () => {
    requestService.processRequestCreateAction.mockReturnValueOnce(of({ requestId: '1234' }));

    page.submitButton.click();
    fixture.detectChanges();

    expect(requestService.processRequestCreateAction).toHaveBeenCalledTimes(1);
    expect(requestService.processRequestCreateAction).toHaveBeenCalledWith({
      requestCreateActionType: 'PERMIT_BATCH_REISSUE',
      requestCreateActionPayload: {
        payloadType: 'PERMIT_BATCH_REISSUE_REQUEST_CREATE_ACTION_PAYLOAD',
        filters: {
          accountStatuses: mockSubmitCompletedState.accountStatuses,
          emitterTypes: mockSubmitCompletedState.emitterTypes,
          installationCategories: ['A_LOW_EMITTER', 'B'],
        },
        signatory: mockSubmitCompletedState.signatory,
      },
    });

    expect(page.confirmationMessage.textContent.trim()).toEqual('Your reference code is: 1234');
  });

  it('should display error message when another request is in progess', async () => {
    requestService.processRequestCreateAction.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'BATCHREISSUE0001' }, status: 400 })),
    );

    page.submitButton.click();
    fixture.detectChanges();

    expect(requestService.processRequestCreateAction).toHaveBeenCalledTimes(1);
    expect(requestService.processRequestCreateAction).toHaveBeenCalledWith({
      requestCreateActionType: 'PERMIT_BATCH_REISSUE',
      requestCreateActionPayload: {
        payloadType: 'PERMIT_BATCH_REISSUE_REQUEST_CREATE_ACTION_PAYLOAD',
        filters: {
          accountStatuses: mockSubmitCompletedState.accountStatuses,
          emitterTypes: mockSubmitCompletedState.emitterTypes,
          installationCategories: ['A_LOW_EMITTER', 'B'],
        },
        signatory: mockSubmitCompletedState.signatory,
      },
    });

    await expectBusinessErrorToBe(anotherInProgressError(false));
  });

  it('should display error message when 0 emitters found', async () => {
    requestService.processRequestCreateAction.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'BATCHREISSUE0002' }, status: 400 })),
    );

    page.submitButton.click();
    fixture.detectChanges();

    expect(requestService.processRequestCreateAction).toHaveBeenCalledTimes(1);
    expect(requestService.processRequestCreateAction).toHaveBeenCalledWith({
      requestCreateActionType: 'PERMIT_BATCH_REISSUE',
      requestCreateActionPayload: {
        payloadType: 'PERMIT_BATCH_REISSUE_REQUEST_CREATE_ACTION_PAYLOAD',
        filters: {
          accountStatuses: mockSubmitCompletedState.accountStatuses,
          emitterTypes: mockSubmitCompletedState.emitterTypes,
          installationCategories: ['A_LOW_EMITTER', 'B'],
        },
        signatory: mockSubmitCompletedState.signatory,
      },
    });

    await expectBusinessErrorToBe(noMatchingEmittersError(false));
  });
});
