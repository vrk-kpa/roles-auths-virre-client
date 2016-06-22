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

import fi.vm.kapa.rova.external.model.virre.PhaseNameType;
import https.ws_prh_fi.novus.ids.services._2008._08._22.PhaseType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

abstract public class AbstractCompanyService extends ServiceLogging {

    protected List<PhaseNameType> parseActiveCompanyPhases(List<PhaseType> phases) {
        List<PhaseNameType> activePhases = new LinkedList<>();
        if (phases == null || phases.isEmpty()) {
            return activePhases;
        }

        ZonedDateTime now = ZonedDateTime.now();
        for (PhaseType phaseType : phases) {
            if (phaseType != null) {
                String phaseName = phaseType.getName();
                PhaseNameType phase = PhaseNameType.parseType(phaseName);
                if (phase != PhaseNameType.NONE && isPhaseActive(phaseType, now)) {
                    activePhases.add(phase);
                }
            }
        }

        return activePhases;
    }

    protected boolean isPhaseActive(PhaseType phaseType, ZonedDateTime now) {

        boolean active = false;

        XMLGregorianCalendar startCal = phaseType.getRegistrationDate();
        XMLGregorianCalendar endCal = phaseType.getExpirationDate();

        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.MIN, zoneId);
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.MAX, zoneId);

        if (startCal != null) {
            start = startCal.toGregorianCalendar().toZonedDateTime();
        }
        if (endCal != null) {
            end = endCal.toGregorianCalendar().toZonedDateTime();
        }

        if ((now.isEqual(start) || now.isAfter(start)) && now.isBefore(end)) {
            active = true;
        }

        return active;
    }

}
