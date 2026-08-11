import React from 'react';
import { Redirect, Route } from 'react-router-dom';
import {
  IonTabs,
  IonTabBar,
  IonTabButton,
  IonIcon,
  IonLabel,
  IonRouterOutlet
} from '@ionic/react';
import {
  personCircleOutline,
  peopleOutline,
  pawOutline,
  medkitOutline,
  medicalOutline,
  calendarOutline,
  shieldCheckmarkOutline
} from 'ionicons/icons';

import { useAuth } from '../core/auth/AuthContext';
import { RequireRole } from '../core/guards/RequireRole';
import { Role } from '../core/models/role.model';

import OwnerListPage from '../pages/owners/OwnerListPage';
import PetListPage from '../pages/pets/PetListPage';
import VaccineListPage from '../pages/vaccines/VaccineListPage';
import VeterinarianListPage from '../pages/veterinarians/VeterinarianListPage';
import AppointmentListPage from '../pages/appointments/AppointmentListPage';
import UserListPage from '../pages/users/UserListPage';
import ProfilePage from '../pages/profile/ProfilePage';

const TabsPage: React.FC = () => {
  const { isAdmin } = useAuth();

  return (
    <IonTabs>
      <IonRouterOutlet>
        <Route exact path="/tabs/owners" component={OwnerListPage} />
        <Route exact path="/tabs/pets" component={PetListPage} />
        <Route exact path="/tabs/vaccines" component={VaccineListPage} />
        <Route exact path="/tabs/veterinarians" component={VeterinarianListPage} />
        <Route exact path="/tabs/appointments" component={AppointmentListPage} />
        <Route exact path="/tabs/users">
          <RequireRole roles={[Role.ADMIN]}>
            <UserListPage />
          </RequireRole>
        </Route>
        <Route exact path="/tabs/profile" component={ProfilePage} />
        <Route exact path="/tabs">
          <Redirect to="/tabs/profile" />
        </Route>
      </IonRouterOutlet>

      <IonTabBar slot="bottom">
        <IonTabButton tab="owners" href="/tabs/owners">
          <IonIcon icon={peopleOutline} />
          <IonLabel>Owners</IonLabel>
        </IonTabButton>

        <IonTabButton tab="pets" href="/tabs/pets">
          <IonIcon icon={pawOutline} />
          <IonLabel>Pets</IonLabel>
        </IonTabButton>

        <IonTabButton tab="appointments" href="/tabs/appointments">
          <IonIcon icon={calendarOutline} />
          <IonLabel>Citas</IonLabel>
        </IonTabButton>

        <IonTabButton tab="vaccines" href="/tabs/vaccines">
          <IonIcon icon={medkitOutline} />
          <IonLabel>Vacunas</IonLabel>
        </IonTabButton>

        <IonTabButton tab="veterinarians" href="/tabs/veterinarians">
          <IonIcon icon={medicalOutline} />
          <IonLabel>Vets</IonLabel>
        </IonTabButton>

        {isAdmin && (
          <IonTabButton tab="users" href="/tabs/users">
            <IonIcon icon={shieldCheckmarkOutline} />
            <IonLabel>Usuarios</IonLabel>
          </IonTabButton>
        )}

        <IonTabButton tab="profile" href="/tabs/profile">
          <IonIcon icon={personCircleOutline} />
          <IonLabel>Perfil</IonLabel>
        </IonTabButton>
      </IonTabBar>
    </IonTabs>
  );
};

export default TabsPage;
