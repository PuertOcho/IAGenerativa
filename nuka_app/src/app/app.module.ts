import { NgModule } from '@angular/core';
import { BrowserModule, HAMMER_GESTURE_CONFIG } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';

import { IonicModule, IonicRouteStrategy, Platform } from '@ionic/angular';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { Storage } from '@ionic/storage'; 
import { AppDataModel } from './app.data.model';
import { PipesModule } from './pipes/pipes.module';
import { ComponentsModule } from './components/components.module';
import { Media } from '@ionic-native/media/ngx';
import { HTTP } from '@ionic-native/http/ngx';
import { File } from '@ionic-native/file/ngx';
import { Clipboard } from '@ionic-native/clipboard/ngx';
import { AuthLoginGuard } from './guards/auth-login.guard';
import { AuthMenuGuard } from './guards/auth-menu.guard';
import { DatePipe } from '@angular/common';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { FingerprintAIO } from '@ionic-native/fingerprint-aio/ngx'
import { AuthNoOpenLoginGuard } from './guards/auth-no-open-login.guard';
import { HammerModule } from "../../node_modules/@angular/platform-browser";
import { NgApexchartsModule } from "ng-apexcharts";
import { NgxGraphModule } from '@swimlane/ngx-graph';

@NgModule({ declarations: [AppComponent],
    bootstrap: [AppComponent], imports: [BrowserModule,
        IonicModule.forRoot({
            rippleEffect: false,
            mode: 'ios'
        }),
        AppRoutingModule,
        PipesModule,
        ComponentsModule,
        HammerModule,
        NgApexchartsModule, NgxGraphModule], providers: [{ provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
        Storage, AppDataModel, Media, HTTP, File, Clipboard, Platform,
        AuthLoginGuard, AuthMenuGuard, AuthNoOpenLoginGuard, DatePipe, FingerprintAIO,
        { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true }, provideHttpClient(withInterceptorsFromDi())] })
export class AppModule { }
