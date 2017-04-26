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
package fi.vm.kapa.rova.virre.resources;

import fi.vm.kapa.rova.external.model.virre.CompanyPerson;
import fi.vm.kapa.rova.external.model.virre.CompanyRepresentations;
import fi.vm.kapa.rova.external.model.virre.RepresentationRight;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.rest.exception.WebApplicationException;
import fi.vm.kapa.rova.soap.prh.VirreException;
import fi.vm.kapa.rova.virre.Virre;
import fi.vm.kapa.rova.virreclient.service.ActiveRolesService;
import fi.vm.kapa.rova.virreclient.service.CompanyReprService;
import fi.vm.kapa.rova.virreclient.service.RightReprService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

@RestController
public class PrhResource implements Virre {

    private static final Logger log = Logger.getLogger(PrhResource.class);

    @Autowired
    private ActiveRolesService arc;

    @Autowired
    private CompanyReprService crs;

    @Autowired
    private RightReprService rrs;

    /* (non-Javadoc)
     * @see fi.vm.kapa.rova.virre.resources.PRH#getCompanyPerson(java.lang.String)
     */
    @Override
    @GetMapping(
            value = GET_COMPANY_PERSON_PATH,
            produces = MediaType.APPLICATION_JSON
    )
    public CompanyPerson getCompanyPerson(@PathVariable("socialsec") String socialsec) throws WebApplicationException {
        log.debug("CompanyPerson request received.");
        try {
            return arc.getCompanyPerson(socialsec)
                    .orElseThrow(() -> new WebApplicationException("Company person not found.", 204));
        } catch (VirreException e) {
            throw new WebApplicationException(e);
        }
    }

    /* (non-Javadoc)
     * @see fi.vm.kapa.rova.virre.resources.PRH#getRepresentations(java.lang.String)
     */
    @Override
    @GetMapping(
            value = GET_REPRESENTATIONS_PATH,
            produces = MediaType.APPLICATION_JSON
    )
    public CompanyRepresentations getRepresentations(@PathVariable("businessid") String businessid)
            throws WebApplicationException {
        log.debug("Representations request received.");
        try {
            return crs.getRepresentations(businessid);
        } catch (VirreException e) {
            throw new WebApplicationException(e);
        }
    }

    /* (non-Javadoc)
     * @see fi.vm.kapa.rova.virre.resources.PRH#getRights(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @GetMapping(
            value = GET_RIGHTS_PATH,
            produces = MediaType.APPLICATION_JSON
    )
    public RepresentationRight getRights(@PathVariable("socialsec") String socialSec,
                                         @PathVariable("businessid") String businessId,
                                         @PathVariable("rightlevel") String rightLevel) {
        log.debug("Rights request received.");
        return rrs.getRights(socialSec, businessId, rightLevel);
    }

}
