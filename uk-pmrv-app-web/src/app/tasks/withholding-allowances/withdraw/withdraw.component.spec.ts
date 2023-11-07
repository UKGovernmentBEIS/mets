import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskDTO, RequestTaskPayload } from 'pmrv-api';

import { TaskSharedModule } from '../../shared/task-shared-module';
import { CommonTasksStore } from '../../store/common-tasks.store';
import { WithholdingAllowancesModule } from '../withholding-allowances.module';
import { mockState } from './testing/mock-state';
import { WithdrawComponent } from './withdraw.component';

describe('WithdrawComponent', () => {
  let component: WithdrawComponent;
  let fixture: ComponentFixture<WithdrawComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<WithdrawComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item'));
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  function createComponent() {
    fixture = TestBed.createComponent(WithdrawComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [WithholdingAllowancesModule, SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  describe('for new  withdraw withholding of allowances', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...mockState,
        requestTaskItem: {
          ...mockState.requestTaskItem,
          requestTask: {
            ...mockState.requestTaskItem.requestTask,
            payload: {
              ...mockState.requestTaskItem.requestTask.payload,
              withholdingWithdrawal: {},
              sectionsCompleted: {},
            } as RequestTaskPayload,
          } as RequestTaskDTO,
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display header', () => {
      expect(page.heading).toEqual('Withdraw withholding of allowances notice');
    });
    it('should display Provide details of breach section as in progress', () => {
      expect(page.sections.length).toEqual(1);

      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Provide reason for withdrawal details');

      expect(page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim()).toEqual(
        'not started',
      );
    });
  });

  describe('for complete  withholding of allowances', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...mockState,
        requestTaskItem: {
          ...mockState.requestTaskItem,
          requestTask: {
            ...mockState.requestTaskItem.requestTask,
            payload: {
              ...mockState.requestTaskItem.requestTask.payload,
              payloadType: 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT_PAYLOAD',
              withholdingWithdrawal: {
                reason: 'Some reason',
              },
              sectionsCompleted: {
                WITHDRAWAL_REASON_CHANGE: true,
              },
            } as RequestTaskPayload,
          } as RequestTaskDTO,
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display Provide details of breach section as not started', () => {
      expect(page.sections.length).toEqual(1);

      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Provide reason for withdrawal details');

      expect(page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim()).toEqual(
        'completed',
      );
    });
  });
});
