import { PageEvent } from '@angular/material/paginator';
import { Pagination } from './../../../core/models/pagination.model';
import { Component, computed, input, output } from '@angular/core';

export interface TableColumn {
  title: string,
  displayedColumn: string
}

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrl: './table.component.scss'
})
export class TableComponent {
  data = input.required<any[]>();
  columns = input.required<TableColumn[]>();
  type = input.required<'editable' | 'selectable'>();
  pagination = input<Pagination | null>(null);
  isSearching = input.required<boolean | null>();
  edit = output<any>();
  delete = output<any>();
  select = output<any>();
  pageChange = output<PageEvent>();
  displayedColumns = computed(() => {
    let test = this.columns().map(column => column.displayedColumn);

    if (this.type() === 'editable') {
      test = [...test, 'actionButtons'];
    }

    if (this.type() === 'selectable') {
      test = [...test, 'selectButton'];
    }

    return test;
  });

  onEdit(item: any): void {
    this.edit.emit(item);
  }

  onDelete(item: any): void {
    this.delete.emit(item);
  }

  onSelect(item: any): void {
    this.select.emit(item);
  }

  handlePageEvent(e: PageEvent): void {
    this.pageChange.emit(e);
  }
}