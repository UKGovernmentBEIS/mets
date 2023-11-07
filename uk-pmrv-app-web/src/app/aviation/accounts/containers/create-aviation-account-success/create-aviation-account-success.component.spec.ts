import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateAviationAccountSuccessComponent } from './create-aviation-account-success.component';

describe('CreateAccountSuccessComponent', () => {
  let component: CreateAviationAccountSuccessComponent;
  let fixture: ComponentFixture<CreateAviationAccountSuccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateAviationAccountSuccessComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateAviationAccountSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
