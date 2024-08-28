import { PageEvent } from '@angular/material/paginator';

export interface Pagination {
  pageSize: number;
  pageIndex: number;
  totalElements: number;
  pageSizeOptions: number[];
  pageEvent: PageEvent | undefined;
}
