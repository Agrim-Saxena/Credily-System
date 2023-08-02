import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { AppComponent } from 'src/app/app.component';
import { Constant } from 'src/app/constants/Constant';
import { Route } from 'src/app/constants/Route';
import { TokenResponse } from 'src/app/models/TokenResponse';
import { AuthService } from 'src/app/services/auth.service';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  readonly Constant = Constant;
  readonly Route = Route;

  userName: string = '';
  password: string = '';
  togglePassword : string = Constant.PASSWORD_INVISIBLE;
  togglePasswords : string = Constant.PASSWORD_INVISIBLE;
  error: string = '';
  loading: boolean = false;
  tokenResponse: TokenResponse = new TokenResponse();

  constructor(private _router: Router,
    public _authService: AuthService,
    public _dataService: DataService) {
    var token = localStorage.getItem(this.Constant.TOKEN);

    if (!this.Constant.EMPTY_STRINGS.includes(token)) {
      this._router.navigate([this.Route.DASHBOARD]);
    }

  }

  ngOnInit() { }

  login() {
    
    this.loading = true;

    this._authService.login(this.userName, this.password).subscribe(async response => {
      if (response.status && response.object != null) {

        //@ts-ignore
        this.tokenResponse = response.object;

        if (this.tokenResponse != null && this.tokenResponse.modules != null) {
          this._dataService.modules = this.tokenResponse.modules;
          localStorage.setItem(Constant.MODULE, JSON.stringify(this.tokenResponse.modules));
        }

        localStorage.setItem(Constant.TOKEN, this.tokenResponse.idToken);
        localStorage.setItem(Constant.REFRESH_TOKEN, this.tokenResponse.refreshToken);
        localStorage.setItem(Constant.ROLE, this.tokenResponse.role);
        localStorage.setItem(Constant.ROLE_TYPE, this.tokenResponse.roleType);
        localStorage.setItem(Constant.IS_INTERNAL_ROLE, this.tokenResponse.isInternalRole+'');
        localStorage.setItem(Constant.CLIENT_BUSINESS_UUID, this.tokenResponse.clientBusinessUuid);
        localStorage.setItem(Constant.ACCOUNT_UUID, this.tokenResponse.accountUuid);
        localStorage.setItem(Constant.USER_UUID, this.tokenResponse.userUuid);
        localStorage.setItem(Constant.USER_EMAIL, this.tokenResponse.email);
        localStorage.setItem(Constant.USER_IS_PASSWORD_CHANGE, this.tokenResponse.isPasswordReset+'');

        await this._dataService.getUserModules();

        if (this.tokenResponse.isPasswordReset == 0) {
          this.resetField = true;
          return;
        } else {
          /**
          * send to dashboard
          */
          if (this.tokenResponse.role == 'Provider') {
            this._router.navigate([Route.EXPIRABLE_PROVIDER_DASHBOARD]);
          } else if (this.tokenResponse.role == 'Client') {
            this._router.navigate([Route.PROVIDER]);
          } else {
            this._router.navigate([Route.DASHBOARD]);
          }
        }

        this.error = '';
        this.userName = '';
        this.password = '';

      } else if (!response.status && response.object != null) {
        this.userName = '';
        this.password = '';
        this.error = response.message;
      } else {
        this.userName = '';
        this.password = '';
        this.error = 'user name or password is invalid.'
      }
      this.loading = false;
    }, error => {
      this.loading = false;
      this._dataService.showToast('Network error');
    })

  }

  togglePasswordField(){
    if(this.togglePassword == Constant.PASSWORD_INVISIBLE){
      this.togglePassword = Constant.PASSWORD_VISIBLE;
    }else{
      this.togglePassword = Constant.PASSWORD_INVISIBLE;
    }
  }


  //Reset password at first login ---->
  resetField: boolean = false;
  confirmLoading: boolean = false;
  confrimNewPassword: string = '';
  newPassword: string = '';
  showError: boolean = false;

  reset() {
    this.showError = false;
    if (this.newPassword != this.confrimNewPassword) {
      this.showError = true;
      return;
    }

    this.confirmLoading = true;
    this._authService.resetPassword(this.newPassword,this.tokenResponse.accountUuid).subscribe((response: any) => {
      if (response.status) {
        this.confirmLoading = false;
        this.resetField = true;
        if (this.tokenResponse.role == 'Provider') {
          this._router.navigate([Route.EXPIRABLE_PROVIDER_DASHBOARD]);
        } else if (this.tokenResponse.role == 'Client') {
          this._router.navigate([Route.PROVIDER]);
        } else {
          this._router.navigate([Route.DASHBOARD]);
        }
      }else{
        this._dataService.showToast('Network error');
        this.confirmLoading = false;
      }
    }, (error: any) => {
      this.confirmLoading = false;
      this.resetField = true;
      this._dataService.showToast('Network error');
    });

  }

  togglePasswordFieldd(){
    if(this.togglePasswords == Constant.PASSWORD_INVISIBLE){
      this.togglePasswords = Constant.PASSWORD_VISIBLE;
    }else{
      this.togglePasswords = Constant.PASSWORD_INVISIBLE;
    }
  }

  checkConfirmPassword() {
    debugger
    if(this.newPassword != this.confrimNewPassword){
      this.showError = true;
    }else{
      this.showError = false;
    }
  }

}
