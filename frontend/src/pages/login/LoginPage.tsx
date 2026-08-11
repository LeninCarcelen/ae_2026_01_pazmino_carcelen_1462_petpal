import React from 'react';
import { IonContent, IonButton, IonIcon, IonText, IonPage } from '@ionic/react';
import { logInOutline } from 'ionicons/icons';
import { useAuth } from '../../core/auth/AuthContext';
import './LoginPage.css';

const LoginPage: React.FC = () => {
  const { login } = useAuth();

  return (
    <IonPage>
      <IonContent className="ion-padding login-content">
        <div className="login-wrapper">
          <h1>🐾 PetPal</h1>
          <IonText color="medium">
            <p>Sistema de gestión veterinaria — inicia sesión con tu cuenta de Cognito.</p>
          </IonText>

          <IonButton expand="block" size="large" onClick={login}>
            <IonIcon slot="start" icon={logInOutline} />
            Iniciar sesión
          </IonButton>
        </div>
      </IonContent>
    </IonPage>
  );
};

export default LoginPage;
