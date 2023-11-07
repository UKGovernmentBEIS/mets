import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { GeneralInformationFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/general-information/general-information-form.provider';
import { SummaryComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/general-information/summary/summary.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import {
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaGeneralInformation,
  TasksService,
} from 'pmrv-api';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let formProvider: GeneralInformationFormProvider;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SummaryComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: GeneralInformationFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<GeneralInformationFormProvider>(TASK_FORM_PROVIDER);

    store.setState({
      requestTaskItem: {
        requestInfo: { type: 'AVIATION_AER_CORSIA' },
        requestTask: {
          id: 19,
          type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
            verificationReport: {
              generalInformation: {
                verificationCriteria: 'My verification criteria',
                operatorData: 'My operator data',
              },
            },
            verificationSectionsCompleted: { generalInformation: [false] },
          },
        },
      },
      isEditable: true,
    } as any);
    formProvider.setFormValue(
      (store.aerVerifyDelegate.payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
        .verificationReport.generalInformation as AviationAerCorsiaGeneralInformation,
    );

    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.summaryValues).toEqual([
      ['Verification criteria', 'My verification criteria'],
      ['Information and data used by the operator', 'My operator data'],
    ]);
  });

  it('should submit a valid form and navigate to task list page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 19,
      requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
      requestTaskActionPayload: {
        generalInformation: {
          verificationCriteria: 'My verification criteria',
          operatorData: 'My operator data',
        },
        verificationSectionsCompleted: { generalInformation: [true] },
        payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../../..'], { relativeTo: activatedRoute });
  });
});
