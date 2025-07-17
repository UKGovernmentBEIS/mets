import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { UIConfigurationService } from 'pmrv-api';

import { BasePage } from '../../../testing';
import { ServiceBannerComponent } from './service-banner.component';

describe('ServiceBannerComponent', () => {
  let component: ServiceBannerComponent;
  let fixture: ComponentFixture<ServiceBannerComponent>;
  let page: Page;

  const mockUIConfigurationService = {
    getUIConfiguration: () =>
      of({
        features: {
          aviation: true,
        },
        analytics: {
          measurementId: '',
          propertyId: '',
        },
        notificationAlerts: [
          {
            subject: 'subject4',
            body: 'body4',
          },
          {
            subject: 'subject6',
            body: 'body6',
          },
          {
            subject: 'subject3',
            body: 'body3',
          },
        ],
      }),
  };

  class Page extends BasePage<ServiceBannerComponent> {
    get banners() {
      return Array.from(this.queryAll<HTMLElement>('.govuk-notification-banner')).map((banner) => ({
        subject: banner
          .querySelector('.govuk-notification-banner__header .govuk-notification-banner__title ')
          .textContent.trim(),
        body: banner.querySelector('.govuk-notification-banner__heading').textContent.trim(),
      }));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ServiceBannerComponent],
      providers: [
        {
          provide: UIConfigurationService,
          useValue: mockUIConfigurationService,
        },
      ],
      imports: [HttpClientTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(ServiceBannerComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);

    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the banners', () => {
    expect(page.banners.length).toBe(3);

    expect(page.banners).toEqual([
      { subject: 'subject4', body: 'body4' },
      { subject: 'subject6', body: 'body6' },
      { subject: 'subject3', body: 'body3' },
    ]);
  });
});
