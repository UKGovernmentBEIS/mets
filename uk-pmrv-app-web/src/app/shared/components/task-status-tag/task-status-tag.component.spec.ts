import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedModule } from '../../shared.module';
import { TaskItemStatus } from '../../task-list/task-list.interface';

describe('TaskStatusTagComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    standalone: false,
    template: `
      <app-task-status-tag [status]="status"></app-task-status-tag>
    `,
  })
  class TestComponent {
    status: TaskItemStatus = 'not started';
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent],
      imports: [SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  const setStatus = (status: TestComponent['status']) => {
    component.status = status;
    fixture.detectChanges();
  };

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render a filled tag for statuses with fill style', () => {
    setStatus('not started');

    const tag = element.querySelector<HTMLElement>('govuk-tag.app-task-list__tag strong');
    expect(tag).toBeTruthy();
    expect(tag.classList.contains('govuk-tag--blue')).toBeTruthy();
    expect(tag.textContent.trim()).toEqual('Not started');
  });

  it('should render an in progress tag with teal color', () => {
    setStatus('in progress');

    const tag = element.querySelector<HTMLElement>('govuk-tag.app-task-list__tag strong');
    expect(tag.classList.contains('govuk-tag--teal')).toBeTruthy();
    expect(tag.textContent.trim()).toEqual('In progress');
  });

  it('should render a tinted status for cannot start yet', () => {
    setStatus('cannot start yet');

    expect(element.querySelector('strong')).toBeNull();

    const status = element.querySelector<HTMLDivElement>('div.app-task-list__tag');
    expect(status).toBeTruthy();
    expect(status.classList.contains('govuk-task-list__status--cannot-start-yet')).toBeTruthy();
    expect(status.textContent.trim()).toEqual('Cannot start yet');
  });

  it('should render plain text for completed status', () => {
    setStatus('complete');

    expect(element.querySelector('strong')).toBeNull();

    const status = element.querySelector<HTMLDivElement>('div.app-task-list__tag');
    expect(status).toBeTruthy();
    expect(status.classList.contains('govuk-task-list__status--cannot-start-yet')).toBeFalsy();
    expect(status.textContent.trim()).toEqual('Completed');
  });
});
