import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { BasePage, mockClass } from '@testing';

import { RequestsService } from 'pmrv-api';

import { AccountsModule } from '../accounts.module';
import { TriggerAirComponent } from './trigger-air.component';

describe('TriggerAirComponent', () => {
  let page: Page;
  let component: TriggerAirComponent;
  let fixture: ComponentFixture<TriggerAirComponent>;

  const requestService = mockClass(RequestsService);
  const expectedAccountId = 1;

  class Page extends BasePage<TriggerAirComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get heading3(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h3');
    }

    get actionLegend(): HTMLSpanElement {
      return this.query<HTMLSpanElement>('span.govuk-caption-l');
    }

    get paragraph(): HTMLParagraphElement {
      return this.query<HTMLParagraphElement>('p.govuk-body');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }

    get panelBody() {
      return this.query<HTMLElement>('div.govuk-panel__body');
    }
  }

  const initComponentState = () => {
    component.isAirInitialized$ = new BehaviorSubject<boolean>(false);
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountsModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(convertToParamMap({ accountId: expectedAccountId })),
          },
        },
        { provide: RequestsService, useValue: requestService },
      ],
    }).compileComponents();
  });

  describe('when it can be submitted', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(TriggerAirComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Trigger Annual improvement report');
      expect(page.paragraph.textContent.trim()).toEqual(
        'Are you sure you want to trigger an Annual improvement report for this installation?',
      );
      expect(page.submitButton.textContent.trim()).toEqual('Confirm');
    });

    it('should submit request and set accordingly properties', () => {
      requestService.processRequestCreateAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(requestService.processRequestCreateAction).toHaveBeenCalledWith(
        {
          requestCreateActionType: 'AIR',
          requestCreateActionPayload: {
            payloadType: 'EMPTY_PAYLOAD',
          },
        },
        expectedAccountId,
      );
    });
  });

  describe('when it has been submitted', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(TriggerAirComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.isAirInitialized$ = new BehaviorSubject(true);
      component.requestId$ = new BehaviorSubject('AIR00001-2023');
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Annual improvement report triggered');
      expect(page.panelBody.textContent.trim()).toEqual('Your reference code is: AIR00001-2023');
      expect(page.heading3.textContent.trim()).toEqual('What happens next');
      expect(page.paragraph.textContent.trim()).toEqual(
        'The operator must now complete their Annual improvement report.',
      );
      expect(page.submitButton).toBeFalsy();
    });
  });
});
