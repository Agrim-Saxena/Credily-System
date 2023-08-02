import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Route } from 'src/app/constants/Route';
import { AuthService } from 'src/app/services/auth.service';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-forgot',
  templateUrl: './forgot.component.html',
  styleUrls: ['./forgot.component.css']
})
export class ForgotComponent implements OnInit {

  otp: string = '';
  userName: string = '';
  otpToggle: boolean = false;
  requestOtpToggle: boolean = false;
  invalidOTPToggle: boolean = false;
  checkingOTPToggle:boolean=false;

  readonly Route = Route;

  constructor(private authService: AuthService,
    private dataService: DataService,
    private router: Router) { }

  ngOnInit(): void {
  }

  requestOtp() {
    this.requestOtpToggle = true;
    this.authService.requestOtp(this.userName).subscribe(response=>{
      if (response.status) {
        this.otpToggle = true;
        this.requestOtpToggle = false;
      } else {
        this.otpToggle = false;
        this.requestOtpToggle = false;
        this.dataService.showToast(response.message);
      }
    },error=>{
      this.otpToggle = false;
      this.requestOtpToggle = false;
      this.dataService.showToast('Network Error !');
    })
  }

  verifyOtp() {
    this.authService.verifyOtp(this.userName, this.otp).subscribe(response=>{
      if (response.status) {
        this.dataService.userName = this.userName;
        this.router.navigate([this.Route.RESET_PASSWORD]);
      } else {
        this.dataService.showToast(response.message);
      }
    },error=>{
      this.dataService.showToast('Network Error !');
    })
  }

  checkOTP(){
    this.invalidOTPToggle=false;
    if(this.otp!=undefined && this.otp!=null && this.otp!='' && this.otp.length==6 && !this.checkingOTPToggle){
        this.verifyOtp();
    }
  }

}
