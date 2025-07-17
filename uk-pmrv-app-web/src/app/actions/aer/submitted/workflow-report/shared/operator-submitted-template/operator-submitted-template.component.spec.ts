import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OperatorSubmittedTemplateComponent } from './operator-submitted-template.component';

describe('OperatorSubmittedTemplateComponent', () => {
  let component: OperatorSubmittedTemplateComponent;
  let fixture: ComponentFixture<OperatorSubmittedTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OperatorSubmittedTemplateComponent],
      imports: [HttpClientTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperatorSubmittedTemplateComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
