import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Business } from 'src/app/interfaces/business.interface';


@Component({
  selector: 'app-finish-registration',
  templateUrl: './finish-registration.component.html',
  styleUrls: ['./finish-registration.component.css']
})
export class FinishRegistrationComponent {

  finishRegistrationForm: FormGroup;
  private userEmailAddress!: string;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.finishRegistrationForm = this.formBuilder.group({
      businessName: ['', [Validators.required]],
      phoneNumber: ['', [Validators.required]],
      city: ['', [Validators.required]],
      postalCode: ['', [Validators.required]],
      address: ['', [Validators.required]],
    });
    this.route.queryParams.subscribe(params => {
      const data = params['email'];
      this.userEmailAddress = data;
    });
  }

  // finishRegistration() {
  //   if (this.finishRegistrationForm.valid) {
  //     const data: Business = this.finishRegistrationForm.value;
  //     this.authService.finishRegistration(this.userEmailAddress, data).subscribe({
  //       next: response => {
  //         console.log('finish registration successful', response);
  //         this.router.navigate(['manage']);
  //       },
  //       error: error => {
  //         console.log('finish registration failed', error);
  //       }
  //     });
  //   } else {
  //     console.log('Form is invalid');
  //   }
  // }

}
