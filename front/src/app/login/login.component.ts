import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AccountService} from '../service/account.service';
import {KeysDto} from "../model";
import {Router} from "@angular/router";
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, AfterViewInit {

  @ViewChild('fileInput') fileInput: ElementRef;
  @ViewChild('downloadFile') downloadFile: ElementRef;

  state: string;
  password: string;

  constructor(private accountService: AccountService, private router: Router) {

    this.state = 'base';
  }

  ngOnInit() {
  }

  ngAfterViewInit() {

    this.fileInput.nativeElement.addEventListener('change', this.readFile.bind(this), false);
  }

  clickLogin() {

    this.fileInput.nativeElement.click();
  }

  clickSubmit() {

    this.accountService.login(this.password).subscribe(rsp => {

      if(rsp.status == 'success') this.router.navigate(['/account']);
    });
  }

  clickCreateAccount() {

    this.accountService.createAccount().subscribe(() => this.state = 'register');
  }

  clickEncryptKeyfile() {

    this.downloadFile.nativeElement.click();
    this.state = 'base';
  }

  readFile(e) {

    let file = e.target.files[0];
    if (!file) return;

    let reader = new FileReader();

    reader.onload = (ev: any) => {
      let content = ev.target.result;
      if(this.verifyKeyfile(content)) this.accountService.keyfileContent = content;
      this.state = 'login';
    };

    reader.readAsText(file);
  }

  verifyKeyfile(fileContent: string): boolean {

    return true;
  }

  keys(): KeysDto {

    return this.accountService.keysDto;
  }

  encryptLink(): string {

    return environment.apiUrl + '/encrypt-keyfile?' +
      'privateKey=' + this.accountService.keysDto.privateKey +
      '&publicKey=' + this.accountService.keysDto.publicKey +
      '&password=' + this.password;
  }

}
