import { SortOption } from "../models/sort-option.model";

export const drugSortOptions: SortOption[] = [
  {
    sortBy: 'name',
    order: 'asc'
  },
  {
    sortBy: 'name',
    order: 'desc'
  }
];

export const activitiesSortOptions: SortOption[] = [
  {
    sortBy: 'activityTime',
    order: 'asc'
  },
  {
    sortBy: 'activityTime',
    order: 'desc'
  },
  {
    sortBy: 'duration',
    order: 'asc'
  },
  {
    sortBy: 'duration',
    order: 'desc'
  },
  {
    sortBy: 'averageHeartRate',
    order: 'asc'
  },
  {
    sortBy: 'averageHeartRate',
    order: 'desc'
  },
  {
    sortBy: 'caloriesBurned',
    order: 'asc'
  },
  {
    sortBy: 'caloriesBurned',
    order: 'desc'
  }
];

export const doctorSortOptions: SortOption[] = [
  {
    sortBy: 'firstName',
    order: 'asc'
  },
  {
    sortBy: 'firstName',
    order: 'desc'
  },
  {
    sortBy: 'lastName',
    order: 'asc'
  },
  {
    sortBy: 'lastName',
    order: 'desc'
  },
  {
    sortBy: 'specialization',
    order: 'asc'
  },
  {
    sortBy: 'specialization',
    order: 'desc'
  },
  {
    sortBy: 'city',
    order: 'asc'
  },
  {
    sortBy: 'city',
    order: 'desc'
  }
];

export const friendshipSortOptions: SortOption[] = [
  {
    sortBy: 'createdAt',
    order: 'asc'
  },
  {
    sortBy: 'createdAt',
    order: 'desc'
  }
];
