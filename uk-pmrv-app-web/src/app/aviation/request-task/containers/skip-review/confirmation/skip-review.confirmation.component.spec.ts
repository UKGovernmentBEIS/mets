import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { mockState } from '@aviation/request-task/aer/ukets/tasks/send-report/testing/mock-state';
import { SkipReviewConfirmationComponent } from '@aviation/request-task/containers';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

describe('SkipReviewConfirmationComponent', () => {
  let component: SkipReviewConfirmationComponent;
  let fixture: ComponentFixture<SkipReviewConfirmationComponent>;
  let store: RequestTaskStore;
  const tasksService = mockClass(TasksService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
      ],
      declarations: [SkipReviewConfirmationComponent],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    store.setState(mockState);

    fixture = TestBed.createComponent(SkipReviewConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the confirmation messages correctly', () => {
    const panelTitle = fixture.debugElement.nativeElement.querySelector('.govuk-panel__title').textContent.trim();
    const bodyMessage = fixture.debugElement.nativeElement.querySelector('.govuk-panel__body').textContent.trim();

    expect(panelTitle).toBe('Emissions report completed');
    expect(bodyMessage).toBe('Your reference code is: AEM00016-2022');
  });
});
