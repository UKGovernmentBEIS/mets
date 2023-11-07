import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';

import { RequestsService } from 'pmrv-api';

import { AerReinitializeComponent } from './aer-reinitialize.component';

describe('AerReinitializeComponent', () => {
  let page: Page;
  let component: AerReinitializeComponent;
  let fixture: ComponentFixture<AerReinitializeComponent>;

  const requestService = mockClass(RequestsService);

  class Page extends BasePage<AerReinitializeComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
    get confirmationMessage() {
      return this.query('h1.govuk-panel__title');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [AerReinitializeComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(convertToParamMap({ 'request-id': 'AEM00001-2022', accountId: '13' })),
          },
        },
        { provide: RequestsService, useValue: requestService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AerReinitializeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit AER action and show confirmation message', () => {
    requestService.processRequestCreateAction.mockReturnValueOnce(of({ requestId: '1234' }));

    expect(page.confirmationMessage).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(requestService.processRequestCreateAction).toHaveBeenCalledTimes(1);
    expect(requestService.processRequestCreateAction).toHaveBeenCalledWith(
      {
        requestCreateActionType: 'AER',
        requestCreateActionPayload: {
          payloadType: 'REPORT_RELATED_REQUEST_CREATE_ACTION_PAYLOAD',
          requestId: 'AEM00001-2022',
        },
      },
      13,
    );

    expect(page.confirmationMessage).toBeTruthy();
    expect(page.confirmationMessage.textContent.trim()).toEqual('The report has been returned to the operator');
  });
});
