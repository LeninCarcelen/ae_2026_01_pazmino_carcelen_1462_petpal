import React, { useEffect, useState } from 'react';
import {
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonPage,
  IonList,
  IonItem,
  IonLabel,
  IonBadge,
  IonItemSliding,
  IonItemOptions,
  IonItemOption,
  IonIcon,
  IonRefresher,
  IonRefresherContent,
  RefresherEventDetail
} from '@ionic/react';
import { trashOutline } from 'ionicons/icons';

import { userService } from '../../core/services/user.service';
import { UserResponse } from '../../core/models/user.model';

// Ruta protegida por RequireRole (roles={[Role.ADMIN]}) en TabsPage.tsx
const UserListPage: React.FC = () => {
  const [users, setUsers] = useState<UserResponse[]>([]);

  const load = (event?: CustomEvent<RefresherEventDetail>) => {
    userService
      .list()
      .then((data) => setUsers(data))
      .finally(() => event?.detail.complete());
  };

  useEffect(() => {
    load();
  }, []);

  const remove = (id: number) => {
    userService.remove(id).then(() => load());
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Usuarios (Administrator)</IonTitle>
        </IonToolbar>
      </IonHeader>

      <IonContent>
        <IonRefresher slot="fixed" onIonRefresh={load}>
          <IonRefresherContent />
        </IonRefresher>

        <IonList>
          {users.map((u) => (
            <IonItemSliding key={u.id}>
              <IonItem>
                <IonLabel>
                  <h2>{u.fullName || u.email}</h2>
                  <p>
                    {u.email} · {u.phone}
                  </p>
                </IonLabel>
                <IonBadge slot="end">{u.role}</IonBadge>
              </IonItem>
              <IonItemOptions side="end">
                <IonItemOption color="danger" onClick={() => remove(u.id)}>
                  <IonIcon icon={trashOutline} />
                </IonItemOption>
              </IonItemOptions>
            </IonItemSliding>
          ))}
        </IonList>
      </IonContent>
    </IonPage>
  );
};

export default UserListPage;
