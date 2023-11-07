import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { DoalTaskComponent } from './doal-task.component';

describe('DoalTaskComponent', () => {
  let component: DoalTaskComponent;
  let fixture: ComponentFixture<DoalTaskComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(DoalTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
