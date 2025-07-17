import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { mockPermanentCessationState } from '../testing/mock-permanent-cessation-payload';
import { PermanentCessationNotifyOperatorComponent } from './notify-operator.component';

describe('PermanentCessationNotifyOperatorComponent', () => {
  let component: PermanentCessationNotifyOperatorComponent;
  let fixture: ComponentFixture<PermanentCessationNotifyOperatorComponent>;
  let store: CommonTasksStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermanentCessationNotifyOperatorComponent],
      providers: [provideRouter([]), DestroySubject],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockPermanentCessationState);

    fixture = TestBed.createComponent(PermanentCessationNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
