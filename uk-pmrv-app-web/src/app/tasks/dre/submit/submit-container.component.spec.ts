import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { format, subDays } from 'date-fns';
import { KeycloakService } from 'keycloak-angular';

import { DreApplicationSubmitRequestTaskPayload, RequestTaskPayload } from 'pmrv-api';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { TaskSharedModule } from '../../shared/task-shared-module';
import { initialState } from '../../store/common-tasks.state';
import { CommonTasksStore } from '../../store/common-tasks.store';
import { mockCompletedDreApplicationSubmitRequestTaskItem, updateMockedDre } from '../test/mock';
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

  describe('for new dre', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedDreApplicationSubmitRequestTaskItem,
          requestTask: {
            ...mockCompletedDreApplicationSubmitRequestTaskItem.requestTask,
            payload: {
              sectionCompleted: false,
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
      expect(page.heading).toEqual('Determine 2022 reportable emissions');
    });

    it('should display reportable emissions section as not started', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Reportable emissions');
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
        requestTaskItem: updateMockedDre({}, false),
      });
    });

    beforeEach(createComponent);

    it('should display reportable emissions section as in progress', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Reportable emissions');
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
        requestTaskItem: mockCompletedDreApplicationSubmitRequestTaskItem,
      });
    });

    beforeEach(createComponent);

    it('should display reportable emissions section as completed', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Reportable emissions');
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
          ...mockCompletedDreApplicationSubmitRequestTaskItem,
          requestTask: {
            ...mockCompletedDreApplicationSubmitRequestTaskItem.requestTask,
            payload: {
              ...mockCompletedDreApplicationSubmitRequestTaskItem.requestTask.payload,
              dre: {
                ...(
                  mockCompletedDreApplicationSubmitRequestTaskItem.requestTask
                    .payload as DreApplicationSubmitRequestTaskPayload
                ).dre,
                fee: {
                  feeDetails: {
                    dueDate: format(subDays(new Date(), 1), 'yyyy-MM-dd'),
                    hourlyRate: '3',
                    totalBillableHours: '34',
                  },
                  chargeOperator: true,
                },
              },
            } as DreApplicationSubmitRequestTaskPayload,
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should display reportable emissions section as needs review', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Reportable emissions');
      expect(page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim()).toEqual(
        'needs review',
      );
    });
  });
});
