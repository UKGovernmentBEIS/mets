import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../../testing';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { WithholdingAllowancesModule } from '../../withholding-allowances.module';
import { mockState } from '../testing/mock-state';
import { WithdrawSummaryComponent } from './withdraw-summary.component';

describe('WithdrawWithdrawSummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: WithdrawSummaryComponent;
  let fixture: ComponentFixture<WithdrawSummaryComponent>;

  class Page extends BasePage<WithdrawSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const tasksService = mockClass(TasksService);
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WithholdingAllowancesModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(WithdrawSummaryComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionPayload: {
        payloadType: 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION_PAYLOAD',
        withholdingWithdrawal: {
          reason: 'Some reason',
        },
        sectionsCompleted: { WITHDRAWAL_REASON_CHANGE: true },
      },
      requestTaskActionType: 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION',
      requestTaskId: 698,
    });

    expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: TestBed.inject(ActivatedRoute) });
  });
});
