import { Injectable, Pipe, PipeTransform } from '@angular/core';

@Injectable({ providedIn: 'root' })
@Pipe({
  name: 'userRoleAll',
  standalone: false,
})
export class UserRoleAllTypesPipe implements PipeTransform {
  transform(role): string {
    switch (role) {
      case 'operator_admin':
        return 'Operator admin';
      case 'operator':
        return 'Operator';
      case 'consultant_agent':
        return 'Consultant';
      case 'emitter_contact':
        return 'Emitter Contact';
      default:
        return role ? role.charAt(0).toUpperCase() + role.replace(/_/g, ' ').slice(1).toLowerCase() : '';
    }
  }
}
