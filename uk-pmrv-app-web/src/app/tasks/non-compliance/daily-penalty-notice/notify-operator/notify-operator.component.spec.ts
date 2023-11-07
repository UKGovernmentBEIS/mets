import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { updateMockedDailyPenaltyNotice } from '../../test/mock';
import { NotifyOperatorComponent } from './notify-operator.component';

describe('NotifyOperatorComponent', () => {
  let store: CommonTasksStore;
  let component: NotifyOperatorComponent;
  let fixture: ComponentFixture<NotifyOperatorComponent>;

  const tasksService = mockClass(TasksService);

  const createComponent = () => {
    fixture = TestBed.createComponent(NotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for new daily penalty', () => {
    const route = new ActivatedRouteStub({});
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [NotifyOperatorComponent],
        imports: [RouterTestingModule, SharedModule, TaskSharedModule],
        providers: [
          { provide: TasksService, useValue: tasksService },
          { provide: ActivatedRoute, useValue: route },
          DestroySubject,
        ],
      }).compileComponents();
    });

    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        requestTaskItem: updateMockedDailyPenaltyNotice({}, false),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });
  });
});
