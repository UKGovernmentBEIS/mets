import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { inspectionSubmitMockState } from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { InspectionNotifyOperatorComponent } from './notify-operator.component';

describe('NotifyOperatorComponent', () => {
  let component: InspectionNotifyOperatorComponent;
  let fixture: ComponentFixture<InspectionNotifyOperatorComponent>;
  let store: CommonTasksStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InspectionNotifyOperatorComponent],
      providers: [provideRouter([]), DestroySubject],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(inspectionSubmitMockState);

    fixture = TestBed.createComponent(InspectionNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
