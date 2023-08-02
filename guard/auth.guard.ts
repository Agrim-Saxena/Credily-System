import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { Constant } from '../constants/Constant';
import { Route } from '../constants/Route';
import { AuthService } from '../services/auth.service';
import { DataService } from '../services/data.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  readonly Constant = Constant;
  readonly Route = Route;

  constructor(
    private _data:DataService,
    private _authService:AuthService,
    private _router:Router){

  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    // debugger

    var token=localStorage.getItem(Constant.TOKEN);
    if(Constant.EMPTY_STRINGS.includes(token)){

      this._router.navigate(['/auth']);
      return false;
    }else{
      return true;
    }
  }
  
}
