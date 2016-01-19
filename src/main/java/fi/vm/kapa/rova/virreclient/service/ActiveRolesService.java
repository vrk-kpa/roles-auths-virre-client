package fi.vm.kapa.rova.virreclient.service;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.vm.kapa.rova.external.model.virre.Company;
import fi.vm.kapa.rova.external.model.virre.CompanyRoleType;
import fi.vm.kapa.rova.external.model.virre.RoleNameType;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.ActiveRolesClient;
import fi.vm.kapa.rova.soap.prh.model.CompaniesResponseMessage;
import fi.vm.kapa.rova.soap.prh.model.ExtendedRoleInfo;
import fi.vm.kapa.rova.soap.prh.model.Role;
import fi.vm.kapa.rova.soap.virre.model.VirreResponseMsg;

/**
 * Created by Juha Korkalainen on 15.1.2016.
 */
@Service

public class ActiveRolesService extends ServiceLogging {
    public static final String OP = "ActiveRolesService";

    private static final Logger LOG = Logger.getLogger(CompaniesService.class);

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");

    @Autowired
    private ActiveRolesClient client;
    
    public List<Company> getCompanies(String hetu) throws VIRREServiceException {
        List<Company> companies = null;

        try {
            long startTime = System.currentTimeMillis();
//            String responseString = client.getResponse(hetu);
            VirreResponseMsg response = client.getResponse(hetu);
            LOG.warning(response.toString());
            logRequest(OP + ":RoVaListCompanies", startTime, System.currentTimeMillis());
            CompaniesResponseMessage msg = null; //MessageParser.parseResponseMessage(responseString, CompaniesResponseMessage.class);
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
                List<CompanyRoleType> roles = new LinkedList<>();
                for (ExtendedRoleInfo eri : role.getExtendedRoleInfos()) {
                    CompanyRoleType cr = new CompanyRoleType();
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
