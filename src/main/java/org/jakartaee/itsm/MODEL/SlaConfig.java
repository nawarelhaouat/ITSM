package org.jakartaee.itsm.MODEL;

import jakarta.persistence.*;
import java.io.Serializable;
import org.jakartaee.itsm.MODEL.Ticket.Priority;
@Entity
@Table(name = "sla_config")
public class SlaConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Priority priorite;

    @Column(name = "delai_heures", nullable = false)
    private Integer delaiHeures;

    @Column(name = "seuil_alerte_pct", nullable = false)
    private Integer seuilAlertePct = 80;

    @Column(name = "delai_escalade_heures", nullable = false)
    private Integer delaiEscaladeHeures;

    // Constructeurs
    public SlaConfig() {
    }

    public SlaConfig(Priority priorite, Integer delaiHeures, Integer seuilAlertePct, Integer delaiEscaladeHeures) {
        this.priorite = priorite;
        this.delaiHeures = delaiHeures;
        this.seuilAlertePct = seuilAlertePct;
        this.delaiEscaladeHeures = delaiEscaladeHeures;
    }
    public SlaConfig(Priority priorite) {
        this.priorite = priorite;
    }
    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Priority getPriorite() {
        return priorite;
    }

    public void setPriorite(Priority priorite) {
        this.priorite = priorite;
    }

    public Integer getDelaiHeures() {
        return delaiHeures;
    }

    public void setDelaiHeures(Integer delaiHeures) {
        this.delaiHeures = delaiHeures;
    }

    public Integer getSeuilAlertePct() {
        return seuilAlertePct;
    }

    public void setSeuilAlertePct(Integer seuilAlertePct) {
        this.seuilAlertePct = seuilAlertePct;
    }

    public Integer getDelaiEscaladeHeures() {
        return delaiEscaladeHeures;
    }

    public void setDelaiEscaladeHeures(Integer delaiEscaladeHeures) {
        this.delaiEscaladeHeures = delaiEscaladeHeures;
    }


}
