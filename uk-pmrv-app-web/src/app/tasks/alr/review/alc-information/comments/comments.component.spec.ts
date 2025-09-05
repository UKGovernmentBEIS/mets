import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { alrMockReviewApplyPayload, alrMockReviewState, mockAlrReviewPostBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AlrCommentsComponent } from './comments.component';

describe('AlrCommentsComponent', () => {
  let component: AlrCommentsComponent;
  let fixture: ComponentFixture<AlrCommentsComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({
    taskId: 1,
  });

  class Page extends BasePage<AlrCommentsComponent> {
    get commentsForUkEtsAuthorityTextArea() {
      return this.query<HTMLInputElement>('textarea[name$="ukEtsAuthorityComments"]');
    }

    get commentsForUkEtsAuthority(): string {
      return this.getInputValue('#ukEtsAuthorityComments');
    }
    set commentsForUkEtsAuthority(value: string) {
      this.setInputValue('#ukEtsAuthorityComments', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AlrCommentsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AlrTaskSharedModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new comment', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(alrMockReviewState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.commentsForUkEtsAuthority = 'A comment';

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockAlrReviewPostBuild(
          {
            regulatorReviewOutcome: {
              ...alrMockReviewApplyPayload.regulatorReviewOutcome,
              ukEtsAuthorityComments: 'A comment',
            },
          },
          {
            ALC: false,
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'summary'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
