import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { BasePage, mockClass } from '@testing';

import { RequestItemsService, RequestsService } from 'pmrv-api';

import { ItemLinkPipe } from '../../shared/pipes/item-link.pipe';
import { SharedModule } from '../../shared/shared.module';
import { TriggerDoalComponent } from './trigger-doal.component';

describe('TriggerDoalComponent', () => {
  let page: Page;
  let component: TriggerDoalComponent;
  let fixture: ComponentFixture<TriggerDoalComponent>;

  let router: Router;
  const requestService = mockClass(RequestsService);
  const requestItemsService = mockClass(RequestItemsService);
  const accountId = 1;

  class Page extends BasePage<TriggerDoalComponent> {
    get yearSelect(): HTMLSelectElement {
      return this.query('select');
    }

    get options(): string[] {
      return Array.from(this.yearSelect.options).map((option) => option.textContent.trim());
    }

    get yearValue(): string {
      return this.yearSelect.value;
    }
    set yearValue(value: string) {
      this.setInputValue('select', value);
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
      declarations: [TriggerDoalComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(convertToParamMap({ accountId })),
          },
        },
        { provide: RequestsService, useValue: requestService },
        { provide: RequestItemsService, useValue: requestItemsService },
        ItemLinkPipe,
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
  });

  describe('when it can be submitted', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(TriggerDoalComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit doal', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      requestService.processRequestCreateAction.mockReturnValueOnce(of({ requestId: '1234' }));
      requestItemsService.getItemsByRequest.mockReturnValueOnce(
        of({
          items: [
            {
              taskId: 1,
              requestType: 'DOAL',
              taskType: 'DOAL_APPLICATION_SUBMIT',
              creationDate: '2022-02-02T17:44:52.944926Z',
            },
          ],
        }),
      );

      expect(page.options).toEqual([
        '2020',
        '2021',
        '2022',
        '2023',
        '2024',
        '2025',
        '2026',
        '2027',
        '2028',
        '2029',
        '2030',
      ]);

      page.submitButton.click();
      fixture.detectChanges();

      expect(requestService.processRequestCreateAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledTimes(0);

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select an option']);

      page.yearValue = '2025';
      page.submitButton.click();
      fixture.detectChanges();

      expect(requestService.processRequestCreateAction).toHaveBeenCalledTimes(1);
      expect(requestService.processRequestCreateAction).toHaveBeenCalledWith(
        {
          requestCreateActionType: 'DOAL',
          requestCreateActionPayload: {
            payloadType: 'DOAL_REQUEST_CREATE_ACTION_PAYLOAD',
            year: 2025,
          },
        },
        accountId,
      );
      expect(requestItemsService.getItemsByRequest).toHaveBeenCalledTimes(1);
      expect(requestItemsService.getItemsByRequest).toHaveBeenCalledWith('1234');
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['/tasks', 1, 'doal', 'submit']);
    });
  });
});
