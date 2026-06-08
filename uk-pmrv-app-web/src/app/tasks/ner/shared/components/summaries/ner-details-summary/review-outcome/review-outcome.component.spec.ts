import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { nerReviewMockState } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { NerReviewOutcomeComponent } from './review-outcome.component';

describe('ReviewOutcomeComponent', () => {
  let component: NerReviewOutcomeComponent;
  let fixture: ComponentFixture<NerReviewOutcomeComponent>;
  let store: CommonTasksStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerReviewOutcomeComponent],
      providers: [CapitalizeFirstPipe, provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(nerReviewMockState);

    fixture = TestBed.createComponent(NerReviewOutcomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
