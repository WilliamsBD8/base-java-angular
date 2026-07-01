import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CategoryService } from '../../core/services/category.service';
import { Category } from '../../core/models/category.model';
import { AuthService } from '../../core/services/auth.service';
import { hasRole } from '../../core/utils/helpers';
import { confirmAction, getErrorMessage, showError, showSuccess } from '../../core/utils/alert';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [ReactiveFormsModule, PaginationComponent],
  templateUrl: './categories.component.html'
})
export class CategoriesComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly categoryService = inject(CategoryService);
  private readonly authService = inject(AuthService);

  readonly categories = signal<Category[]>([]);
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly editingId = signal<number | null>(null);
  readonly page = signal(0);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    description: ['', Validators.required]
  });

  get isAdmin(): boolean {
    return hasRole(this.authService.getRoles(), 'ADMIN');
  }

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(page = 0): void {
    this.loading.set(true);
    this.page.set(page);
    this.categoryService.getAll(page, 10).subscribe({
      next: (response) => {
        this.categories.set(response.data.content);
        this.totalPages.set(response.data.totalPages);
        this.totalElements.set(response.data.totalElements);
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        showError(getErrorMessage(error));
      }
    });
  }

  startCreate(): void {
    this.editingId.set(null);
    this.form.reset({ name: '', description: '' });
  }

  startEdit(category: Category): void {
    this.editingId.set(category.id);
    this.form.patchValue({
      name: category.name,
      description: category.description
    });
  }

  async submit(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    const payload = this.form.getRawValue();
    const id = this.editingId();

    const request$ = id
      ? this.categoryService.update(id, payload)
      : this.categoryService.create(payload);

    request$.subscribe({
      next: async (response) => {
        this.saving.set(false);
        await showSuccess(response.message);
        this.startCreate();
        this.loadCategories(this.page());
      },
      error: async (error) => {
        this.saving.set(false);
        await showError(getErrorMessage(error));
      }
    });
  }

  async remove(id: number): Promise<void> {
    const confirmed = await confirmAction('Eliminar categoría', 'Esta acción no se puede deshacer.');
    if (!confirmed) return;

    this.categoryService.delete(id).subscribe({
      next: async (response) => {
        await showSuccess(response.message);
        this.loadCategories(this.page());
      },
      error: async (error) => {
        await showError(getErrorMessage(error));
      }
    });
  }
}
