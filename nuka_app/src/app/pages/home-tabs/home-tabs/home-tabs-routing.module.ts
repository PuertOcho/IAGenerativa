import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeTabsPage } from './home-tabs.page';
import { AuthChatsGuard } from 'src/app/guards/auth-chats.guard';
import { AuthLoginGuard } from 'src/app/guards/auth-login.guard';

const routes: Routes = [
  {
    path: '',
    component: HomeTabsPage,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'status',
        //redirectTo: 'chats' // TEST
      },
      {
        path: 'status',
        loadChildren: () => import('../status/status.module').then(m => m.StatusPageModule),
        canActivate: [AuthLoginGuard]
      },
      {
        path: 'settings',
        loadChildren: () => import('../settings/settings.module').then(m => m.SettingsPageModule),
        canActivate: [AuthLoginGuard]
      },
      {
        path: 'chats',
        loadChildren: () => import('../chats/chats.module').then(m => m.ChatsPageModule),
        canActivate: [AuthChatsGuard, AuthLoginGuard]
      },
      {
        path: 'details:id',
        loadChildren: () => import('../chat-detail/chat-detail.module').then(m => m.ChatDetailPageModule),
        canActivate: [AuthLoginGuard]
      }
    ]
  },
  {
    path: '',
    loadChildren: () => import('../status/status.module').then(m => m.StatusPageModule),
    canActivate: [AuthLoginGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HomeTabsPageRoutingModule { }
