/**
 * The MIT License
 * Copyright (c) 2016 Population Register Centre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
