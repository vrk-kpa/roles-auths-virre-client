
package fi.vm.kapa.rova.virreclient.service;

import fi.vm.kapa.rova.engine.model.prh.Company;
import fi.vm.kapa.rova.engine.model.prh.CompanyRole;
import fi.vm.kapa.rova.engine.model.RoleNameType;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.CompaniesClient;
import fi.vm.kapa.rova.soap.prh.model.CompaniesResponseMessage;
import fi.vm.kapa.rova.soap.prh.model.Role;
import fi.vm.kapa.rova.soap.prh.model.ExtendedRoleInfo;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for loading Companies from PRH
 * @author mtom
 */
@Service
public class CompaniesService extends ServiceLogging {

    public static final String OP = "CompaniesService";

    private static final Logger log = Logger.getLogger(CompaniesService.class);

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");

    @Autowired
    private CompaniesClient cc;

    /**
     * Fetches company associations for given hetu from PRH's registry
     * @param hetu Social security number
     * @return List of organizations for given hetu
     * @throws VIRREServiceException 
     */
    public List<Company> getCompanies(String hetu) throws VIRREServiceException {

        List<Company> companies = null;

        try {
            long startTime = System.currentTimeMillis();
            String responseString = cc.getResponse(hetu);
            logRequest(OP + ":RoVaListCompanies", startTime, System.currentTimeMillis());
            CompaniesResponseMessage msg = MessageParser.parseResponseMessage(responseString, CompaniesResponseMessage.class);
            companies = parseCompanies(msg);
        } catch (Exception e) {
            logError(OP, "Failed to parse companies: " + e.getMessage());
            throw new VIRREServiceException(e.getMessage(), e);
        }

        return companies;
    }

    private List<Company> parseCompanies(CompaniesResponseMessage msg) {
        List<Company> companies = new LinkedList<>();
        if (msg != null) {
            for (Role role : msg.getRoles()) {
                Company company = new Company();
                company.setBusinessId(role.getBusinessId());
                company.setCompanyName(role.getCompanyName());
                List<CompanyRole> roles = new LinkedList<>();
                for (ExtendedRoleInfo eri : role.getExtendedRoleInfos()) {
                    CompanyRole cr = new CompanyRole();
                    RoleNameType type = null;
                    String rt = eri.getRoleType();
                    try {
                        type = RoleNameType.valueOf(rt);
                    } catch (IllegalArgumentException e) {
                        logWarning(OP, "Unable to parse role: " + rt);
                    }
                    cr.setType(type);
                    roles.add(cr);
                }
                company.setRoles(roles);
                companies.add(company);
            }
        } else {
            logWarning(OP, "Message was null.");
        }
        return companies;
    }

}
