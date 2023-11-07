import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { mockCompletedNonComplianceConclusionRequestTaskItem, updateMockedConclusion } from '../../test/mock';
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
      requestTaskItem: updateMockedConclusion({}, false),
    });
  });
  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary list', () => {
    expect(page.summaryListRows.map((el) => el.textContent.trim())).toEqual(['No', 'some comments', 'No', 'No']);
  });

  it('should submit status section true and submit the task', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(2);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION',
      requestTaskId: mockCompletedNonComplianceConclusionRequestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION_PAYLOAD',
        complianceRestored: false,
        comments: 'some comments',
        reissuePenalty: false,
        determinationCompleted: true,
      },
    });
  });
});
