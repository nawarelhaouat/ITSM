package org.jakartaee.itsm.BEAN;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

import org.jakartaee.itsm.MODEL.SlaConfig;
import org.jakartaee.itsm.MODEL.Ticket.Priority;
import org.jakartaee.itsm.SERVICE.SlaService;

@Named("slaBean")
@ViewScoped
public class SlaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SlaService slaService;

    // SLA par priorité (utilisés dans le form)
    private SlaConfig slaCritique;
    private SlaConfig slaHaute;
    private SlaConfig slaMoyenne;
    private SlaConfig slaBasse;

    // liste pour le tableau
    private List<SlaConfig> allSlaConfigs;

    @PostConstruct
    public void init() {
        loadSlaConfigs();
    }

    // ============================
    // 🔁 Chargement des données
    // ============================
    public void loadSlaConfigs() {
        allSlaConfigs = slaService.getAllSlaConfigs();

        // affecter chaque SLA selon priorité
        for (SlaConfig s : allSlaConfigs) {
            switch (s.getPriorite()) {
                case CRITIQUE:
                    slaCritique = s;
                    break;
                case HAUTE:
                    slaHaute = s;
                    break;
                case MOYENNE:
                    slaMoyenne = s;
                    break;
                case BASSE:
                    slaBasse = s;
                    break;
            }
        }

        // sécurité (si base vide)
        if (slaCritique == null) slaCritique = new SlaConfig(Priority.CRITIQUE);
        if (slaHaute == null) slaHaute = new SlaConfig(Priority.HAUTE);
        if (slaMoyenne == null) slaMoyenne = new SlaConfig(Priority.MOYENNE);
        if (slaBasse == null) slaBasse = new SlaConfig(Priority.BASSE);
    }

    // ============================
    // 💾 SAVE
    // ============================
    public void save(SlaConfig sla) {
        try {
            slaService.saveOrUpdate(sla);
            loadSlaConfigs(); // refresh

            System.out.println("[SLA] Sauvegarde réussie pour : " + sla.getPriorite());

        } catch (Exception e) {
            System.err.println("[SLA] Erreur save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================
    // GETTERS
    // ============================

    public SlaConfig getSlaCritique() {
        return slaCritique;
    }

    public SlaConfig getSlaHaute() {
        return slaHaute;
    }

    public SlaConfig getSlaMoyenne() {
        return slaMoyenne;
    }

    public SlaConfig getSlaBasse() {
        return slaBasse;
    }

    public List<SlaConfig> getAllSlaConfigs() {
        return allSlaConfigs;
    }
}