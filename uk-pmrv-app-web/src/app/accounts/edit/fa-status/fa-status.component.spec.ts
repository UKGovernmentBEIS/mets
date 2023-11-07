import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, asyncData, BasePage, MockType } from '@testing';

import { InstallationAccountUpdateService } from 'pmrv-api';

import { mockedAccountPermit } from '../../testing/mock-data';
import { FaStatusComponent } from './fa-status.component';

describe('FaStatusComponent', () => {
  let component: FaStatusComponent;
  let fixture: ComponentFixture<FaStatusComponent>;
  let accountUpdateService: MockType<InstallationAccountUpdateService>;
  let page: Page;
  let route: ActivatedRouteStub;

  class Page extends BasePage<FaStatusComponent> {
    get faStatus() {
      return this.queryAll<HTMLInputElement>('input[name$="faStatus"]');
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub(undefined, undefined, {
      accountPermit: mockedAccountPermit,
    });

    accountUpdateService = {
      updateFaStatus: jest.fn().mockReturnValue(asyncData(null)),
    };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [FaStatusComponent],
      providers: [
        { provide: InstallationAccountUpdateService, useValue: accountUpdateService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FaStatusComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change site name on form submit', () => {
    page.faStatus[1].click();
    fixture.detectChanges();

    page.confirmButton.click();
    fixture.detectChanges();

    expect(accountUpdateService.updateFaStatus).toHaveBeenCalled();
    expect(accountUpdateService.updateFaStatus).toHaveBeenCalledWith(1, {
      faStatus: false,
    });
  });
});
