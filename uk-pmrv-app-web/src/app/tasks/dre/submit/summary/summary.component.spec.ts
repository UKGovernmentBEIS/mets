import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';
import moment from 'moment';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { DreTaskComponent } from '../../shared/components/dre-task/dre-task.component';
import { dreCompleted, mockCompletedDreApplicationSubmitRequestTaskItem, updateMockedDre } from '../../test/mock';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get reasonValues() {
      return this.queryAll<HTMLElement>(
        'app-determination-reason-summary-template .govuk-summary-list .govuk-summary-list__value',
      );
    }
    get monitoringApproachesValues() {
      return this.queryAll<HTMLElement>(
        'app-monitoring-approaches-summary-template .govuk-summary-list .govuk-summary-list__value',
      );
    }
    get reportableEmissionsValues() {
      return this.queryAll<HTMLElement>('app-reportable-emissions-summary-template govuk-table tbody td:nth-child(2)');
    }
    get sustainableBiomassValues() {
      return this.queryAll<HTMLElement>('app-reportable-emissions-summary-template govuk-table tbody td:nth-child(3)');
    }
    get informationSourcesValues() {
      return this.queryAll<HTMLElement>(
        'app-information-sources-summary-template .govuk-summary-list .govuk-summary-list__value',
      );
    }
    get feeValues() {
      return this.queryAll<HTMLElement>('app-fee-summary-template .govuk-summary-list .govuk-summary-list__value');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryComponent, DreTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

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

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display dre', () => {
    expect(page.reasonValues.map((el) => el.textContent.trim())).toEqual([
      'Verified report not submitted in accordance with the order',
      'Yes',
      'dfdf',
      'supportingDoc1.pdf',
      'fdfdf',
    ]);

    expect(page.monitoringApproachesValues.map((el) => el.textContent.trim())).toEqual(['Fallback approach']);

    expect(page.reportableEmissionsValues.map((el) => el.textContent.trim())).toEqual(['1  t', '1  tCO2e']);

    expect(page.sustainableBiomassValues.map((el) => el.textContent.trim())).toEqual(['2 t', '2 tCO2e']);

    expect(page.informationSourcesValues.map((el) => el.textContent.trim())).toEqual(['fgf']);

    const dateFormatOptions: Intl.DateTimeFormatOptions = {
      timeZone: 'Europe/London',
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    };
    const formatter = Intl.DateTimeFormat('en-GB-u-hc-h12', dateFormatOptions);
    const dueDateString = formatter
      .formatToParts(new Date(moment().add(1, 'day').set('hour', 12).toISOString()))
      .map(({ value }, index) => (index === 9 ? '' : value))
      .join('');

    expect(page.feeValues.map((el) => el.textContent.trim())).toEqual([
      'Yes',
      '34 hours',
      '£3 per hour',
      dueDateString,
      '£102',
    ]);
  });

  it('should submit status section true', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'DRE_SAVE_APPLICATION',
      requestTaskId: mockCompletedDreApplicationSubmitRequestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'DRE_SAVE_APPLICATION_PAYLOAD',
        dre: dreCompleted,
        sectionCompleted: true,
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
  });
});
