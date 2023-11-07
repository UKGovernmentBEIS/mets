import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { AerModule } from '../../aer.module';
import { EmissionsSummaryComponent } from './emissions-summary.component';

describe('EmissionsSummaryComponent', () => {
  let component: EmissionsSummaryComponent;
  let fixture: ComponentFixture<EmissionsSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();

    fixture = TestBed.createComponent(EmissionsSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
