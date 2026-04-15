package org.jakartaee.itsm.MODEL;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "metrics")
public class Metrics implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date periode;

    @Column(name = "taux_respect_sla", precision = 5, scale = 2)
    private BigDecimal tauxRespectSla;

    @Column(name = "temps_moyen_resolution", precision = 10, scale = 2)
    private BigDecimal tempsMoyenResolution;

    @Column(name = "taux_satisfaction", precision = 5, scale = 2)
    private BigDecimal tauxSatisfaction;

    // Constructeurs
    public Metrics() {
    }

    public Metrics(Date periode, BigDecimal tauxRespectSla, BigDecimal tempsMoyenResolution, BigDecimal tauxSatisfaction) {
        this.periode = periode;
        this.tauxRespectSla = tauxRespectSla;
        this.tempsMoyenResolution = tempsMoyenResolution;
        this.tauxSatisfaction = tauxSatisfaction;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getPeriode() {
        return periode;
    }

    public void setPeriode(Date periode) {
        this.periode = periode;
    }

    public BigDecimal getTauxRespectSla() {
        return tauxRespectSla;
    }

    public void setTauxRespectSla(BigDecimal tauxRespectSla) {
        this.tauxRespectSla = tauxRespectSla;
    }

    public BigDecimal getTempsMoyenResolution() {
        return tempsMoyenResolution;
    }

    public void setTempsMoyenResolution(BigDecimal tempsMoyenResolution) {
        this.tempsMoyenResolution = tempsMoyenResolution;
    }

    public BigDecimal getTauxSatisfaction() {
        return tauxSatisfaction;
    }

    public void setTauxSatisfaction(BigDecimal tauxSatisfaction) {
        this.tauxSatisfaction = tauxSatisfaction;
    }
}
