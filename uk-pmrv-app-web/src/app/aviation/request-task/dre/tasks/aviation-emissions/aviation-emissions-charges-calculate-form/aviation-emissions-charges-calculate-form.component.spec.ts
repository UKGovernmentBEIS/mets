import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AviationEmissionsChargesCalculateFormComponent } from './aviation-emissions-charges-calculate-form.component';

describe('AviationEmissionsChargesCalculateFormComponent', () => {
  let fixture: ComponentFixture<AviationEmissionsChargesCalculateFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AviationEmissionsChargesCalculateFormComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
