import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { SharedUserModule } from '@shared-user/shared-user.module';
import { BasePage, mockClass } from '@testing';

import { RequestItemsService, RequestsService } from 'pmrv-api';

import { ItemLinkPipe } from '../../shared/pipes/item-link.pipe';
import { AccountsModule } from '../accounts.module';
import { TriggerHseTiComponent } from './trigger-hseti.component';

describe('TriggerHseTiComponent', () => {
  let page: Page;
  let component: TriggerHseTiComponent;
  let fixture: ComponentFixture<TriggerHseTiComponent>;

  let router: Router;
  const requestsService = mockClass(RequestsService);
  const requestItemsService = mockClass(RequestItemsService);
  const accountId = 1;

  class Page extends BasePage<TriggerHseTiComponent> {
    get allocationPeriodRadios(): NodeListOf<HTMLInputElement> {
      return this.query<HTMLDivElement>('.govuk-radios').querySelectorAll('input[type="radio"]');
    }

    get radioValues(): string[] {
      return Array.from(this.allocationPeriodRadios).map((input) => input.value);
    }

    get allocationPeriodRadios2021To2025() {
      return this.query<HTMLButtonElement>('input[type="radio"]#allocationPeriod-option0');
    }

    get errorSummary(): HTMLDivElement {
      return this.query('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedUserModule, AccountsModule],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(convertToParamMap({ accountId })),
          },
        },
        { provide: RequestsService, useValue: requestsService },
        { provide: RequestItemsService, useValue: requestItemsService },
        // { provide: Router, useValue: router },
        ItemLinkPipe,
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
  });

  describe('when it can be submitted', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(TriggerHseTiComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit hseti', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      requestsService.processRequestCreateAction.mockReturnValueOnce(of({ requestId: '1234' }));
      requestItemsService.getItemsByRequest.mockReturnValueOnce(
        of({
          items: [
            {
              taskId: 1,
              requestType: 'HSE_TI',
              taskType: 'HSE_TI_APPLICATION_SUBMIT',
              creationDate: '2022-02-02T17:44:52.944926Z',
            },
          ],
        }),
      );

      expect(page.radioValues).toEqual(['2021-2025', '2026-2030']);

      page.submitButton.click();
      fixture.detectChanges();

      expect(requestsService.processRequestCreateAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledTimes(0);

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select the allocation period']);

      page.allocationPeriodRadios2021To2025.click();
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(requestsService.processRequestCreateAction).toHaveBeenCalledTimes(1);
      expect(requestsService.processRequestCreateAction).toHaveBeenCalledWith(
        {
          requestCreateActionType: 'HSE_TI',
          requestCreateActionPayload: {
            payloadType: 'HSE_TI_REQUEST_CREATE_ACTION_PAYLOAD',
            allocationPeriod: 'PERIOD_2021_2025',
          },
        },
        String(accountId),
      );
      expect(requestItemsService.getItemsByRequest).toHaveBeenCalledTimes(1);
      expect(requestItemsService.getItemsByRequest).toHaveBeenCalledWith('1234');
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['/tasks', 1, 'hseti', 'submit']);
    });
  });
});
