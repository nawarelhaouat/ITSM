package org.jakartaee.itsm.SERVICE;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.jakartaee.itsm.DAO.SlaConfigDAO;
import org.jakartaee.itsm.MODEL.SlaConfig;
import org.jakartaee.itsm.MODEL.Ticket.Priority;
import org.jakartaee.itsm.MODEL.Ticket;

import java.util.List;

@Stateless
public class SlaService {
    @Inject
    private SlaConfigDAO slaConfigDAO;

    public void createSlaConfig(SlaConfig slaConfig) {
        slaConfigDAO.create(slaConfig);
    }

    public SlaConfig getSlaConfig(Long id) {
        return slaConfigDAO.findById(id);
    }

    public SlaConfig getSlaConfigByPriority(Priority priority) {
        return slaConfigDAO.findByPriority(priority);
    }

    public List<SlaConfig> getAllSlaConfigs() {
        return slaConfigDAO.findAll();
    }

    public void updateSlaConfig(SlaConfig slaConfig) {
        slaConfigDAO.update(slaConfig);
    }

    public void deleteSlaConfig(Long id) {
        slaConfigDAO.delete(id);
    }

    public Integer getDelaiForPriority(Ticket.Priority priority) {
        try {
            SlaConfig config = getSlaConfigByPriority(priority);
            return config != null ? config.getDelaiHeures() : null;
        } catch (Exception e) {
            return null;
        }
    }
    public void saveOrUpdate(SlaConfig sla) {
        if (sla.getId() == null) {
            slaConfigDAO.create(sla);
        } else {
            slaConfigDAO.update(sla);
        }
    }
}
