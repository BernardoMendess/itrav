import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-auth-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './auth-page.component.html',
  styleUrl: './auth-page.component.css'
})
export class AuthPageComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly mode = signal<'login' | 'register'>('login');

  readonly form = this.formBuilder.nonNullable.group({
    name: [''],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['']
  });

  switchMode(mode: 'login' | 'register'): void {
    this.mode.set(mode);
    this.errorMessage.set('');
    this.form.patchValue({ password: '', confirmPassword: '' });
  }

  submit(): void {
    if (this.form.invalid || this.loading()) {
      this.form.markAllAsTouched();
      return;
    }

    const formValue = this.form.getRawValue();
    if (this.mode() === 'register') {
      if (!formValue.name.trim()) {
        this.errorMessage.set('Informe seu nome para criar a conta');
        return;
      }

      if (formValue.password !== formValue.confirmPassword) {
        this.errorMessage.set('As senhas não conferem');
        return;
      }
    }

    this.loading.set(true);
    this.errorMessage.set('');

    const action =
      this.mode() === 'register'
        ? this.authService.register({
            name: formValue.name.trim(),
            email: formValue.email.trim(),
            password: formValue.password
          })
        : this.authService.login({
            email: formValue.email.trim(),
            password: formValue.password
          });

    action.subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.loading.set(false);
        if (error?.status === 0) {
          this.errorMessage.set('Não foi possível conectar ao backend. Verifique se a API está rodando em http://localhost:8080');
          return;
        }
        const message = error?.error?.message ?? 'Falha ao autenticar usuário';
        const validationDetails = error?.error?.data;
        if (validationDetails && typeof validationDetails === 'object') {
          const first = Object.values(validationDetails)[0];
          this.errorMessage.set(typeof first === 'string' ? first : message);
          return;
        }
        this.errorMessage.set(message);
      }
    });
  }
}
