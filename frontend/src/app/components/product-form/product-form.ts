import { Component, OnInit, ChangeDetectorRef  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService, Product } from '../../services/product';

@Component({
  selector: 'app-product-form',
  imports: [CommonModule, FormsModule],
  templateUrl: './product-form.html',
  styleUrl: './product-form.css'
})
export class ProductForm implements OnInit {

  product: Product = {
    sku: '',
    nombre: '',
    precio: 0,
    cantidad: 0
  };

  isEditMode = false;
  productId: number | null = null;
  errorMessage = '';

  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.isEditMode = true;
      this.productId = Number(idParam);
      this.loadProduct(this.productId);
    }
  }

    loadProduct(id: number): void {
    this.productService.getById(id).subscribe({
      next: (data) => {
        this.product = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = 'No se pudo cargar el producto';
        this.cdr.detectChanges();
      }
    });
  }
    onSubmit(): void {
    this.errorMessage = '';

    if (this.isEditMode && this.productId) {
      this.productService.update(this.productId, this.product).subscribe({
        next: () => {
          this.router.navigate(['/products']);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Error al actualizar el producto';
          this.cdr.detectChanges();
        }
      });
    } else {
      this.productService.create(this.product).subscribe({
        next: () => {
          this.router.navigate(['/products']);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Error al crear el producto';
          this.cdr.detectChanges();
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/products']);
  }
}