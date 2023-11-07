import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AviationEmissionsFormComponent } from './aviation-emissions-form.component';

describe('AviationEmissionsFormComponent', () => {
  let fixture: ComponentFixture<AviationEmissionsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AviationEmissionsFormComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
