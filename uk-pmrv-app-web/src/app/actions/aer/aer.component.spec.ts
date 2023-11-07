import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerComponent } from './aer.component';

describe('AerComponent', () => {
  let component: AerComponent;
  let fixture: ComponentFixture<AerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AerComponent],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
