import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.nuka.memento',
  appName: 'Memento',
  webDir: 'www',
  server: {
    androidScheme: 'https',
    cleartext: true
  },
  plugins: {
    SplashScreen: {
      launchShowDuration: 500,
      backgroundColor: '#9b9ca1',
      showSpinner: false,
      androidSpinnerStyle: 'small',
      splashFullScreen: true,
      splashImmersive: true
    },
    LocalNotifications: {
      iconColor: "#488AFF"
    },
    BackgroundRunner: {
      label: 'com.nuka.memento.check',
      src: 'background.js', 
      event: 'syncTest',
      repeat: true,
      interval: 1,
      autoStart: true,
    },
    PushNotifications: {
      presentationOptions: ["badge", "sound", "alert"]
    }
  }
};

export default config;
