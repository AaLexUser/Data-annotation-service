import { EUserRole, IUserInfo } from '@/api/user';
import queryClient from '@/constant/queryClient';

export interface IAccessValue {
  canReadPage: boolean; // Whether can access the page
  canReadUsersPage: boolean; // Whether can access users management page
  canUsersPagePermission: boolean; // Whether has users management module permission
}

type UserRoleMap = Record<EUserRole, IAccessValue>;

const roleAccessMap: UserRoleMap = {
  [EUserRole.admin]: {
    canReadPage: true,
    canUsersPagePermission: true,
    canReadUsersPage: true,
  },
  [EUserRole.user]: {
    canReadPage: false,
    canUsersPagePermission: false,
    canReadUsersPage: false,
  },
};

export function hasPermission(permission: keyof IAccessValue) {
  const user = queryClient.getQueryData<IUserInfo>(['user_info']);
  if (!user?.role) return false;
  return roleAccessMap[user?.role][permission];
}