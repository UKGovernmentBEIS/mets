import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { TaskSharedModule } from '../../../../shared/task-shared-module';
import { initialState } from '../../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { DreTaskComponent } from '../../../shared/components/dre-task/dre-task.component';
import { dreCompleted, mockCompletedDreApplicationSubmitRequestTaskItem, updateMockedDre } from '../../../test/mock';
import { InformationSourceComponent } from './information-source.component';

describe('InformationSourceComponent', () => {
  let component: InformationSourceComponent;
  let fixture: ComponentFixture<InformationSourceComponent>;

  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<InformationSourceComponent> {
    get informationSource() {
      return this.getInputValue('#informationSource');
    }
    set informationSource(value: string) {
      this.setInputValue('#informationSource', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(InformationSourceComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for new dre', () => {
    const route = new ActivatedRouteStub({});
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [InformationSourceComponent, DreTaskComponent],
        imports: [RouterTestingModule, SharedModule, TaskSharedModule],
        providers: [
          KeycloakService,
          { provide: TasksService, useValue: tasksService },
          { provide: ActivatedRoute, useValue: route },
        ],
      }).compileComponents();
    });

    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockedDre({ informationSources: [] }, false),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['You must add at least one item']);

      page.informationSource = 'inf source1';

      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DRE_SAVE_APPLICATION',
        requestTaskId: mockCompletedDreApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DRE_SAVE_APPLICATION_PAYLOAD',
          dre: {
            ...dreCompleted,
            informationSources: ['inf source1'],
          },
          sectionCompleted: false,
        },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../..', 'information-sources'], { relativeTo: route });
    });
  });

  describe('for existing dre', () => {
    const route = new ActivatedRouteStub({ index: '0' });
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [InformationSourceComponent, DreTaskComponent],
        imports: [RouterTestingModule, SharedModule, TaskSharedModule],
        providers: [
          KeycloakService,
          { provide: TasksService, useValue: tasksService },
          { provide: ActivatedRoute, useValue: route },
        ],
      }).compileComponents();
    });

    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockedDre({ informationSources: ['item1'] }, false),
      });
    });
    beforeEach(createComponent);

    it('should update', () => {
      expect(page.informationSource.trim()).toEqual('item1');
      page.informationSource = 'inf source1 updated';

      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DRE_SAVE_APPLICATION',
        requestTaskId: mockCompletedDreApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DRE_SAVE_APPLICATION_PAYLOAD',
          dre: {
            ...dreCompleted,
            informationSources: ['inf source1 updated'],
          },
          sectionCompleted: false,
        },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../..', 'information-sources'], { relativeTo: route });
    });
  });
});
