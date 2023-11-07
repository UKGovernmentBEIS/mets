import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedModule } from '../../../shared/shared.module';
import { SubmittedComponent } from './submitted.component';

describe('SubmittedComponent', () => {
  let component: SubmittedComponent;
  let fixture: ComponentFixture<SubmittedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubmittedComponent],
      imports: [SharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(SubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
