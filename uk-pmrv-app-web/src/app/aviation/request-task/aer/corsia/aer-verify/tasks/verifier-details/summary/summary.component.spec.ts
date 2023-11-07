import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SummaryComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifier-details/summary/summary.component';
import { VerifierDetailsFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifier-details/verifier-details-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import {
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaVerifierDetails,
  TasksService,
} from 'pmrv-api';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let formProvider: VerifierDetailsFormProvider;

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
        { provide: TASK_FORM_PROVIDER, useClass: VerifierDetailsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<VerifierDetailsFormProvider>(TASK_FORM_PROVIDER);

    store.setState({
      requestTaskItem: {
        requestInfo: { type: 'AVIATION_AER_CORSIA' },
        requestTask: {
          id: 19,
          type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
            verificationReport: {
              verificationBodyDetails: {
                name: 'Verification body company',
                address: {
                  line1: 'Korinthou 4, Neo Psychiko',
                  line2: 'line 2 legal test',
                  city: 'Athens',
                  country: 'GR',
                  postcode: '15452',
                },
              },
              verificationBodyId: 1,
              verifierDetails: {
                verificationTeamLeader: {
                  name: 'My name',
                  role: 'My role',
                  email: 'test@pmrv.com',
                  position: 'My position',
                },
                interestConflictAvoidance: {
                  sixVerificationsConducted: false,
                  impartialityAssessmentResult: 'My results',
                },
              },
            },
            verificationSectionsCompleted: { verifierDetails: [false] },
          },
        },
      },
      isEditable: true,
    } as any);
    formProvider.setFormValue(
      (store.aerVerifyDelegate.payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
        .verificationReport.verifierDetails as AviationAerCorsiaVerifierDetails,
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
      ['Company name', 'Verification body company'],
      ['Address', `Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452`],
      ['Name', 'My name'],
      ['Position', 'My position'],
      ['Role and expertise', 'My role'],
      ['Email', 'test@pmrv.com'],
      ['Have you conducted six or more annual verifications as a team leader for this aeroplane operator?', 'No'],
      ['Describe the main results of impartiality and avoidance of conflict of interest assessment', 'My results'],
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
        verifierDetails: {
          interestConflictAvoidance: {
            breakTaken: null,
            sixVerificationsConducted: false,
            reason: null,
            impartialityAssessmentResult: 'My results',
          },
          verificationTeamLeader: {
            name: 'My name',
            role: 'My role',
            email: 'test@pmrv.com',
            position: 'My position',
          },
        },
        verificationSectionsCompleted: { verifierDetails: [true] },
        payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../../..'], { relativeTo: activatedRoute });
  });
});
