import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { AlrService } from '@tasks/alr/core';
import { alrMockAuthorityState } from '@tasks/alr/test/mock-authority';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AlrAuthorityNotifyOperatorComponent } from './notify-operator.component';

describe('NotifyOperatorComponent', () => {
  let component: AlrAuthorityNotifyOperatorComponent;
  let fixture: ComponentFixture<AlrAuthorityNotifyOperatorComponent>;
  let store: CommonTasksStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrAuthorityNotifyOperatorComponent],
      providers: [provideRouter([]), AlrService, CapitalizeFirstPipe, DestroySubject],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrMockAuthorityState);

    fixture = TestBed.createComponent(AlrAuthorityNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
