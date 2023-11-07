import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockPostBuild } from '@tasks/vir/comments-response/testing/mock-state';
import {
  mockStateRespond,
  mockVirApplicationRespondPayload,
} from '@tasks/vir/comments-response/testing/mock-vir-application-respond-payload';
import { VirTaskCommentsResponseModule } from '@tasks/vir/comments-response/vir-task-comments-response.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  const currentItem = 'B1';
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockStateRespond.requestTaskItem.requestTask.id, id: currentItem },
    null,
    {
      reference: currentItem,
    },
  );
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get verificationItem() {
      return this.query('app-verification-recommendation-item');
    }

    get operatorResponseItem() {
      return this.query('app-operator-response-item');
    }

    get regulatorResponseItem() {
      return this.query('app-regulator-response-item');
    }

    get operatorFollowupItem() {
      return this.query('app-operator-followup-item');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirTaskCommentsResponseModule, RouterTestingModule],
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
    expect(page.verificationItem).toBeTruthy();
    expect(page.operatorResponseItem).toBeTruthy();
    expect(page.regulatorResponseItem).toBeTruthy();
    expect(page.operatorFollowupItem).toBeTruthy();
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
          reference: currentItem,
          operatorImprovementFollowUpResponse:
            mockVirApplicationRespondPayload?.operatorImprovementFollowUpResponses?.[currentItem],
        },
        { B1: true },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
