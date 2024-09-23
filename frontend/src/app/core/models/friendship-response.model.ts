import { UserResponse } from "./user/user-response.model";

export interface FriendshipResponse {
  friendshipId: string;
  user: UserResponse;
  status: FriendshipStatus;
  updatedAt: string;
  permissions: FriendshipPermissions;
}

export interface FriendshipPermissions {
  userMedicineAccess: boolean;
  activitiesAccess: boolean;
  parametersAccess: boolean;
}

export enum FriendshipStatus {
  WAITING = 'WAITING',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED'
}
