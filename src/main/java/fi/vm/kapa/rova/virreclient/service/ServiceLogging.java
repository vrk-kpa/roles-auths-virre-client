
package fi.vm.kapa.rova.virreclient.service;

import fi.vm.kapa.rova.logging.Logger;
import static fi.vm.kapa.rova.logging.Logger.Field.DURATION;
import static fi.vm.kapa.rova.logging.Logger.Field.ERRORSTR;
import static fi.vm.kapa.rova.logging.Logger.Field.WARNINGSTR;
import static fi.vm.kapa.rova.logging.Logger.Field.OPERATION;

public class ServiceLogging {

    private static final Logger log = Logger.getLogger(ServiceLogging.class);


    public static void logRequest(String op, long startTime, long currentTimeMillis) {
        Logger.LogMap logmap = log.infoMap();
        logmap.set(OPERATION, op);
        logmap.set(DURATION, currentTimeMillis - startTime);
        logmap.log();
    }

    public static void logWarning(String op, String warningString) {
        Logger.LogMap logmap = log.warningMap();
        logmap.set(OPERATION, op);
        logmap.set(WARNINGSTR, warningString);
        logmap.log();
    }

    public static void logError(String op, String errorString) {
        Logger.LogMap logmap = log.errorMap();
        logmap.set(OPERATION, op);
        logmap.set(ERRORSTR, errorString);
        logmap.log();
    }

}
