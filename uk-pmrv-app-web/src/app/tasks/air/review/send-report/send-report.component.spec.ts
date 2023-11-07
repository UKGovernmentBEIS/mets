import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AirTaskReviewModule } from '@tasks/air/review/air-task-review.module';
import { mockStateReview } from '@tasks/air/review/testing/mock-air-application-review-payload';
import { mockStateBuild } from '@tasks/air/review/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { SendReportComponent } from './send-report.component';

describe('SendReportComponent', () => {
  let page: Page;
  let component: SendReportComponent;
  let store: CommonTasksStore;
  let fixture: ComponentFixture<SendReportComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SendReportComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get notifyOperator(): HTMLElement {
      return this.query<HTMLElement>('app-notify-operator');
    }

    get paragraph(): HTMLParagraphElement {
      return this.query<HTMLParagraphElement>('p.govuk-body');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirTaskReviewModule, RouterTestingModule],
      providers: [KeycloakService, DestroySubject, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('when it can be submitted', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateReview);
      fixture = TestBed.createComponent(SendReportComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.notifyOperator).toBeTruthy();
    });
  });

  describe('when it cannot be submitted', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          reviewSectionsCompleted: {
            A1: false,
          },
        }),
      );
      fixture = TestBed.createComponent(SendReportComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Submit responses');
      expect(page.paragraph.textContent.trim()).toEqual('You need to complete all tasks before submitting the report.');
    });
  });
});
