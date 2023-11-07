import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AviationEmissionsSummaryTemplateComponent } from './aviation-emissions-summary-template.component';

describe('AviationEmissionsSummaryTemplateComponent', () => {
  let fixture: ComponentFixture<AviationEmissionsSummaryTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AviationEmissionsSummaryTemplateComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
