import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Constant } from 'src/app/constants/Constant';
import { Route } from 'src/app/constants/Route';
import { AuthService } from 'src/app/services/auth.service';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {

  readonly Route = Route;
  readonly Constant = Constant;

  showError: boolean = false;
  newPassword: string = '';
  confirmPassword: string = '';
  confirmLoading: boolean = false;
  toggleNewPassword : string = Constant.PASSWORD_INVISIBLE;
  toggleConfirmPassword : string = Constant.PASSWORD_INVISIBLE;

  constructor(private authService: AuthService,
    private dataSevice: DataService,
    private router: Router) { }

  ngOnInit(): void {
    if (this.dataSevice.userName == undefined || this.dataSevice.userName == null || this.dataSevice.userName == '') {
      this.router.navigate([Route.FORGOT_PASSWORD]);
    }
  }

  togglePasswordField(){
    if(this.toggleNewPassword == Constant.PASSWORD_INVISIBLE){
      this.toggleNewPassword = Constant.PASSWORD_VISIBLE;
    }else{
      this.toggleNewPassword = Constant.PASSWORD_INVISIBLE;
    }
  }

  toggleConfirmPasswordField(){
    if(this.toggleConfirmPassword == Constant.PASSWORD_INVISIBLE){
      this.toggleConfirmPassword = Constant.PASSWORD_VISIBLE;
    }else{
      this.toggleConfirmPassword = Constant.PASSWORD_INVISIBLE;
    }
  }

  checkSamePassword() {
    if (this.newPassword != this.confirmPassword) {
      this.showError = true;
    } else {
      this.showError = false;
    }
  }

  reset() {
    this.authService.updatePassword(this.dataSevice.userName, this.confirmPassword).subscribe(response=>{
      if (response.status) {
        this.router.navigate([Route.AUTH_ROUTE]);
      }
    })
  }
}
