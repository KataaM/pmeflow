import Vue from 'vue'
import Router from 'vue-router'

import DashboardView from '@/view/dashboard-view'
//import DossiersView from '@/view/dossiers-view'
import EntreprisesListView from '@/view/entreprises-list-view.vue'
import ContactsListView from '@/view/contacts-list-view.vue'
import OpportunitesListView from '@/view/opportunites-list-view.vue'
import OpportunitesDetailsView from '@/view/opportunites-details-view.vue'
import ContactsDetailsView from '@/view/contacts-details-view.vue'
import EntreprisesDetailsView from '@/view/entreprises-details-view.vue'

Vue.use(Router)

const router = new Router({
  base: "/private/",
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'default', component: EntreprisesListView,
      meta: { authorize: [] }
    },
    {
      path: '/entreprises',
      name: 'entreprises', component: EntreprisesListView,
      meta: { authorize: [] }
    },
    {
      path: '/contacts',
      name: 'contacts', component: ContactsListView,
      meta: { authorize: [] }
    },
    {
      path: '/contacts/:contact_id',
      name: 'contacts_details', component: ContactsDetailsView,
      meta: { authorize: [] }
    },
    {
      path: '/entreprises/:entreprise_id',
      name: 'entreprises_details', component: EntreprisesDetailsView,
      meta: { authorize: [] }
    },

    {
      path: '/opportunites',
      name: 'opportunites', component: OpportunitesListView,
      meta: { authorize: [] }
    },

    {
      path: '/opportunites/:opportunite_id',
      name: 'opportunites_details', component: OpportunitesDetailsView,
      meta: { authorize: [] }
    },

    {
      path: '/projets',
      name: 'projets', component: DashboardView,
      meta: { authorize: [] }
    },
    {
      path: '/suivitemps',
      name: 'suivitemps', component: DashboardView,
      meta: { authorize: [] }
    },


    // { 
    //   path: '/dashboard', 
    //   name: 'dashboard', component: DashboardView,
    //   meta: { authorize:[] }
    // },   
    // {
    //   path: '/abonnements', 
    //   name: 'abonnements', component: AbonnementsView,
    //   meta: { authorize: [] } 
    // },
    // {
    //   path: '/clients', 
    //   name: 'clients', component: ClientsView,
    //   meta: { authorize: [] } 
    // },
    // {
    //   path: '/coaching_rdv', 
    //   name: 'coaching_rdv', component: CoachingRdvView,
    //   meta: { authorize: [] } 
    // },
    // {
    //   path: '/configuration-commandes',
    //   name: 'configuration-commandes', component: ConfigurationCommandesView,
    //   meta: { authorize: ["ADMIN"] } 
    // },
    // {
    //   path: '/configuration-comptabilite',
    //   name: 'configuration-comptabilite', component: ConfigurationComptabiliteView,
    //   meta: { authorize: ["ADMIN"] } 
    // },
    // {
    //   path: '/configuration-factures',
    //   name: 'configuration-factures', component: ConfigurationFacturesView,
    //   meta: { authorize: ["ADMIN"] } 
    // },
    // {
    //   path: '/configuration-interventions',
    //   name: 'configuration-interventions', component: ConfigurationInterventionsView,
    //   meta: { authorize: ["ADMIN"] } 
    // },
    // {
    //   path: '/configuration-lieux',
    //   name: 'configuration-lieux', component: ConfigurationLieuxView,
    //   meta: { authorize: ["ADMIN"] } 
    // },
    // {
    //   path: '/configuration-opportunites',
    //   name: 'configuration-opportunites', component: ConfigurationOpportunitesView,
    //   meta: { authorize: ["ADMIN"] } 
    // },
    // {
    //   path: '/configuration-organisations',
    //   name: 'configuration-organisations', component: ConfigurationOrganisationsView,
    //   meta: { authorize: ["ADMIN"] } 
    // },
    // {
    //   path: '/configuration-planning',
    //   name: 'configuration-planning', component: ConfigurationPlanningView,
    //   meta: { authorize: ["ADMIN"] } 
    // },
    // {
    //   path: '/configuration-projets',
    //   name: 'configuration-projets', component: ConfigurationProjetsView,
    //   meta: { authorize: ["ADMIN"] } 
    // },
    // {
    //   path: '/configuration-rh',
    //   name: 'configuration-rh', component: ConfigurationRhView,
    //   meta: { authorize: ["ADMIN"] } 
    // },
    // {
    //   path: '/configuration-templates',
    //   name: 'configuration-templates', component: ConfigurationTemplatesView,
    //   meta: { authorize: ["ADMIN"] } 
    // },
    // {
    //   path: '/configuration-tickets',
    //   name: 'configuration-tickets', component: ConfigurationTicketsView,
    //   meta: { authorize: ["ADMIN"] } 
    // },
    // {
    //   path: '/dossiers',
    //   name: 'dossiers', component: DossiersView,
    //   meta: { authorize: [] } 
    // },
    // {
    //   path: '/dossiers/:id',
    //   name: 'dossier', component: DossiersView,
    //   meta: { authorize: [] } 
    // },
    // {
    //   path: "/mon-planning",
    //   name: "mon-planning", component: MonPlanningView,
    //   meta: { authorize: ["JURISTE"] } 
    // },
    // {
    //   path: "/mon-profil",
    //   name: "mon-profil", component: MonProfilView,
    //   meta: { authorize: [] } 
    // },
    // {
    //   path: '/prestations', 
    //   name: 'prestations', component: PrestationsView,
    //   meta: { authorize: [] } 
    // },
    // {
    //   path: '/prestation/:id',
    //   name: 'prestation', component: PrestationsView,
    //   meta: { authorize: [] } 
    // },
    // {
    //   path: '/ref_abonnement',
    //   name: 'ref_abonnement', component: RefAbonnementView,
    //   meta: { authorize: ["ADMIN_FONCTIONNEL", "ADMIN"] } 
    // },
    // {
    //   path: '/ref_formulaire',
    //   name: 'ref_formulaire', component: RefFormulaireView,
    //   meta: { authorize: ["ADMIN_FONCTIONNEL", "ADMIN"] } 
    // },
    // {
    //   path: '/ref_prestation',
    //   name: 'ref_prestation', component: RefPrestationView,
    //   meta: { authorize: ["ADMIN_FONCTIONNEL", "ADMIN"] } 
    // },
    // {
    //   path: '/utilisateurs',
    //   name: 'utilisateurs', component: UtilisateursView,
    //   meta: { authorize: ["ADMIN"] } 
    // }
  ]
});

export default router;

router.beforeEach((to, from, next) => {
  const { authorize } = to.meta;
  let result = false;
  const roles = sessionStorage.getItem("oidc-roles");

  if (authorize != null && authorize.length > 0) {
    authorize.forEach(role => {
      result |= roles.includes(role);
    });
  }
  else {
    result = true;
  }
  if (!result) {
    return next({ name: 'entreprises' });
  }
  next();
})