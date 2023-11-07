import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AviationEmissionsReasonsFormComponent } from './aviation-emissions-reasons-form.component';

describe('AviationEmissionsReasonsFormComponent', () => {
  let fixture: ComponentFixture<AviationEmissionsReasonsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AviationEmissionsReasonsFormComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
