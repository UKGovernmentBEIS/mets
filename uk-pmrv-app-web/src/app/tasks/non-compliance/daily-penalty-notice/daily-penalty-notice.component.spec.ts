import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskPayload } from 'pmrv-api';

import { NonComplianceModule } from '../non-compliance.module';
import { mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem } from '../test/mock';
import { DailyPenaltyNoticeComponent } from './daily-penalty-notice.component';

describe('DailyPenaltyNoticeComponent', () => {
  let component: DailyPenaltyNoticeComponent;
  let fixture: ComponentFixture<DailyPenaltyNoticeComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<DailyPenaltyNoticeComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item'));
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DailyPenaltyNoticeComponent],
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  const route = new ActivatedRouteStub(null, null, {
    pageTitle: 'Upload initial penalty notice: non-compliance',
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, NonComplianceModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(DailyPenaltyNoticeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }
  describe('for new daily penalty notice', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          requestTask: {
            ...mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem.requestTask,
            payload: {
              dailyPenaltyCompleted: false,
            } as RequestTaskPayload,
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display header', () => {
      expect(page.heading).toEqual('Upload initial penalty notice: non-compliance');
    });

    it('should display Upload daily penalty notice section as not started', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Upload daily penalty notice');
      expect(page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim()).toEqual(
        'not started',
      );
    });
  });

  describe('for in progress payload', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem,
          requestTask: {
            ...mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem.requestTask,
            payload: {
              ...mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem.requestTask.payload,
              dailyPenaltyCompleted: false,
            } as RequestTaskPayload,
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should display Upload daily penalty notice section as in progress', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Upload daily penalty notice');
      expect(page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim()).toEqual(
        'in progress',
      );
    });
  });

  describe('for completed payload', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem,
      });
    });

    beforeEach(createComponent);

    it('should display Upload daily penalty notice section as completed', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Upload daily penalty notice');
      expect(page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim()).toEqual(
        'completed',
      );
    });
  });
});
