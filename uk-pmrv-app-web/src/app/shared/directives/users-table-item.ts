export interface UsersTableItem {
  authorityCreationDate?: string;
  authorityStatus?: 'ACCEPTED' | 'ACTIVE' | 'DISABLED' | 'PENDING' | 'TEMP_DISABLED' | 'TEMP_DISABLED_PENDING';
  firstName?: string;
  lastName?: string;
  userId?: string;
  roleCode?: string;
  roleName?: string;
  jobTitle?: string;
}
