import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: true,
  templateUrl: './pagination.component.html'
})
export class PaginationComponent {
  @Input() page = 0;
  @Input() totalPages = 0;
  @Input() totalElements = 0;
  @Output() pageChange = new EventEmitter<number>();

  get canGoPrev(): boolean {
    return this.page > 0;
  }

  get canGoNext(): boolean {
    return this.page < this.totalPages - 1;
  }

  prev(): void {
    if (this.canGoPrev) {
      this.pageChange.emit(this.page - 1);
    }
  }

  next(): void {
    if (this.canGoNext) {
      this.pageChange.emit(this.page + 1);
    }
  }
}
