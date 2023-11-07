import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { SubmitSectionListComponent } from '@tasks/doal/shared/components/submit-section-list/submit-section-list.component';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskPayload } from 'pmrv-api';

import { TaskSharedModule } from '../../shared/task-shared-module';
import { initialState } from '../../store/common-tasks.state';
import { CommonTasksStore } from '../../store/common-tasks.store';
import { mockDoalApplicationSubmitRequestTaskItem, updateMockDoalApplicationSubmitRequestTaskItem } from '../test/mock';
import { SubmitContainerComponent } from './submit-container.component';

describe('SubmitContainerComponent', () => {
  let component: SubmitContainerComponent;
  let fixture: ComponentFixture<SubmitContainerComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<SubmitContainerComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get notifyOperatorButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[title="Notify Operator for decision"]');
    }

    get completeButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[title="Complete"]');
    }

    get appSubmitSectionList() {
      return this.query('app-submit-section-list');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubmitContainerComponent, SubmitSectionListComponent],
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(SubmitContainerComponent);
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

    it('should display HTML elements', () => {
      expect(page.heading).toEqual('2025 Determination of activity level change');
      expect(page.appSubmitSectionList).toBeTruthy();
      expect(page.notifyOperatorButton).toBeFalsy();
      expect(page.completeButton).toBeFalsy();
    });
  });

  describe('for completed doal for proceed to authority when user has chosen not to send an official notice', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem({
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
          determination: {
            type: 'PROCEED_TO_AUTHORITY',
            reason: 'reason',
            articleReasonGroupType: 'ARTICLE_6A_REASONS',
            articleReasonItems: ['ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5'],
            hasWithholdingOfAllowances: true,
            withholdingAllowancesNotice: {
              noticeIssuedDate: '2022-08-10',
              withholdingOfAllowancesComment: 'withholdingOfAllowancesComment',
            },
            needsOfficialNotice: false,
          },
        }),
      });
    });

    beforeEach(createComponent);

    it('should display appropriate buttons', () => {
      expect(page.notifyOperatorButton).toBeFalsy();
      expect(page.completeButton).toBeTruthy();
    });
  });

  describe('for completed doal for proceed to authority when user has chosen to send an official notice', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem({
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
          determination: {
            type: 'PROCEED_TO_AUTHORITY',
            reason: 'reason',
            articleReasonGroupType: 'ARTICLE_6A_REASONS',
            articleReasonItems: ['ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5'],
            hasWithholdingOfAllowances: true,
            withholdingAllowancesNotice: {
              noticeIssuedDate: '2022-08-10',
              withholdingOfAllowancesComment: 'withholdingOfAllowancesComment',
            },
            needsOfficialNotice: true,
          },
        }),
      });
    });

    beforeEach(createComponent);

    it('should display appropriate buttons', () => {
      expect(page.notifyOperatorButton).toBeTruthy();
      expect(page.completeButton).toBeFalsy();
    });
  });

  describe('for completed doal for close', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem({
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
          determination: {
            type: 'CLOSED',
            reason: 'reason',
          },
        }),
      });
    });

    beforeEach(createComponent);

    it('should display appropriate buttons', () => {
      expect(page.notifyOperatorButton).toBeFalsy();
      expect(page.completeButton).toBeTruthy();
    });
  });
});
