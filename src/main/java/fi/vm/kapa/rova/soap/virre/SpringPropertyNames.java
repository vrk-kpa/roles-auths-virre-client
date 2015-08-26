package fi.vm.kapa.rova.soap.virre;

public interface SpringPropertyNames {

    String VIRRE_USERNAME = "${virre_username}"; // virre-palvelun käyttäjätunnus
    String VIRRE_PASSWORD = "${virre_password}"; // virre-palvelun salasana
    String VIRRE_USERID = "${userId}"; // xroad-kutsun userId
    String VIRRE_ID = "${id}"; // xroad-kutsun id
    String VIRRE_ISSUE = "${virre_issue}"; // xroad-kutsun issue
    String VIRRE_SDSB_INSTANCE = "${service_sdsb_instance}"; // kutsuttavan xroad-instanssin id (eg. FI_DEV)
    String VIRRE_MEMBER_CLASS = "${service_member_class}"; // xroad-kohdeorganisaation tyyppi (eg. COM, ORG, GOV)
    String VIRRE_MEMBER_CODE = "${service_member_code}"; // xroad-kohdeorganisaation id (eg. y-tunnus)
    String VIRRE_SUBSYSTEM_CODE = "${service_subsystem_code}"; // xroad-kohdealijärjestelmän nimi (eg. DemoService)
    String VIRRE_SERVICE_CODE = "${service_service_code}"; // xroad-kohdepalvelun nimi (eg. getRandom)

    String SERVICE_OBJECT_TYPE = "${service_object_type}"; // xroad-kutsun palvelutyyppi 
    String CLIENT_OBJECT_TYPE = "${client_object_type}"; // xroad-kutsun objektityyppi	

    String ROVA_ENDPOINT = "${rova_endpoint}"; // xroad-kutsun endpoint (oman liityntäpalvelimen ip)
    String ROVA_SDSB_INSTANCE = "${client_sdsb_instance}"; // oman xroad-instanssin id
    String ROVA_MEMBER_CLASS = "${client_member_class}"; // oman xroad-organisaation tyyppi
    String ROVA_MEMBER_CODE = "${client_member_code}"; // oman xroad-organisaation id
    String ROVA_SUBSYSTEM_CODE = "${client_subsystem_code}"; // oman xroad-alijärjestelmän nimi

}
