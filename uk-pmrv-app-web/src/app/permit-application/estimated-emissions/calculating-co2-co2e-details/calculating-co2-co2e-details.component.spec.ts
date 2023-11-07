import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalculatingCo2Co2eDetailsComponent } from './calculating-co2-co2e-details.component';

describe('CalculatingCo2Co2eDetailsComponent', () => {
  let component: CalculatingCo2Co2eDetailsComponent;
  let fixture: ComponentFixture<CalculatingCo2Co2eDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CalculatingCo2Co2eDetailsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CalculatingCo2Co2eDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
