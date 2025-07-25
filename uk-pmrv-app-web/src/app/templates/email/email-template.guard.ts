import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { NotificationTemplateDTO, NotificationTemplatesService } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class EmailTemplateGuard {
  emailTemplate: NotificationTemplateDTO;

  constructor(private readonly notificationTemplatesService: NotificationTemplatesService) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.notificationTemplatesService.getNotificationTemplateById(Number(route.paramMap.get('templateId'))).pipe(
      tap((notificationTemplate) => (this.emailTemplate = notificationTemplate)),
      map((notificationTemplate) => !!notificationTemplate),
    );
  }

  resolve(): NotificationTemplateDTO {
    return this.emailTemplate;
  }
}
