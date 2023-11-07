import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseWorkflowSummaryTemplateComponent } from './choose-workflow-summary-template.component';

describe('ChooseWorkflowSummaryTemplateComponent', () => {
  let component: ChooseWorkflowSummaryTemplateComponent;
  let fixture: ComponentFixture<ChooseWorkflowSummaryTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChooseWorkflowSummaryTemplateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChooseWorkflowSummaryTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
