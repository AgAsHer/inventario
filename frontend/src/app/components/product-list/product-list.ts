import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProductService, Product } from '../../services/product';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-product-list',
  imports: [CommonModule],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css'
})
export class ProductList implements OnInit {

  products: Product[] = [];
  isAdmin = false;
  errorMessage = '';

  constructor(
    private productService: ProductService,
    private authService: Auth,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.getRole() === 'ADMIN';
    this.loadProducts();
  }

   loadProducts(): void {
    this.productService.getAll().subscribe({
      next: (data) => {
        this.products = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = 'No se pudieron cargar los productos';
        this.cdr.detectChanges();
      }
    });
  }

  deleteProduct(id: number | undefined): void {
    if (!id) return;

    if (confirm('¿Seguro que deseas eliminar este producto?')) {
      this.productService.delete(id).subscribe({
        next: () => {
          this.loadProducts();
        },
        error: (err) => {
          this.errorMessage = 'No se pudo eliminar el producto';
        }
      });
    }
  }

  goToCreate(): void {
    this.router.navigate(['/products/new']);
  }

  goToEdit(id: number | undefined): void {
    if (!id) return;
    this.router.navigate(['/products/edit', id]);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}