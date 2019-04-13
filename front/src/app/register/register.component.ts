import { CoreService } from './../services/core.service';
import { Component, OnInit } from '@angular/core';
import { TextService } from '../services/text.service';
import { FormBuilder, Validators, FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  registerData = this.fb.group({
    email: ['', [Validators.email, Validators.required]],
    password: ['', [Validators.minLength(5), Validators.required]],
    repeatPassword: ['']
  }, {validator: this.checkEqualPassword } );

  errorMessage: string;

  constructor(private core: CoreService, public ts: TextService, private fb: FormBuilder) { }

  ngOnInit() {

  }

  register() {
    this.core.register(this.registerData.value.email, this.registerData.value.password).subscribe(null, error => {
      if (error.status === 400) {
        this.errorMessage = error.error.error.message;
      }
      if (error.status === 409) {
        this.errorMessage = error.error.error.message;
      }

    });
  }

  get email () {
    return this.registerData.controls.email as FormControl;
  }

  get password (): FormControl {
    return this.registerData.controls.password as FormControl;
  }

  get repeatPassword (): FormControl {
    return this.registerData.controls.repeatPassword as FormControl;
  }

  checkEqualPassword(fg: FormGroup) {
    const p1 = fg.value.password;
    const p2 = fg.value.repeatPassword;

    if (p1 !== p2) {
      return { notEquals: true };
    }
    return null;
  }
}
