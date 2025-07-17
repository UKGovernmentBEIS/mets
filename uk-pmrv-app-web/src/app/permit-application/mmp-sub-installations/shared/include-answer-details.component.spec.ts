import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PermitApplicationModule } from '@permit-application/permit-application.module';

import { IncludeAnswerDetailsComponent } from './include-answer-details.component';

describe('IncludeAnswerDetailsComponent', () => {
  let component: IncludeAnswerDetailsComponent;
  let fixture: ComponentFixture<IncludeAnswerDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, IncludeAnswerDetailsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IncludeAnswerDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
