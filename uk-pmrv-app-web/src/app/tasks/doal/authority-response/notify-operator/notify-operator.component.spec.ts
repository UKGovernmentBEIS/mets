import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { NotifyOperatorComponent } from '@tasks/doal/authority-response/notify-operator/notify-operator.component';
import { mockDoalAuthorityResponseRequestTaskTaskItem } from '@tasks/doal/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

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

  describe('notify operator', () => {
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
        requestTaskItem: mockDoalAuthorityResponseRequestTaskTaskItem,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });
  });
});
