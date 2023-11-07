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
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  class Page extends BasePage<SummaryComponent> {
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

    fixture = TestBed.createComponent(SummaryComponent);
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
        payloadType: 'WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION_PAYLOAD',
        withholdingOfAllowances: {
          reasonType: 'DETERMINING_A_SURRENDER_APPLICATION',
          regulatorComments: 'sadhnDF',
          year: 2027,
        },
        sectionsCompleted: { DETAILS_CHANGE: true },
      },
      requestTaskActionType: 'WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION',
      requestTaskId: 698,
    });

    expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: TestBed.inject(ActivatedRoute) });
  });
});
