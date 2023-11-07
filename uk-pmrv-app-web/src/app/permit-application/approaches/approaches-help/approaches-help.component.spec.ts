import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { ApproachesHelpComponent } from './approaches-help.component';

describe('ApproachesHelpComponent', () => {
  let component: ApproachesHelpComponent;
  let fixture: ComponentFixture<ApproachesHelpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ApproachesHelpComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(ApproachesHelpComponent);
    component = fixture.componentInstance;
  }

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
