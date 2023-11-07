import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '@tasks/doal/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskPayload } from 'pmrv-api';

import { SubmitSectionListComponent } from './submit-section-list.component';

describe('SubmitSectionListComponent', () => {
  let component: SubmitSectionListComponent;
  let fixture: ComponentFixture<SubmitSectionListComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<SubmitSectionListComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item'));
    }

    get sectionStatuses(): HTMLElement[] {
      return this.sections.map((section) => section.querySelector<HTMLElement>('.app-task-list__tag'));
    }

    get sectionAnchors(): HTMLAnchorElement[] {
      return this.sections.map((section) => section.querySelector<HTMLAnchorElement>('a'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubmitSectionListComponent],
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(SubmitSectionListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  describe('for new doal', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockDoalApplicationSubmitRequestTaskItem,
          requestTask: {
            ...mockDoalApplicationSubmitRequestTaskItem.requestTask,
            payload: {
              ...mockDoalApplicationSubmitRequestTaskItem.requestTask.payload,
              doal: null,
              doalSectionsCompleted: {},
              historicalPreliminaryAllocations: undefined,
            } as RequestTaskPayload,
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display sections as not started', () => {
      expect(page.sections.length).toEqual(5);
      expect(page.sectionStatuses.map((st) => st.textContent.trim())).toEqual([
        'not started',
        'not started',
        'not started',
        'not started',
        'cannot start yet',
      ]);
    });
  });

  describe('for completed doal', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: mockDoalApplicationSubmitRequestTaskItem,
      });
    });

    beforeEach(createComponent);

    it('should display sections as completed', () => {
      expect(page.sectionStatuses.map((st) => st.textContent.trim())).toEqual([
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
      ]);
    });
  });

  describe('for in progress doal with already set determination', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {},
          {
            operatorActivityLevelReport: false,
            verificationReportOfTheActivityLevelReport: false,
            additionalDocuments: false,
            activityLevelChangeInformation: false,
            determination: false,
          },
        ),
      });
    });

    beforeEach(createComponent);

    it('should display all sections as in progress', () => {
      expect(page.sectionStatuses.map((st) => st.textContent.trim())).toEqual([
        'in progress',
        'in progress',
        'in progress',
        'in progress',
        'in progress',
      ]);
    });

    it('should display anchors for all', () => {
      expect(page.sectionAnchors.map((st) => st?.href?.trim())).toEqual([
        'http://localhost/operator-report/summary',
        'http://localhost/verification-report/summary',
        'http://localhost/additional-documents/summary',
        'http://localhost/alc-information/summary',
        'http://localhost/determination/proceed-authority/summary',
      ]);
    });
  });

  describe('for in progress doal with no determination set yet', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
            determination: undefined,
          },
          {
            operatorActivityLevelReport: false,
            verificationReportOfTheActivityLevelReport: false,
            additionalDocuments: false,
            activityLevelChangeInformation: false,
          },
        ),
      });
    });

    beforeEach(createComponent);

    it('should display sections as in progress and determination as cannot start yet', () => {
      expect(page.sectionStatuses.map((st) => st.textContent.trim())).toEqual([
        'in progress',
        'in progress',
        'in progress',
        'in progress',
        'cannot start yet',
      ]);
    });

    it('should display anchors for all except the determination', () => {
      expect(page.sectionAnchors.map((st) => st?.href?.trim())).toEqual([
        'http://localhost/operator-report/summary',
        'http://localhost/verification-report/summary',
        'http://localhost/additional-documents/summary',
        'http://localhost/alc-information/summary',
        undefined,
      ]);
    });
  });

  describe('for completed doal with no determination set yet', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
            determination: undefined,
          },
          {
            operatorActivityLevelReport: true,
            verificationReportOfTheActivityLevelReport: true,
            additionalDocuments: true,
            activityLevelChangeInformation: true,
          },
        ),
      });
    });

    beforeEach(createComponent);

    it('should display sections as completed and determination as not started', () => {
      expect(page.sectionStatuses.map((st) => st.textContent.trim())).toEqual([
        'completed',
        'completed',
        'completed',
        'completed',
        'not started',
      ]);
    });

    it('should display anchors for all and the determination to first wizard determination step', () => {
      expect(page.sectionAnchors.map((st) => st?.href?.trim())).toEqual([
        'http://localhost/operator-report/summary',
        'http://localhost/verification-report/summary',
        'http://localhost/additional-documents/summary',
        'http://localhost/alc-information/summary',
        'http://localhost/determination',
      ]);
    });
  });

  describe('for in progress doal with no determination set yet with user not having allowed actions', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: false,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
            operatorActivityLevelReport: undefined,
            determination: undefined,
          },
          {
            verificationReportOfTheActivityLevelReport: false,
            additionalDocuments: false,
            activityLevelChangeInformation: false,
          },
        ),
      });
    });

    beforeEach(createComponent);

    it('should display anchors for all except the not started and cannot start yet', () => {
      expect(page.sectionAnchors.map((st) => st?.href?.trim())).toEqual([
        undefined,
        'http://localhost/verification-report/summary',
        'http://localhost/additional-documents/summary',
        'http://localhost/alc-information/summary',
        undefined,
      ]);
    });
  });
});
