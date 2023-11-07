import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../../../../../shared/shared.module';
import { TaskSharedModule } from '../../../../shared/task-shared-module';
import { DreTaskComponent } from './dre-task.component';

describe('DreTaskComponent', () => {
  let component: DreTaskComponent;
  let fixture: ComponentFixture<DreTaskComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DreTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(DreTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
