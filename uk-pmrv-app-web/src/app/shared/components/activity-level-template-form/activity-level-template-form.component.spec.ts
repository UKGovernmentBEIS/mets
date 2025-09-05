import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ALR_TASK_FORM } from '@tasks/alr/core';
import { alrActivityLevelFormProvider } from '@tasks/alr/review/alc-information/activity-level-change/activity-levels/activity-level/activity-level-form.provider';
import { alrMockReviewState } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub } from '@testing';

import { ActivityLevelTemplateFormComponent } from './activity-level-template-form.component';

describe('ActivityLevelTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: CommonTasksStore;

  const activatedRoute = new ActivatedRouteStub({ taskId: 1, index: '0' }, null, null);

  @Component({
    template: `
      <form [formGroup]="form">
        <app-activity-level-template-form></app-activity-level-template-form>
      </form>
    `,
    providers: [alrActivityLevelFormProvider],
  })
  class TestComponent {
    constructor(@Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup) {}
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActivityLevelTemplateFormComponent],
      providers: [provideHttpClient(withInterceptorsFromDi()), { provide: ActivatedRoute, useValue: activatedRoute }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrMockReviewState);

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
