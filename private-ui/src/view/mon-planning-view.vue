<template>
  <v-col col="12" sm="12" md="12">
    <v-card class="card-view">
      <v-card-title>        
        <span class="headline">{{$t("pmeflow.ui.mon-planning.title")}}</span>
        <v-spacer></v-spacer>
        <v-toolbar flat floating max-width="fit-content">
          <v-btn outlined class="mr-4" color="grey darken-2" @click="setToday">{{$t("pmeflow.ui.mon-planning.label.today")}}</v-btn>
          <v-btn fab text small color="grey darken-2" @click="prev" >
            <v-icon small>mdi-chevron-left</v-icon>
          </v-btn>
          <v-btn fab text small color="grey darken-2" @click="next">
            <v-icon small>mdi-chevron-right</v-icon>
          </v-btn>
          <v-toolbar-title v-if="$refs.calendar">{{ $refs.calendar.title }}</v-toolbar-title>
        </v-toolbar>
      </v-card-title>
      <v-card-text>  
        <v-row>
          <v-col cols="12">
            <v-sheet height="65vh">
            <v-calendar ref="calendar" v-model="focus" :first-interval="horaireMin"  :interval-count="amplitude"
              color="primary" type="week" weekdays="1,2,3,4,5,6,0" locale="fr"
              :events="events" :event-color="getEventColor" :event-ripple="false"
              @change="getEvents"
              @click:event="showEvent" @click:more="viewDay" @click:date="viewDay"
              @mousedown:event="startDrag" @mousedown:time="startTime"
              @mousemove:time="mouseMove" @mouseup:time="endDrag"
              @mouseleave.native="cancelDrag">
              <template v-slot:event="{ event, timed, eventSummary }">
                <div class="v-event-draggable" v-html="eventSummary()"></div>
                <div v-if="timed" class="v-event-drag-bottom" @mousedown.stop="extendBottom(event)"></div>
              </template>
              <template v-slot:day-body="{ date, week }">
                <div class="v-current-time" :class="{ first: date === week[0].date }" :style="{ top: nowY }"/>
            </template>
            </v-calendar>
            </v-sheet>
          </v-col>
        </v-row>                 
      </v-card-text>
    </v-card>
    <v-menu v-model="selectedOpen" :close-on-content-click="false" :activator="selectedElement" offset-x>
      <v-card color="grey lighten-4" min-width="350px" v-if="selectedEvent!=null && selectedEditable==false && selectedEvent.type!='rendez-vous'" class="event-detail">
        <v-toolbar :color="selectedEvent.color" dark flat>
          <v-btn icon @click="selectedEditable=true"><v-icon>mdi-pencil</v-icon></v-btn>
          <v-toolbar-title>{{selectedEvent.name}}</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="removeEvent(selectedEvent);closeSelected()"><v-icon>mdi-delete</v-icon></v-btn>
        </v-toolbar>
        <v-card-text>
          <v-row>
            <v-col cols="3"><span>{{$t("pmeflow.ui.mon-planning.label.debut")}}</span></v-col>
            <v-col cols="9">{{toDate(selectedEvent.start)}}</v-col>
          </v-row>
          <v-row>
            <v-col cols="3"><span>{{$t("pmeflow.ui.mon-planning.label.fin")}}</span></v-col>
            <v-col cols="9">{{toDate(selectedEvent.end)}}</v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <span>{{$t("pmeflow.ui.mon-planning.label.description")}}</span>
              <v-textarea rows="2" :value="selectedEvent.description" readonly></v-textarea>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="3"><span>{{$t("pmeflow.ui.mon-planning.label.type")}}</span></v-col>
            <v-col cols="9">{{selectedEvent.type}}</v-col>
          </v-row>
          <v-row>
            <v-col cols="3"><span>{{$t("pmeflow.ui.mon-planning.label.lieu")}}</span></v-col>
            <v-col cols="9">{{selectedEvent.lieu}}</v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>    
          <v-btn color="blue darken-1" rounded outlined @click="selectedOpen=false">{{$t('pmeflow.ui.generic.button.entry.fermer')}}</v-btn>
        </v-card-actions>
      </v-card>
      <v-card color="grey lighten-4" min-width="350px" v-if="selectedEvent!=null && selectedEvent.type!='rendez-vous' && selectedEditable==true" class="event-detail">
        <v-toolbar :color="selectedEvent.color" dark flat>
          <v-text-field v-model="selectedEvent.name"/>
          <v-spacer></v-spacer>
          <v-btn icon @click="removeEvent(selectedEvent);"><v-icon>mdi-delete</v-icon></v-btn>
        </v-toolbar>
        <v-card-text>
          <v-row>
            <v-col cols="12" md="6">
              <v-datetime-picker dense :label="$t('pmeflow.ui.mon-planning.label.debut')" v-model="selectedStart" locale="fr-FR"
                :text-field-props="textFieldProps" :date-picker-props="dateProps" :time-picker-props="timeProps"
                v-on:input="verifFinAfter">
                <template slot="dateIcon">
                  <v-icon>mdi-calendar-month</v-icon>
                </template>
                <template slot="timeIcon">
                  <v-icon>mdi-clock-outline</v-icon>
                </template>
              </v-datetime-picker>
            </v-col>
            <v-col cols="12" md="6">
              <v-datetime-picker dense :label="$t('pmeflow.ui.mon-planning.label.fin')" v-model="selectedEnd" locale="fr-FR"
                :text-field-props="textFieldProps" :date-picker-props="dateProps" :time-picker-props="timeProps"
                v-on:input="verifDebutBefore">
                <template slot="dateIcon">
                  <v-icon>mdi-calendar-month</v-icon>
                </template>
                <template slot="timeIcon">
                  <v-icon>mdi-clock-outline</v-icon>
                </template>
              </v-datetime-picker>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <v-textarea dense :label="$t('pmeflow.ui.mon-planning.label.description')" rows="2" v-model="selectedEvent.description"></v-textarea>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="6">
              <v-select dense :label="$t('pmeflow.ui.mon-planning.label.type')" :items="types"
                v-model="selectedEvent.type"/>
            </v-col>
            <v-col cols="12" md="6">
              <v-switch dense :label="$t('pmeflow.ui.mon-planning.label.gHoraire')" v-model="selectedEvent.timed" color="success" hide-details/>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <v-text-field dense :label="$t('pmeflow.ui.mon-planning.label.lieu')" v-model="selectedEvent.lieu"/>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>    
          <v-btn color="blue darken-1" rounded outlined @click="closeSelected()">{{$t('pmeflow.ui.generic.button.entry.fermer')}}</v-btn>
          <v-btn color="red darken-1" rounded outlined @click="saveEvent(selectedEvent);closeSelected()">{{$t('pmeflow.ui.generic.button.entry.save')}}</v-btn>
        </v-card-actions>
      </v-card>
      <v-card color="grey lighten-4" min-width="350px" v-if="selectedEvent!=null && selectedEvent.type=='rendez-vous' && selectedEditable==true" class="event-detail">
      </v-card>
      <rendezVousCard v-model="rendezVous" v-on:remove="removeSelected()" v-if="selectedEvent!=null && selectedEvent.type=='rendez-vous'"/>
    </v-menu>
  </v-col>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
import EvenementService from "@/api/EvenementService.js";
import ProfilService from "@/api/ProfilService.js";
import rendezVousCard from '@/components/ui/rdv/rendez-vous-card.vue';
import RendezVousService from "@/api/RendezVousService.js"

export default {
  components: {
    rendezVousCard
  },    
  created() {
    eventBus.$emit("errors-loaded", []);
  },
  asyncComputed: {
    profil() {
      return this.getProfilService().getProfil();
    },
  },
  computed: {
    cal () {
      return this.ready ? this.$refs.calendar : null
    },
    nowY () {
      return this.cal ? this.cal.timeToY(this.cal.times.now) + 'px' : '-10px'
    },
    horaireMin(){
      let horaireMin = 8;      
      if (horaireMin>this.disponibilite.lundi.matin.ouverture){
        horaireMin=this.disponibilite.lundi.matin.ouverture;
      }
      if (horaireMin>this.disponibilite.mardi.matin.ouverture){
        horaireMin=this.disponibilite.mardi.matin.ouverture;
      }
      if (horaireMin>this.disponibilite.mercredi.matin.ouverture){
        horaireMin=this.disponibilite.mercredi.matin.ouverture;
      }
      if (horaireMin>this.disponibilite.jeudi.matin.ouverture){
        horaireMin=this.disponibilite.jeudi.matin.ouverture;
      }
      if (horaireMin>this.disponibilite.vendredi.matin.ouverture){
        horaireMin=this.disponibilite.vendredi.matin.ouverture;
      }
      if (horaireMin>this.disponibilite.samedi.matin.ouverture){
        horaireMin=this.disponibilite.samedi.matin.ouverture;
      }
      if (horaireMin>this.disponibilite.dimanche.matin.ouverture){
        horaireMin=this.disponibilite.dimanche.matin.ouverture;
      }
      return horaireMin;
    },    
    amplitude(){
      let horaireMax = 8;      
      if (horaireMax<this.disponibilite.lundi.apresmidi.fermeture){
        horaireMax=this.disponibilite.lundi.apresmidi.fermeture;
      }
      if (horaireMax<this.disponibilite.mardi.apresmidi.fermeture){
        horaireMax=this.disponibilite.mardi.apresmidi.fermeture;
      }
      if (horaireMax<this.disponibilite.mercredi.apresmidi.fermeture){
        horaireMax=this.disponibilite.mercredi.apresmidi.fermeture;
      }
      if (horaireMax<this.disponibilite.jeudi.apresmidi.fermeture){
        horaireMax=this.disponibilite.jeudi.apresmidi.fermeture;
      }
      if (horaireMax<this.disponibilite.vendredi.apresmidi.fermeture){
        horaireMax=this.disponibilite.vendredi.apresmidi.fermeture;
      }
      if (horaireMax<this.disponibilite.samedi.apresmidi.fermeture){
        horaireMax=this.disponibilite.samedi.apresmidi.fermeture;
      }
      if (horaireMax<this.disponibilite.dimanche.apresmidi.fermeture){
        horaireMax=this.disponibilite.dimanche.apresmidi.fermeture;
      }
      return horaireMax - this.horaireMin;
    },
    types(){
      let types = [];
      for (let index = 0; index < this.$store.state.configuration.parametres.eventTypes.length; index++) {
        const element = this.$store.state.configuration.parametres.eventTypes[index];
        types.push(element.type);
      }
      return types;
    },
    rendezVous(){
      return this.toBackEndRdv(this.selectedEvent);
    }
  },  
  data: () => ({
    textFieldProps: {
        appendIcon: 'mdi-calendar-clock'
    },
    dateProps: {
      locale: 'fr-FR'
    },
    timeProps: {
      locale: 'fr-FR',
      format:"24hr"
    },
    selectedStart:null,
    selectedEnd:null,
    days:["lundi","mardi","mercredi","jeudi","vendredi","samedi","dimanche"],
    disponibilite:{
      lundi:{
        matin:{
          ouverture:8,
          fermeture:12
        },
        apresmidi:{
          ouverture:13,
          fermeture:19
        }
      },        
      mardi:{
        matin:{
          ouverture:8,
          fermeture:12
        },
        apresmidi:{
          ouverture:13,
          fermeture:19
        }
      },        
      mercredi:{
        matin:{
          ouverture:8,
          fermeture:12
        },
        apresmidi:{
          ouverture:13,
          fermeture:19
        }
      },       
      jeudi:{
        matin:{
          ouverture:8,
          fermeture:12
        },
        apresmidi:{
          ouverture:13,
          fermeture:19
        }
      },        
      vendredi:{
        matin:{
          ouverture:8,
          fermeture:12
        },
        apresmidi:{
          ouverture:13,
          fermeture:19
        }
      },        
      samedi:{
        matin:{
          ouverture:8,
          fermeture:12
        },
        apresmidi:{
          ouverture:null,
          fermeture:null,
        }
      },
      dimanche:{
        matin:{
          ouverture:null,
          fermeture:null
        },
        apresmidi:{
          ouverture:null,
          fermeture:null,
        }
      }
    },
    focus:'',
    ready:false,
    selectedEvent: {},
    selectedElement: null,
    selectedOpen: false,
    selectedEditable: false,
    events: [],
    dragEvent: null,
    dragStart: null,
    createEvent: null,
    createStart: null,
    extendOriginal: null
  }),

  mounted () {
    this.$refs.calendar.checkChange();
    this.scrollToTime();
    this.updateTime();
    this.initialize();
    this.ready=true;
  },

  methods: {
    verifFinAfter(){
      if (this.selectedStart.getTime()>this.selectedEnd.getTime() )
      {
        this.selectedEnd=new Date(this.selectedStart.getTime()+(15*60*1000));
      }
    },
    verifDebutBefore(){
      if (this.selectedStart.getTime()>this.selectedEnd.getTime() )
      {
        this.selectedStart=new Date(this.selectedEnd.getTime()-(15*60*1000));
      }
    },
    getEvenementService() {
      return new EvenementService(
        this.$store.state.configuration.services.evenementServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    getProfilService() {
      return new ProfilService(
        this.$store.state.configuration.services.profilServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    getRendezVousService() {
      return new RendezVousService(
        this.$store.state.configuration.services.rendezVousServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    initialize() {
      this.getDisponibilite();
    },
    getDisponibilite() {
      this.getProfilService().getDisponibilite().then(disponibilite => {
        this.disponibilite = disponibilite;
        let style = document.querySelector('[title="disponibilite"]');
        if (style!=null){
          style.parentNode.removeChild(style);
        }
        style = document.createElement('style');
        style.title="disponibilite";
        style.innerHTML = this.getCompiledCss();
        document.head.appendChild(style);
      });
    },
    toDate(millis){
      return new Date(millis).toLocaleString("fr-FR").substr(0, 17).replace("T"," ");
    },
    toBackEndEvent(event){
      let backend = {
        oid:event.oid,
        libelle:event.name,
        dateDebut:event.start,
        dateFin:event.end,
        utilisateur:event.utilisateur,
        type:event.type,
        description:event.description,
        lieu:event.lieu,
        jourEntier:!event.timed,
      }
      return backend;
    },
    toBackEndRdv(event){
      let backend = {
        oid:event.oid,
        motif:event.name,
        description:event.description,        
        dateDebut:event.start,
        dateFin:event.end,
        dossierId:event.dossierId,
        client:event.client,
        juriste:event.juriste,
        type:"rendez-vous",
        etat:event.etat
      }
      return backend;
    },
    toInternalEventFromRdv(backEvent){
      let internal = {
        oid:backEvent.oid,
        name:backEvent.motif,
        description:backEvent.description,
        start:backEvent.dateDebut,
        end:backEvent.dateFin,
        type:"rendez-vous",
        timed:true,
        color:'#982d43',
        client:backEvent.client,
        juriste:backEvent.juriste,
        dossierId:backEvent.dossierId,
        etat:backEvent.etat
      }
      return internal;
    },
    toInternalEventFromEvent(backEvent){
      let internal = {
        oid:backEvent.oid,
        name:backEvent.libelle,
        start:backEvent.dateDebut,
        end:backEvent.dateFin,
        description:backEvent.description,
        type:backEvent.type,
        lieu:backEvent.lieu,
        timed:!backEvent.jourEntier,
        color:this.getColorForType(backEvent.type),
        utilisateur:backEvent.utilisateur
      }
      return internal;
    },
    getColorForType(type){
      let color = "blue";
      let eventTypes = this.$store.state.configuration.parametres.eventTypes;
      for (let index = 0; index < eventTypes.length; index++) {
        const eventType = eventTypes[index];
        if (eventType.type===type)
        {
          color=eventType.color;
        }
      }
      return color;
    },
    getEvents () {
      this.events=[];
      this.getEvenementService().getEvenements().then(response => {
        let backEvents = response.data;
        for (let index = 0; index < backEvents.length; index++) {
          this.events.push(this.toInternalEventFromEvent(backEvents[index]))        
        }
        eventBus.$emit("errors-loaded", []);
      });
      this.getRendezVousService().getRendezVousActifs().then(response => {
        let backRdvs = response.data;
        for (let index = 0; index < backRdvs.length; index++) {
          this.events.push(this.toInternalEventFromRdv(backRdvs[index]))        
        }
        eventBus.$emit("errors-loaded", []);
      });
    },
    getCompiledCss(){
      let style = "";
      style += this.getStyleForDay(1, this.disponibilite.lundi);
      style += this.getStyleForDay(2, this.disponibilite.mardi);
      style += this.getStyleForDay(3, this.disponibilite.mercredi);
      style += this.getStyleForDay(4, this.disponibilite.jeudi);
      style += this.getStyleForDay(5, this.disponibilite.vendredi);
      style += this.getStyleForDay(6, this.disponibilite.samedi);
      style += this.getStyleForDay(7, this.disponibilite.dimanche);
      return style;
    },
    getStyleForDay(jour, dispo){
      let style = "";
      let nbre=13-this.horaireMin;
      let offset=0;

      //matin
      if (dispo.matin.ouverture!=null){
        nbre = dispo.matin.ouverture - this.horaireMin;
      }
      for (let index = 1; index <= nbre; index++) {
        style += `.v-calendar-daily__day-container div:nth-child(${jour+1}) div.v-calendar-daily__day-interval:nth-child(${offset+index}){background-color: lightgrey;} `;
      }

      //repas
      offset=12-this.horaireMin;
      nbre=this.amplitude-offset;
      if (dispo.matin.fermeture!=null && dispo.apresmidi.ouverture!=null){
        offset=dispo.matin.fermeture-this.horaireMin;
        nbre = dispo.apresmidi.ouverture - dispo.matin.fermeture;
      }
      if (dispo.matin.fermeture==null && dispo.apresmidi.ouverture!=null){
        offset=12-this.horaireMin;
        nbre = dispo.apresmidi.ouverture - dispo.matin.fermeture;
      }
      for (let index = 1; index <= nbre; index++) {
        style += `.v-calendar-daily__day-container div:nth-child(${jour+1}) div.v-calendar-daily__day-interval:nth-child(${offset+index}){background-color: lightgrey;} `;
      }

      //soir
      offset=17-this.horaireMin;
      nbre=this.amplitude - offset;
      if (dispo.apresmidi.fermeture!=null){
        offset=dispo.apresmidi.fermeture - this.horaireMin;
        nbre = this.amplitude - offset;
      }
      for (let index = 1; index <= nbre; index++) {
        style += `.v-calendar-daily__day-container div:nth-child(${jour+1}) div.v-calendar-daily__day-interval:nth-child(${offset+index}){background-color: lightgrey;} `;
      }
      return style;
    },
    viewDay ({ date }) {
      this.focus = date
      this.type = 'day'
    },
    setToday () {
      this.focus = ''
    },
    prev () {
      this.$refs.calendar.prev()
    },
    next () {
      this.$refs.calendar.next()
    },
    getCurrentTime () {
      return this.cal ? this.cal.times.now.hour * 60 + this.cal.times.now.minute : 0
    },
    scrollToTime () {
      const time = this.getCurrentTime();
      const first = Math.max(0, time - (time % 30) - 30);
      if (this.cal!=null){
        this.cal.scrollToTime(first)
      }
    },
    updateTime () {
      setInterval(() => this.cal.updateTimes(), 60 * 1000)
    },  
    startDrag ({ event, timed }) {
      if (event && timed) {
        this.dragEvent = event
        this.dragTime = null
        this.extendOriginal = null
      }
    },
    startTime (tms) {
      const mouse = this.toTime(tms)
      if (this.dragEvent && this.dragTime === null) {
        const start = this.dragEvent.start
        this.dragTime = mouse - start
      } 
      else {
        this.createStart = this.roundTime(mouse)
        this.createEnd = this.createStart + (this.$store.state.configuration.parametres.dureeRdv*60*1000),
        this.createEvent = {
          oid:'',
          name: `Event #${this.events.length}`,
          description:'',
          utilisateur: this.profil,
          type:"indefini",
          start: this.createStart,
          end: this.createEnd,
          color:'blue',
          timed: true,
        }
        this.events.push(this.createEvent)
      }
    },
    extendBottom (event) {
      this.createEvent = event
      this.createStart = event.start
      this.extendOriginal = event.end
    },
    mouseMove (tms) {      
      const mouse = this.toTime(tms)
      if (this.dragEvent && this.dragTime !== null && this.dragEvent.type!="rendez-vous") {
        const start = this.dragEvent.start
        const end = this.dragEvent.end
        const duration = end - start
        const newStartTime = mouse - this.dragTime
        const newStart = this.roundTime(newStartTime)
        const newEnd = newStart + duration
        this.dragEvent.start = newStart
        this.dragEvent.end = newEnd
      } 
      else if (this.createEvent && this.createStart !== null) {
        const mouseRounded = this.roundTime(mouse, false)
        const min = Math.min(mouseRounded, this.createStart)
        const max = Math.max(mouseRounded, this.createStart)
        this.createEvent.start = min
        this.createEvent.end = max
      }
    },
    endDrag () {
      if (this.createEvent!=null){
        this.saveEvent(this.createEvent);
      }
      if (this.dragEvent!=null && this.dragEvent.type!='rendez-vous'){
        this.saveEvent(this.dragEvent);
      }
      this.selectedEvent=null;
      this.selectedOpen=false;
      this.dragTime = null
      this.dragEvent = null
      this.createEvent = null
      this.createStart = null
      this.extendOriginal = null
    },
    cancelDrag () {
      if (this.createEvent) {
        if (this.extendOriginal) {
          this.createEvent.end = this.extendOriginal
        } else {
          const i = this.events.indexOf(this.createEvent)
          if (i !== -1) {
            this.events.splice(i, 1)
          }
        }
      }
      this.createEvent = null
      this.createStart = null
      this.dragTime = null
      this.dragEvent = null
    },
    closeSelected(){
      this.selectedEvent = null
      this.selectedStart = null
      this.selectedStop = null
      this.selectedOpen = false ;
    },
    removeSelected(){
      let clearedEvents = [];
      for (let index = 0; index < this.events.length; index++) {
        const event = this.events[index];        
        if (event.oid!=this.selectedEvent.oid){
          clearedEvents.push(event);
        }
      }
      this.events=clearedEvents;
      this.closeSelected();
    },
    roundTime (time, down = true) {
      const roundTo = 15 // minutes
      const roundDownTime = roundTo * 60 * 1000
      return down
        ? time - time % roundDownTime
        : time + (roundDownTime - (time % roundDownTime))
    },
    toTime (tms) {
      return new Date(tms.year, tms.month - 1, tms.day, tms.hour, tms.minute).getTime()
    },
    getEventColor(event) {
      const rgb = parseInt(event.color.substring(1), 16)
      const r = (rgb >> 16) & 0xFF
      const g = (rgb >> 8) & 0xFF
      const b = (rgb >> 0) & 0xFF
      return event === this.dragEvent? `rgba(${r}, ${g}, ${b}, 0.7)` 
          : event === this.createEvent ? `rgba(${r}, ${g}, ${b}, 0.7)` 
          : event.color
    },
    showEvent ({ nativeEvent, event }) {
      if(event.type=='indefini')
      {
        this.selectedEditable=true;
      }
      else {
        this.selectedEditable=false;
      }
      const open = () => {
        this.selectedEvent = event;
        this.selectedStart = new Date(event.start);
        this.selectedEnd = new Date(event.end);
        this.selectedElement = nativeEvent.target
        requestAnimationFrame(() => requestAnimationFrame(() => this.selectedOpen = true))
      }
      if (this.selectedOpen) {
        this.selectedOpen = false
        requestAnimationFrame(() => requestAnimationFrame(() => open()))
      } 
      else {
        open()
      }
      nativeEvent.stopPropagation()
    }, 
    removeEvent(event){
      this.getEvenementService().removeEvenement(event.oid)
        .then(() => {
          this.getEvents(); 
        eventBus.$emit("errors-loaded", []);
        })
        .catch(e => {
          let errors=[]
          if (e.response!=null && e.response.data != null && e.response.data.conteneur != null)
          {
            let oEvents = Object.entries(e.response.data.conteneur.mapEvenements);
            for (let index = 0; index < oEvents.length; index++) {
              errors.push({
                "type" : oEvents[index][1].niveau.toLowerCase(),
                "msg" :  oEvents[index][1].libelle
              });
            }
          }
          else
          {
            errors.push({
              type:"error",
              msg:e.message
            });
          }
          eventBus.$emit("errors-loaded", errors);
        });
      this.selectedOpen=false;
    }, 
    saveEvent(event){
      if (this.selectedStart!=null && this.selectedEnd!=null && this.selectedEvent!=null){
        event.start=this.selectedStart.getTime();
        event.end=this.selectedEnd.getTime();
      }
      this.getEvenementService().mergeEvenement(this.toBackEndEvent(event))
        .then(() => {  
          console.log(event.name);
          this.getEvents(); 
          eventBus.$emit("errors-loaded", []);
        })
        .catch(e => {
          let errors=[];
          if (e.response!=null && e.response.data != null && e.response.data.conteneur != null)
          {
            let oEvents = Object.entries(e.response.data.conteneur.mapEvenements);
            for (let index = 0; index < oEvents.length; index++) {
              errors.push({
                "type" : oEvents[index][1].niveau.toLowerCase(),
                "msg" :  oEvents[index][1].libelle
              });
            }
          }
          else
          {
            errors.push({
              type:"error",
              msg:e.message
            });
          }
          eventBus.$emit("errors-loaded", errors);
        });
    },
  },
};
</script>
<style scoped>
  .event-detail span{
    font-weight: bold;
  }
  .v-event-draggable {
    padding-left: 6px;
  }

  .v-event-timed {
    user-select: none;
    -webkit-user-select: none;
  }

  .v-event-drag-bottom {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 4px;
    height: 4px;
    cursor: ns-resize;

    &::after {
      display: none;
      position: absolute;
      left: 50%;
      height: 4px;
      border-top: 1px solid white;
      border-bottom: 1px solid white;
      width: 16px;
      margin-left: -8px;
      opacity: 0.8;
      content: '';
    }

    &:hover::after {
      display: block;
    }
  }
  
  .v-current-time {
    height: 2px;
    background-color: #ea4335;
    position: absolute;
    left: -1px;
    right: 0;
    pointer-events: none;

    &.first::before {
      content: '';
      position: absolute;
      background-color: #ea4335;
      width: 12px;
      height: 12px;
      border-radius: 50%;
      margin-top: -5px;
      margin-left: -6.5px;
    }
  }
</style>