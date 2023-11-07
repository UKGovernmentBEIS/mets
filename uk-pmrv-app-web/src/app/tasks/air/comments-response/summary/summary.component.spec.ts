import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AirTaskCommentsResponseModule } from '@tasks/air/comments-response/air-task-comments-response.module';
import {
  mockAirApplicationRespondPayload,
  mockStateRespond,
} from '@tasks/air/comments-response/testing/mock-air-application-respond-payload';
import { mockPostBuild } from '@tasks/air/comments-response/testing/mock-state';
import { AirTaskSharedModule } from '@tasks/air/shared/air-task-shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AirImprovement, TasksService } from 'pmrv-api';

import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  const reference = '1';
  const currentItem: AirImprovement = mockAirApplicationRespondPayload.airImprovements[reference];
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockStateRespond.requestTaskItem.requestTask.id, id: reference },
    null,
    {
      airImprovement: currentItem,
    },
  );
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get airImprovementItem() {
      return this.query('app-air-improvement-item');
    }

    get airOperatorResponseItem() {
      return this.query('app-air-operator-response-item');
    }

    get airRegulatorResponseItem() {
      return this.query('app-air-regulator-response-item');
    }

    get airOperatorFollowupItem() {
      return this.query('app-air-operator-followup-item');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirTaskCommentsResponseModule, AirTaskSharedModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockStateRespond);
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

  it('should display all HTMLElements', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Check your answers');
    expect(page.airImprovementItem).toBeTruthy();
    expect(page.airOperatorResponseItem).toBeTruthy();
    expect(page.airRegulatorResponseItem).toBeTruthy();
    expect(page.airOperatorFollowupItem).toBeTruthy();
    expect(page.submitButton).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          reference: Number(reference),
          operatorImprovementFollowUpResponse:
            mockAirApplicationRespondPayload?.operatorImprovementFollowUpResponses?.[reference],
        },
        { [reference]: true },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
