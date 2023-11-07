import { ComponentFixture, inject, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore, UserState } from '@core/store/auth';
import { ProcessActionsComponent } from '@shared/accounts';
import { ItemLinkPipe } from '@shared/pipes/item-link.pipe';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { ItemDTOResponse, RequestCreateActionProcessResponseDTO, RequestItemsService, RequestsService } from 'pmrv-api';

import { mockedAccount } from '../../../../accounts/testing/mock-data';

let component: ProcessActionsComponent;
let fixture: ComponentFixture<ProcessActionsComponent>;
let authStore: AuthStore;
let authService: Partial<jest.Mocked<AuthService>>;
let activatedRouteStub: ActivatedRouteStub;
let requestService: Partial<jest.Mocked<RequestsService>>;
let requestItemsService: Partial<jest.Mocked<RequestItemsService>>;
let availableItemsResponse: ItemDTOResponse;
let page: Page;

const processRequestCreateActionResponse: RequestCreateActionProcessResponseDTO = { requestId: '1234' };
const taskId = 1;
const mockAvailableTasksOperator = {
  PERMIT_SURRENDER: { valid: false, requests: ['PERMIT_SURRENDER'] },
  PERMIT_NOTIFICATION: { valid: true },
};
const mockAvailableTasksRegulator = {
  PERMIT_REVOCATION: { valid: true },
  AIR: { valid: false, improvementsExist: false },
};

class Page extends BasePage<ProcessActionsComponent> {
  get buttons() {
    return this.queryAll<HTMLButtonElement>('button');
  }

  get buttonContents(): string[] {
    return this.buttons.map((item) => item.textContent.trim());
  }

  get errorListContents(): string[] {
    return this.queryAll<HTMLLIElement>('.govuk-grid-column-full li').map((item) => item.textContent.trim());
  }
}

const createRequestPayload = (requestType) => ({
  requestCreateActionType: requestType,
  requestCreateActionPayload: {
    payloadType: 'EMPTY_PAYLOAD',
  },
});
const createComponent = () => {
  fixture = TestBed.createComponent(ProcessActionsComponent);
  component = fixture.componentInstance;
  page = new Page(fixture);
  fixture.detectChanges();
};
const createModule = async () => {
  await TestBed.configureTestingModule({
    declarations: [ProcessActionsComponent],
    imports: [RouterTestingModule, SharedModule],
    providers: [
      { provide: ActivatedRoute, useValue: activatedRouteStub },
      { provide: RequestsService, useValue: requestService },
      { provide: RequestItemsService, useValue: requestItemsService },
      { provide: AuthService, useValue: authService },
      ItemLinkPipe,
    ],
  }).compileComponents();

  authStore = TestBed.inject(AuthStore);
};

describe('ProcessActionsComponent', () => {
  beforeEach(async () => {
    activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
      account: mockedAccount,
    });
    requestItemsService = {
      getItemsByRequest: jest.fn().mockReturnValue(of(availableItemsResponse)),
    };
  });

  describe('for operator', () => {
    beforeEach(async () => {
      authService = {
        loadUserState: jest.fn(),
      };
      requestService = {
        getAvailableAccountWorkflows: jest.fn().mockReturnValue(of(mockAvailableTasksOperator)),
        processRequestCreateAction: jest.fn().mockReturnValue(of(processRequestCreateActionResponse)),
      };
      await createModule();
      setUser('OPERATOR');
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should retrieve available workflows and display them as expected', () => {
      expect(page.buttonContents).toEqual(['Start a notification']);
      expect(page.errorListContents).toEqual([
        'You cannot start a permit surrender as there is already one in progress.',
      ]);
    });

    it('should processRequestCreateAction, navigate to the task item page, when a single Task Item is received', inject(
      [Router],
      (router: Router) => {
        const expectedRequestType = 'PERMIT_NOTIFICATION';
        const getItemsResponse: ItemDTOResponse = { items: [{ requestType: expectedRequestType, taskId }] };
        const onRequestButtonClickSpy = jest.spyOn(component, 'onRequestButtonClick');
        const navigateSpy = jest.spyOn(router, 'navigate');
        requestItemsService.getItemsByRequest.mockReturnValueOnce(of(getItemsResponse));

        page.buttons[0].click();

        expect(onRequestButtonClickSpy).toHaveBeenCalledTimes(1);
        expect(onRequestButtonClickSpy).toHaveBeenCalledWith(expectedRequestType);

        expect(requestService.processRequestCreateAction).toHaveBeenCalledTimes(1);
        expect(requestService.processRequestCreateAction).toHaveBeenCalledWith(
          createRequestPayload(expectedRequestType),
          0,
        );

        expect(requestItemsService.getItemsByRequest).toHaveBeenCalledTimes(1);
        expect(requestItemsService.getItemsByRequest).toHaveBeenCalledWith(
          processRequestCreateActionResponse.requestId,
        );

        expect(navigateSpy).toHaveBeenCalledTimes(1);
        expect(navigateSpy).toHaveBeenCalledWith(['.']);
      },
    ));

    it('should processRequestCreateAction, navigate to dashboard, when multiple or 0 task Items are received', inject(
      [Router],
      (router: Router) => {
        const navigateSpy = jest.spyOn(router, 'navigate');

        requestItemsService.getItemsByRequest.mockReturnValueOnce(of({ items: [] }));
        page.buttons[0].click();
        expect(navigateSpy).toHaveBeenCalledTimes(1);
        expect(navigateSpy).toHaveBeenLastCalledWith(['/dashboard']);

        requestItemsService.getItemsByRequest.mockReturnValueOnce(
          of({
            items: [
              { requestType: 'AIR', taskId },
              { requestType: 'PERMIT_ISSUANCE', taskId: taskId + 1 },
            ],
          }),
        );
        page.buttons[0].click();
        expect(navigateSpy).toHaveBeenCalledTimes(2);
        expect(navigateSpy).toHaveBeenLastCalledWith(['/dashboard']);
      },
    ));
  });

  describe('for regulator', () => {
    beforeEach(async () => {
      authService = {
        loadUserState: jest.fn(),
      };
      requestService = {
        getAvailableAccountWorkflows: jest.fn().mockReturnValue(of(mockAvailableTasksRegulator)),
        processRequestCreateAction: jest.fn().mockReturnValue(of(processRequestCreateActionResponse)),
      };
      await createModule();
      setUser('REGULATOR');
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should retrieve available workflows and display them as expected', () => {
      expect(page.buttonContents).toEqual(['Start a permit revocation']);
      expect(page.errorListContents).toEqual([
        'You cannot trigger an annual improvement report as the installation does not have any improvements to make.',
      ]);
    });

    it('should processRequestCreateAction, navigate to the task item page, when a single Task Item is received', inject(
      [Router],
      (router: Router) => {
        const expectedRequestType = 'PERMIT_REVOCATION';
        const getItemsResponse: ItemDTOResponse = { items: [{ requestType: expectedRequestType, taskId }] };
        const onRequestButtonClickSpy = jest.spyOn(component, 'onRequestButtonClick');
        const navigateSpy = jest.spyOn(router, 'navigate');
        requestItemsService.getItemsByRequest.mockReturnValueOnce(of(getItemsResponse));

        page.buttons[0].click();

        expect(onRequestButtonClickSpy).toHaveBeenCalledTimes(1);
        expect(onRequestButtonClickSpy).toHaveBeenCalledWith(expectedRequestType);

        expect(requestService.processRequestCreateAction).toHaveBeenCalledTimes(1);
        expect(requestService.processRequestCreateAction).toHaveBeenCalledWith(
          createRequestPayload(expectedRequestType),
          0,
        );

        expect(requestItemsService.getItemsByRequest).toHaveBeenCalledTimes(1);
        expect(requestItemsService.getItemsByRequest).toHaveBeenCalledWith(
          processRequestCreateActionResponse.requestId,
        );

        expect(navigateSpy).toHaveBeenCalledTimes(1);
        expect(navigateSpy).toHaveBeenLastCalledWith(['.']);
      },
    ));

    it('should processRequestCreateAction, navigate to dashboard, when multiple or 0 task Items are received', inject(
      [Router],
      (router: Router) => {
        const navigateSpy = jest.spyOn(router, 'navigate');

        requestItemsService.getItemsByRequest.mockReturnValueOnce(of({ items: [] }));
        page.buttons[0].click();
        expect(navigateSpy).toHaveBeenCalledTimes(1);
        expect(navigateSpy).toHaveBeenLastCalledWith(['/dashboard']);

        requestItemsService.getItemsByRequest.mockReturnValueOnce(
          of({
            items: [
              { requestType: 'AIR', taskId },
              { requestType: 'PERMIT_ISSUANCE', taskId: taskId + 1 },
            ],
          }),
        );
        page.buttons[0].click();
        expect(navigateSpy).toHaveBeenCalledTimes(2);
        expect(navigateSpy).toHaveBeenLastCalledWith(['/dashboard']);
      },
    ));
  });

  function setUser(roleType: UserState['roleType']) {
    authStore.setUserState({
      ...authStore.getState().userState,
      roleType,
      userId: 'opTestId',
      domainsLoginStatuses: { INSTALLATION: 'ENABLED', AVIATION: 'ENABLED' },
    });
  }
});
