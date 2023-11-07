import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockPostBuild } from '@tasks/vir/review/testing/mock-state';
import {
  mockStateReview,
  mockVirApplicationReviewPayload,
} from '@tasks/vir/review/testing/mock-vir-application-review-payload';
import { VirTaskReviewModule } from '@tasks/vir/review/vir-task-review.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService, UncorrectedItem } from 'pmrv-api';

import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  const currentItem: UncorrectedItem = mockVirApplicationReviewPayload.verificationData.uncorrectedNonConformities.B1;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockStateReview.requestTaskItem.requestTask.id, id: currentItem.reference },
    null,
    {
      verificationDataItem: currentItem,
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

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirTaskReviewModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockStateReview);
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
          regulatorReviewResponse: mockVirApplicationReviewPayload.regulatorReviewResponse,
        },
        { B1: true },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
