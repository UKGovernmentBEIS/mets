import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { AlrService } from '@tasks/alr/core';
import { alrMockReviewState } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AlrNotifyOperatorComponent } from './notify-operator.component';

describe('NotifyOperatorComponent', () => {
  let component: AlrNotifyOperatorComponent;
  let fixture: ComponentFixture<AlrNotifyOperatorComponent>;
  let store: CommonTasksStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrNotifyOperatorComponent],
      providers: [provideRouter([]), AlrService, CapitalizeFirstPipe, DestroySubject],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrMockReviewState);

    fixture = TestBed.createComponent(AlrNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
