import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedModule } from '../../../shared/shared.module';
import { ReturnedComponent } from './returned.component';

describe('ReturnedComponent', () => {
  let component: ReturnedComponent;
  let fixture: ComponentFixture<ReturnedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReturnedComponent],
      imports: [SharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(ReturnedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReturnedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
