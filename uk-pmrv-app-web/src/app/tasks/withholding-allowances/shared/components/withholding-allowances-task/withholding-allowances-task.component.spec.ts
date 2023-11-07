import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { WithholdingAllowancesTaskComponent } from './withholding-allowances-task.component';

describe('WithholdingAllowancesTaskComponent', () => {
  let component: WithholdingAllowancesTaskComponent;
  let fixture: ComponentFixture<WithholdingAllowancesTaskComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WithholdingAllowancesTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(WithholdingAllowancesTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
