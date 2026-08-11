import React, { useEffect, useState } from 'react';
import { IonHeader, IonToolbar, IonTitle, IonContent, IonPage, IonList, IonItem, IonLabel, IonButton, IonSpinner, IonBadge } from '@ionic/react';

import { userService } from '../../core/services/user.service';
import { useAuth } from '../../core/auth/AuthContext';
import { UserResponse } from '../../core/models/user.model';
import { Role } from '../../core/models/role.model';

const formatDate = (iso: string): string => {
  if (!iso) return '';
  const d = new Date(iso);
  if (isNaN(d.getTime())) return iso;
  return d.toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' });
};

const ProfilePage: React.FC = () => {
  const { getCognitoSub, getRoles, logout } = useAuth();

  const [profile, setProfile] = useState<UserResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [notRegistered, setNotRegistered] = useState(false);

  const load = () => {
    setLoading(true);
    setNotRegistered(false);
    userService
      .me()
      .then((u) => setProfile(u))
      .catch(() => setNotRegistered(true))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  /**
   * Si Cognito autenticó al usuario pero todavía no existe su fila en
   * la tabla app_user, se crea aquí (POST /api/users está abierto a
   * los 4 roles, pensado exactamente para este auto-registro).
   */
  const createProfile = () => {
    const sub = getCognitoSub();
    const roles = getRoles();
    if (!sub) return;

    userService
      .createProfile({
        cognitoSub: sub,
        email: '',
        fullName: '',
        phone: '',
        role: roles[0] ?? Role.OWNER
      })
      .then(() => load());
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Mi perfil</IonTitle>
        </IonToolbar>
      </IonHeader>

      <IonContent className="ion-padding">
        {loading && (
          <div className="ion-text-center">
            <IonSpinner name="crescent" />
          </div>
        )}

        {profile && !loading && (
          <IonList>
            <IonItem>
              <IonLabel>
                <h2>{profile.fullName || profile.email}</h2>
                <p>{profile.email}</p>
              </IonLabel>
              <IonBadge slot="end">{profile.role}</IonBadge>
            </IonItem>
            <IonItem>
              <IonLabel>Teléfono</IonLabel>
              <IonLabel slot="end">{profile.phone || '—'}</IonLabel>
            </IonItem>
            <IonItem>
              <IonLabel>Miembro desde</IonLabel>
              <IonLabel slot="end">{formatDate(profile.createdAt)}</IonLabel>
            </IonItem>
          </IonList>
        )}

        {notRegistered && !loading && (
          <div className="ion-text-center ion-padding">
            <p>Tu cuenta de Cognito está autenticada, pero aún no tienes un perfil creado en PetPal.</p>
            <IonButton onClick={createProfile}>Crear mi perfil</IonButton>
          </div>
        )}

        <IonButton expand="block" color="danger" fill="outline" onClick={logout} className="ion-margin-top">
          Cerrar sesión
        </IonButton>
      </IonContent>
    </IonPage>
  );
};

export default ProfilePage;
