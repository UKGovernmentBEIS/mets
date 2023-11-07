import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { TotalEmissionsSummaryTemplateComponent } from './total-emissions-summary-template.component';

describe('TotalEmissionsSummaryTemplateComponent', () => {
  let fixture: ComponentFixture<TotalEmissionsSummaryTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(TotalEmissionsSummaryTemplateComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
