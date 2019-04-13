import { CoreService } from './../services/core.service';
import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { TextService } from '../services/text.service';
import { FormBuilder, Validators, FormControl } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginData = this.fb.group({
    email: ['admin@galaxyvictor.com', [Validators.email, Validators.required]],
    password: ['12345', Validators.required]
  });

  @ViewChild('password') passwordInput: ElementRef;

  errorMessage: string;

  constructor(public core: CoreService, public ts: TextService, private fb: FormBuilder) { }

  ngOnInit() {

  }

  login() {
    this.core.login(this.loginData.value.email, this.loginData.value.password).subscribe(null, error => {
      if (error.status === 401) {
        this.errorMessage = this.ts.strings.invalidLoginCredentials;
        this.loginData.patchValue({password: ''});

        this.passwordInput.nativeElement.focus();
      }
    });
  }

  get email () {
    return this.loginData.controls.email as FormControl;
  }

  get password (): FormControl {
    return this.loginData.controls.password as FormControl;
  }
}
