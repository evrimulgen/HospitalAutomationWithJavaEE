package com.ilkgunel.hastaneotomasyonu.controller;

import com.ilkgunel.hastaneotomasyonu.entity.Hastaneler;
import com.ilkgunel.hastaneotomasyonu.entity.Klinikler;
import com.ilkgunel.hastaneotomasyonu.entity.Randevusaatleri;
import com.ilkgunel.hastaneotomasyonu.entity.Uygunrandevular;
import java.io.Serializable;
import java.text.SimpleDateFormat;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
//Çalışan Sınıf Budur
@ManagedBean(name = "getAvaliableAppointments")
@ViewScoped
public class GetAvaliableAppointments implements Serializable{
    
    EntityManagerFactory emf= Persistence.createEntityManagerFactory("HospitalAutomation");
    EntityManager em=emf.createEntityManager();
        
    List<Uygunrandevular> availableAppointments;
    List<Object[]> doctorAndTimeList;
    List<Randevusaatleri> appointmentClockResults;
    boolean renderingTakingAppointmentInfo=true;
    boolean renderingClocks=false;
    boolean renderingDataTable=false;
    int hospitalid;
    @ManagedProperty(value = "#{saveAppointments}")
    private SaveAppointments saveAppointmentsObject;

    @ManagedProperty(value = "#{getHospitals}")
    private GetHospitals getHospitalsObject;
    
    @ManagedProperty(value = "#{getClinics}")
    private GetClinics getClinicsObject;

    public boolean isRenderingTakingAppointmentInfo() {
        return renderingTakingAppointmentInfo;
    }

    public void setRenderingTakingAppointmentInfo(boolean renderingTakingAppointmentInfo) {
        this.renderingTakingAppointmentInfo = renderingTakingAppointmentInfo;
    }

    public boolean isRenderingClocks() {
        return renderingClocks;
    }

    public void setRenderingClocks(boolean renderingClocks) {
        this.renderingClocks = renderingClocks;
    }

    public SaveAppointments getSaveAppointmentsObject() {
        return saveAppointmentsObject;
    }

    public void setSaveAppointmentsObject(SaveAppointments saveAppointmentsObject) {
        this.saveAppointmentsObject = saveAppointmentsObject;
    }

    public boolean isRenderingDataTable() {
        return renderingDataTable;
    }

    public void setRenderingDataTable(boolean renderingDataTable) {
        this.renderingDataTable = renderingDataTable;
    }

    public GetHospitals getGetHospitalsObject() {
        return getHospitalsObject;
    }

    public void setGetHospitalsObject(GetHospitals getHospitalsObject) {
        this.getHospitalsObject = getHospitalsObject;
    }

    public List<Uygunrandevular> getAvailableAppointments() {
        return availableAppointments;
    }

    public void setAvailableAppointments(List<Uygunrandevular> availableAppointments) {
        this.availableAppointments = availableAppointments;
    }

    public List<Object[]> getDoctorAndTimeList() {
        return doctorAndTimeList;
    }

    public void setDoctorAndTimeList(List<Object[]> doctorAndTimeList) {
        this.doctorAndTimeList = doctorAndTimeList;
    }

    public GetClinics getGetClinicsObject() {
        return getClinicsObject;
    }

    public void setGetClinicsObject(GetClinics getClinicsObject) {
        this.getClinicsObject = getClinicsObject;
    }

    public List<Randevusaatleri> getAppointmentClockResults() {
        return appointmentClockResults;
    }

    public void setAppointmentClockResults(List<Randevusaatleri> appointmentClockResults) {
        this.appointmentClockResults = appointmentClockResults;
    }
    
    public void fillList()
    {
        availableAppointments=new ArrayList<>();

        

        for (Hastaneler h:getHospitalsObject.getHospitalResults())
        {
            if(h.getHastaneadi().equals(saveAppointmentsObject.getHospital()))
            {
                hospitalid=h.getId();
                break;
            }
        }
        
        int clinicId=0;
        for(Klinikler k:getClinicsObject.clinicResults)
        {
            if(k.getKlinikadi().equals(saveAppointmentsObject.clinic))
            {
                clinicId=k.getId();
                break;
            }
        }
        
        TypedQuery<Uygunrandevular> query=em.createQuery("SELECT u FROM Uygunrandevular AS u WHERE u.hastaneid=:hospitalid AND u.klinikid=:clinicid AND u.klinikyeri=:clinicplace "
                + "AND u.tarih = (select min(uu.tarih) from Uygunrandevular uu where uu.doktorid = u.doktorid)",Uygunrandevular.class);
        
        
        query.setParameter("hospitalid",hospitalid);
        query.setParameter("clinicid", clinicId);
        query.setParameter("clinicplace", saveAppointmentsObject.clinicPlace);

        availableAppointments=query.getResultList();

        setRenderingDataTable(true);

    }

    public void changeRenderingStates()
    {
        
        
        
        TypedQuery<Object[]> doctorAndTimeQuery = em.createQuery("SELECT u.doktoradi,FUNCTION('DATE',u.tarih),u.uygunrandevuid FROM Uygunrandevular AS u WHERE u.doktorid=:doctorid ORDER BY u.tarih ASC",Object[].class);
        doctorAndTimeQuery.setParameter("doctorid", saveAppointmentsObject.selectedAppointment.getDoktorid());
        doctorAndTimeList=new ArrayList<>();
        doctorAndTimeList=doctorAndTimeQuery.getResultList();
        
        TypedQuery<Randevusaatleri> query=em.createQuery("SELECT c FROM Randevusaatleri c WHERE c.doktorid=:doctorid",Randevusaatleri.class);
        System.out.println("Seçilen Randevunun ID'si"+saveAppointmentsObject.selectedAppointment.getUygunrandevuid());
        query.setParameter("doctorid", saveAppointmentsObject.selectedAppointment.getDoktorid());
        appointmentClockResults=new ArrayList<>();
        appointmentClockResults=query.getResultList();
        
        setRenderingTakingAppointmentInfo(false);
        setRenderingClocks(true);
        
    }
}

