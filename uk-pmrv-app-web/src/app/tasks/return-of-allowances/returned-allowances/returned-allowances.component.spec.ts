import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { RequestTaskPayload } from 'pmrv-api';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { TaskSharedModule } from '../../shared/task-shared-module';
import { initialState } from '../../store/common-tasks.state';
import { CommonTasksStore } from '../../store/common-tasks.store';
import {
  mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem,
  updateMockedReturnOfAllowancesReturned,
} from '../test/mock';
import { ReturnedAllowancesComponent } from './returned-allowances.component';

describe('ReturnedAllowancesComponent', () => {
  let component: ReturnedAllowancesComponent;
  let fixture: ComponentFixture<ReturnedAllowancesComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<ReturnedAllowancesComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReturnedAllowancesComponent],
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(ReturnedAllowancesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  describe('for new return of allowances returned', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem,
          requestTask: {
            ...mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem.requestTask,
            payload: {
              sectionsCompleted: {
                PROVIDE_RETURNED_DETAILS: false,
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

    it('should display return of allowances returned section as not started', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Have allowances been returned?');
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
        requestTaskItem: updateMockedReturnOfAllowancesReturned({}, false),
      });
    });

    beforeEach(createComponent);

    it('should display section as in progress', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Have allowances been returned?');
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
        requestTaskItem: mockCompletedReturnOfAllowancesReturnedApplicationSubmitRequestTaskItem,
      });
    });

    beforeEach(createComponent);

    it('should display section as completed', async () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Have allowances been returned?');
      expect(page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim()).toEqual(
        'completed',
      );
    });
  });
});
