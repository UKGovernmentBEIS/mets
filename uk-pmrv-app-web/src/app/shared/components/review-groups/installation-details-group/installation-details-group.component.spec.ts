import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { InstallationDetailsGroupComponent } from '@shared/components/review-groups/installation-details-group/installation-details-group.component';
import { SharedModule } from '@shared/shared.module';
import { mockAerApplyPayload } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

describe('InstallationDetailsGroupComponent', () => {
  let component: InstallationDetailsGroupComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: ` <app-installation-details-group [payload]="payload"></app-installation-details-group> `,
  })
  class TestComponent {
    payload = mockAerApplyPayload as AerApplicationVerificationSubmitRequestTaskPayload;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    component = fixture.debugElement.query(By.directive(InstallationDetailsGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name')).map(
        (el) => el.textContent.trim(),
      ),
    ).toEqual([
      'Installation details',
      'Pollutant Release and Transfer Register codes',
      'NACE codes for the main activities at the installation',
      'Regulated activities',
      'Monitoring plan changes',
      'Monitoring approaches',
    ]);
  });
});
