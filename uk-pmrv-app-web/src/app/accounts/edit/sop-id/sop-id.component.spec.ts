import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { InstallationAccountUpdateService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, MockType } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { mockedAccountPermit } from '../../testing/mock-data';
import { SopIdComponent } from './sop-id.component';

describe('SopIdComponent', () => {
  let component: SopIdComponent;
  let fixture: ComponentFixture<SopIdComponent>;
  let accountUpdateService: MockType<InstallationAccountUpdateService>;
  let page: Page;
  let route: ActivatedRouteStub;

  class Page extends BasePage<SopIdComponent> {
    set sopIdValue(value: string) {
      this.setInputValue('#sopId', value);
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
      updateInstallationAccountSopId: jest.fn().mockReturnValue(asyncData(null)),
    };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [SopIdComponent],
      providers: [
        { provide: InstallationAccountUpdateService, useValue: accountUpdateService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SopIdComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change site name on form submit', () => {
    page.sopIdValue = '123456';
    fixture.detectChanges();

    page.confirmButton.click();
    fixture.detectChanges();

    expect(accountUpdateService.updateInstallationAccountSopId).toHaveBeenCalled();
    expect(accountUpdateService.updateInstallationAccountSopId).toHaveBeenCalledWith(1, { sopId: 123456 });
  });
});
