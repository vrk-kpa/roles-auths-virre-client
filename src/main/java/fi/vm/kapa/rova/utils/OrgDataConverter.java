
package fi.vm.kapa.rova.utils;

import fi.vm.kapa.rova.engine.model.BodyType;
import fi.vm.kapa.rova.engine.model.Organization;
import fi.vm.kapa.rova.engine.model.OrganizationType;
import fi.vm.kapa.rova.engine.model.RoleNameType;
import fi.vm.kapa.rova.engine.model.RoleType;
import fi.vm.kapa.rova.engine.model.SigningCodeType;
import fi.vm.kapa.rova.virre.model.OrganizationalPerson;
import fi.vm.kapa.rova.virre.model.OrganizationalRepresentation;
import fi.vm.kapa.rova.virre.model.OrganizationalRole;
import fi.vm.kapa.rova.virre.model.OrganizationalRoleInfo;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * VIRRE to API model data converter
 */
public class OrgDataConverter {

    public static Logger LOG = Logger.getLogger(OrgDataConverter.class.toString());

    public static List<fi.vm.kapa.rova.engine.model.OrganizationalRole> convertVirreToApi(OrganizationalPerson orgPerson) {

        String hetu = orgPerson.getPersonId();
        List<fi.vm.kapa.rova.engine.model.OrganizationalRole> roles = new LinkedList<>(); 

        for (OrganizationalRole origRole : orgPerson.getOrgRoles()) {
            for (SigningCodeType code : createSigningCodes(origRole)) {
                try {
                    fi.vm.kapa.rova.engine.model.OrganizationalRole newRole = new fi.vm.kapa.rova.engine.model.OrganizationalRole();
                    newRole.setPersonIdentifier(hetu);
                    newRole.setOrganization(createOrganization(origRole, code));
                    newRole.setRoles(new LinkedList<>(createRoleTypes(origRole)));
                    roles.add(newRole);
                } catch (IllegalArgumentException | NullPointerException e) {
                    LOG.warning("Unable to create OrganizationalRole: " + e.getMessage());
                }
            }
        }
        return roles;
    }

    private static Set<SigningCodeType> createSigningCodes(OrganizationalRole role) {
        Set<SigningCodeType> codes = new HashSet<SigningCodeType>();
        for (OrganizationalRepresentation repr : role.getRepresentations()) {
            String codeString = repr.getSigningcode();
            try  {
                SigningCodeType code = SigningCodeType.valueOf(codeString);
                codes.add(code);
            } catch (IllegalArgumentException | NullPointerException e) {
                LOG.warning("Unable to create SigningCodeType: " + codeString);
            }
        }
        return codes;
    }

    private static Organization createOrganization(OrganizationalRole role, SigningCodeType code) {
        Organization org = new Organization();
        org.setName(role.getOrganizationName());
        org.setExceptionStatus(role.getExceptionStatus());
        org.setType(OrganizationType.find(role.getCompanyFormCode()));
        org.setSigningCode(code);
        return org;
    }

    private static Set<RoleType> createRoleTypes(OrganizationalRole role) {
        Set<RoleType> roleTypes = new HashSet<>();
        for (OrganizationalRoleInfo info : role.getRoleInfos()) {
            try {
                RoleType roleType = new RoleType();
                roleType.setRoleName(RoleNameType.valueOf(info.getRoleName()));
                roleType.setBodyType(BodyType.valueOf(info.getBodyType()));
                roleType.setStartDate(info.getStartDate());
                roleType.setExpirationDate(info.getExpirationDate());
                roleTypes.add(roleType);
            } catch (IllegalArgumentException | NullPointerException e) {
                LOG.warning("Unable to create RoleType: " + e.getMessage());
            }
        }
        return roleTypes;
    }

}
