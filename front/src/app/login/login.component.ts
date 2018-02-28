import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AccountService} from '../service/account.service';
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

  constructor(private accountService: AccountService,
              private router: Router,
              private cdr: ChangeDetectorRef) { }

  ngOnInit() {

    this.state = 'base';
  }

  ngAfterViewInit() {

    this.registerKeystoreUploadListener();
  }

  clickLogin() {

    this.fileInput.nativeElement.click();
  }

  clickSubmit() {

    this.accountService.login(this.password);
  }

  clickCreateAccount() {

    this.accountService.createAccount().subscribe(() => this.state = 'register');
  }

  clickEncryptKeyfile() {

    this.downloadFile.nativeElement.click();
    this.state = 'base';
    this.cdr.detectChanges();
    this.registerKeystoreUploadListener();
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

  encryptLink(): string {

    return environment.apiUrl + '/encrypt-keyfile?' +
      'privateKey=' + this.accountService.account.keys.privateKey +
      '&publicKey=' + this.accountService.account.keys.publicKey +
      '&password=' + this.password;
  }

  registerKeystoreUploadListener() {

    console.log('abc');
    this.fileInput.nativeElement.addEventListener('change', this.readFile.bind(this), false);
  }
}
