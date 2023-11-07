import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import {
  mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem,
  updateMockedDailyPenaltyNotice,
} from '../../test/mock';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: CommonTasksStore;
  let router: Router;
  let page: Page;

  const tasksService = mockClass(TasksService);
  const activatedRouteStub = new ActivatedRouteStub();

  class Page extends BasePage<SummaryComponent> {
    get summaryListRows() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__value');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        DestroySubject,
      ],
    }).compileComponents();
    router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockedDailyPenaltyNotice({}, false),
    });
  });
  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary list', () => {
    expect(page.summaryListRows.map((el) => el.textContent.trim())).toEqual(['supportingDoc1.pdf', 'some comments']);
  });

  it('should submit status section true', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SAVE_APPLICATION',
      requestTaskId: mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SAVE_APPLICATION_PAYLOAD',
        comments: 'some comments',
        dailyPenaltyNotice: '2b587c89-1973-42ba-9682-b3ea5453b9dd',
        dailyPenaltyCompleted: true,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRouteStub });
  });
});
