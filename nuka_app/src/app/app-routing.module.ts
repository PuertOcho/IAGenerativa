import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { AuthLoginGuard } from './guards/auth-login.guard';
import { AuthMenuGuard } from './guards/auth-menu.guard';
import { AuthNoOpenLoginGuard } from './guards/auth-no-open-login.guard';


const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./pages/login/login.module').then( m => m.LoginPageModule),
    canActivate: [AuthNoOpenLoginGuard]
  },
  {
    path: 'home-tabs',
    canActivate: [AuthMenuGuard, AuthLoginGuard],
    loadChildren: () => import('./pages/home-tabs/home-tabs/home-tabs.module').then( m => m.HomeTabsPageModule)
  },
  {
    path: 'shopping-tabs',
    canActivate: [AuthMenuGuard, AuthLoginGuard],
    loadChildren: () => import('./pages/shopping-tabs/shopping-tabs/shopping-tabs.module').then( m => m.ShoppingTabsPageModule)
  },
  {
    path: 'details/:id',
    loadChildren: () => import('./pages/home-tabs/chat-detail/chat-detail.module').then( m => m.ChatDetailPageModule),
    canActivate: [AuthLoginGuard]
  },
  {
    path: 'login',
    loadChildren: () => import('./pages/login/login.module').then( m => m.LoginPageModule),
    canActivate: [AuthNoOpenLoginGuard]
  },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full'
  }

];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
