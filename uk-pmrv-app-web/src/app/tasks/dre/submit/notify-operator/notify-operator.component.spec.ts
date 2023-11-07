import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { dreCompleted, updateMockedDre } from '@tasks/dre/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

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

  describe('for new dre', () => {
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
        requestTaskItem: updateMockedDre(dreCompleted, false),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });
  });
});
