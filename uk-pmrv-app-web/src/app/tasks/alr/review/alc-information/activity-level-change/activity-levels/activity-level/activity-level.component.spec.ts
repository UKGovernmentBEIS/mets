import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { alrMockReviewState } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AlrActivityLevelComponent } from './activity-level.component';

describe('ActivityLevelComponent', () => {
  let component: AlrActivityLevelComponent;
  let fixture: ComponentFixture<AlrActivityLevelComponent>;
  let store: CommonTasksStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrActivityLevelComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrMockReviewState);

    fixture = TestBed.createComponent(AlrActivityLevelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
