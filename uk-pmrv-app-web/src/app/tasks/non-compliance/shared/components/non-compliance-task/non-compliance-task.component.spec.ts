import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../../../../../shared/shared.module';
import { TaskSharedModule } from '../../../../shared/task-shared-module';
import { NonComplianceTaskComponent } from './non-compliance-task.component';

describe('NonComplianceTaskComponent', () => {
  let component: NonComplianceTaskComponent;
  let fixture: ComponentFixture<NonComplianceTaskComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NonComplianceTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(NonComplianceTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
