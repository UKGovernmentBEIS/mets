import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { WithholdingAllowancesModule } from '@tasks/withholding-allowances/withholding-allowances.module';
import { KeycloakService } from 'keycloak-angular';

import { TaskSharedModule } from '../../shared/task-shared-module';
import { PeerReviewWaitComponent } from './peer-review-wait.component';

describe('PeerReviewWaitComponent', () => {
  let component: PeerReviewWaitComponent;
  let fixture: ComponentFixture<PeerReviewWaitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [WithholdingAllowancesModule, SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PeerReviewWaitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
