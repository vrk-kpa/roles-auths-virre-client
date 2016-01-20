package fi.vm.kapa.rova.soap.prh;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.springframework.stereotype.Component;

import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.model.CompaniesResponseMessage;
import fi.vm.kapa.rova.soap.prh.model.ExtendedRoleInfo;
import fi.vm.kapa.rova.soap.prh.model.Role;
import fi.vrk.xml.rova.prh.activeroles.ObjectFactory;
import fi.vrk.xml.rova.prh.activeroles.PersonActiveRoleInfoType;
import fi.vrk.xml.rova.prh.activeroles.RoleInCompanyType;
import fi.vrk.xml.rova.prh.activeroles.RoleInCompanyType.RoleBasicInfo;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfo;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfoPortType;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfoPortTypeService;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfoResponse;

@Component
public class ActiveRolesClient extends AbstractPrhClient {
    private static Logger LOG = Logger.getLogger(ActiveRolesClient.class);
    
    XRoadPersonActiveRoleInfoPortTypeService service = new XRoadPersonActiveRoleInfoPortTypeService();
    ObjectFactory factory = new ObjectFactory();

    public CompaniesResponseMessage getResponse(String personId) throws JAXBException {
        XRoadPersonActiveRoleInfoPortType port = service.getXRoadPersonActiveRoleInfoPortTypePort();
        BindingProvider bp = (BindingProvider) port;
        
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xrdEndPoint);
        XRoadPersonActiveRoleInfo request = factory.createXRoadPersonActiveRoleInfo();
        PersonActiveRoleInfoType value = factory.createPersonActiveRoleInfoType();
        value.setSocialSecurityNumber(personId);
        request.setRequest(value);

        Holder<XRoadPersonActiveRoleInfoResponse> response = new Holder<XRoadPersonActiveRoleInfoResponse>();
        response.value = factory.createXRoadPersonActiveRoleInfoResponse();

        CompaniesResponseMessage result = new CompaniesResponseMessage();
        try {
            port.xRoadPersonActiveRoleInfo(request, getClientHeader(factory), getServiceHeader(factory), getUserIdHeader(), getIdHeader(), getProtocolVersionHeader(), response);
            LOG.info("soap succeeded");

            result.setSocialSec(response.value.getResponse().getSocialSecurityNumber());
            result.setStatus(response.value.getResponse().getStatus());


            List<Role> roles = new ArrayList<>(); // to be set into responseMsg

            for (RoleInCompanyType roleInCompany : response.value.getResponse().getRoleInCompany()) {

                Role role = new Role();
                role.setBusinessId(roleInCompany.getBusinessId());
                role.setCompanyName(roleInCompany.getCompanyName());

                List<ExtendedRoleInfo> extendedRoleInfos = new ArrayList<>();
                
                for (RoleBasicInfo roleInfo : roleInCompany.getRoleBasicInfo()) {
                    ExtendedRoleInfo extendedRoleInfo = new ExtendedRoleInfo();
                    extendedRoleInfo.setRoleType(roleInfo.getName());
                    extendedRoleInfo.setBodyType(roleInfo.getBodyType());
                    extendedRoleInfo.setStartDate(roleInfo.getStartDate().toString());

                    extendedRoleInfos.add(extendedRoleInfo);
                    
                }
                role.setExtendedRoleInfos(extendedRoleInfos);

                roles.add(role);
            }
            
            result.setRoles(roles);
        } catch (Exception e) {
            LOG.warning("exception: "+ e.getMessage());
            e.printStackTrace();
        }
        
        return result;
   }
}
 