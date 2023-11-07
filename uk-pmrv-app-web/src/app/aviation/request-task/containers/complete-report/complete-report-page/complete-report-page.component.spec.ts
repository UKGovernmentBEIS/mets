import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { mockState } from '@aviation/request-task/aer/ukets/tasks/send-report/testing/mock-state';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import CompleteReportPageComponent from './complete-report-page.component';

describe('CompleteReportPageComponent', () => {
  let component: CompleteReportPageComponent;
  let fixture: ComponentFixture<CompleteReportPageComponent>;
  let page: Page;
  let store: RequestTaskStore;
  let router: Router;
  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<CompleteReportPageComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }
    get paragraphsContent() {
      return this.queryAll('p').map((p) => p.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          type: 'AVIATION_AER_UKETS_APPLICATION_REVIEW',
        },
      },
    });

    fixture = TestBed.createComponent(CompleteReportPageComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual('Complete emissions report');
    expect(page.paragraphsContent[0]).toEqual(
      'By selecting ‘Confirm and complete’ you confirm that the information in your report is correct to the best of your knowledge.',
    );
  });

  it('should submit', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AVIATION_AER_UKETS_COMPLETE_REVIEW',
      requestTaskId: mockState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });

    page.submitButton.click();
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalledWith(['complete-report-confirmation'], { relativeTo: activatedRouteStub });
  });
});
