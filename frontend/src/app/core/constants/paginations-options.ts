import { Pagination } from "../models/pagination.model";

export const basePagination: Pagination= {
  pageSize: 10,
  pageIndex: 0,
  totalElements: 0,
  pageSizeOptions: [5, 10, 25, 50, 100],
  pageEvent: undefined
}
