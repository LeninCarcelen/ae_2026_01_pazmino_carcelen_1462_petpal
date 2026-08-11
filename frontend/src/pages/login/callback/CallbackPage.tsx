import React, { useEffect, useState } from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import { IonContent, IonSpinner, IonText, IonPage } from '@ionic/react';
import { useAuth } from '../../../core/auth/AuthContext';

/**
 * Página de callback registrada como redirect_uri en el App Client de Cognito.
 * Equivale a lo que hacíamos manualmente en Postman: copiar el "code" de la
 * URL y ejecutar "Exchange code for token".
 */
const CallbackPage: React.FC = () => {
  const { handleCallback } = useAuth();
  const history = useHistory();
  const location = useLocation();
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const code = new URLSearchParams(location.search).get('code');

    if (!code) {
      setError('No se recibió el código de autorización de Cognito.');
      return;
    }

    handleCallback(code)
      .then(() => history.replace('/tabs/profile'))
      .catch(() => {
        // El code de Cognito expira en ~20-30s y es de un solo uso (mismo
        // comportamiento visto en Postman -> invalid_grant).
        setError('El código expiró o ya fue usado. Vuelve a iniciar sesión.');
      });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <IonPage>
      <IonContent className="ion-padding ion-text-center">
        <IonSpinner name="crescent" />
        <IonText>
          <p>Autenticando con Cognito...</p>
        </IonText>
        {error && (
          <IonText color="danger">
            <p>{error}</p>
          </IonText>
        )}
      </IonContent>
    </IonPage>
  );
};

export default CallbackPage;
