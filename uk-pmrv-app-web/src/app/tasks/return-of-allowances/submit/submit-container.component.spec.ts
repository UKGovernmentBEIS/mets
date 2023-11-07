import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';
import moment from 'moment';

import { RequestTaskPayload, ReturnOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { TaskSharedModule } from '../../shared/task-shared-module';
import { initialState } from '../../store/common-tasks.state';
import { CommonTasksStore } from '../../store/common-tasks.store';
import {
  mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem,
  updateMockedReturnOfAllowances,
} from '../test/mock';
import { SubmitContainerComponent } from './submit-container.component';

describe('SubmitContainerComponent', () => {
  let component: SubmitContainerComponent;
  let fixture: ComponentFixture<SubmitContainerComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<SubmitContainerComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item'));
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubmitContainerComponent],
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

  describe('for new return of allowances', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem,
          requestTask: {
            ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem.requestTask,
            payload: {
              sectionsCompleted: {
                PROVIDE_DETAILS: false,
              },
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
      expect(page.heading).toEqual('Return of allowances');
    });

    it('should display return of allowances section as not started', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Provide details for return of allowances');
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
        requestTaskItem: updateMockedReturnOfAllowances({}, false),
      });
    });

    beforeEach(createComponent);

    it('should display section as in progress', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Provide details for return of allowances');
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
        requestTaskItem: mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem,
      });
    });

    beforeEach(createComponent);

    it('should display section as completed', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Provide details for return of allowances');
      expect(page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim()).toEqual(
        'completed',
      );
    });
  });

  describe('for completed payload with invalid dueDate', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem,
          requestTask: {
            ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem.requestTask,
            payload: {
              ...mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem.requestTask.payload,
              returnOfAllowances: {
                ...(
                  mockCompletedReturnOfAllowancesApplicationSubmitRequestTaskItem.requestTask
                    .payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload
                ).returnOfAllowances,
                dateToBeReturned: moment().add(-1, 'day').format('YYYY-MM-DD'),
              },
            } as ReturnOfAllowancesApplicationSubmitRequestTaskPayload,
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should display section as needs review', () => {
      expect(page.sections.length).toEqual(1);
      expect(page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim()).toEqual(
        'needs review',
      );
    });
  });
});
