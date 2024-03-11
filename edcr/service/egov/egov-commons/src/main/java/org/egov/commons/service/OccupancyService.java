/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.commons.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.entity.bpa.Occupancy;
import org.egov.common.entity.bpa.SubOccupancy;
import org.egov.common.entity.bpa.Usage;
import org.egov.commons.repository.bpa.OccupancyRepository;
import org.egov.commons.repository.bpa.SubOccupancyRepository;
import org.egov.commons.repository.bpa.UsagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OccupancyService {

    @Autowired
    private OccupancyRepository occupancyRepository;
    @Autowired
    private SubOccupancyRepository subOccupancyRepository;

    @Autowired
    private UsagesRepository usagesRepository;

    public Occupancy findById(final Long id) {
        return occupancyRepository.findOne(id);
    }

    public List<Occupancy> findAll() {
        return occupancyRepository.findAll();
    }

    public List<Occupancy> findAllByActive() {
        return occupancyRepository.findByIsactiveTrueOrderByOrderNumberAsc();
    }

    public List<Occupancy> findAllOrderByOrderNumber() {
        return occupancyRepository.findAll(new Sort(Sort.Direction.ASC, "orderNumber"));
    }

    public List<Usage> findSubUsagesByOccupancy(final String occupancyName) {
        Occupancy occupancy = occupancyRepository.findByName(occupancyName);
        List<SubOccupancy> list = null;
        List<Usage> usagesList = new ArrayList<>();
        if (occupancy != null) {
            list = subOccupancyRepository.findByOccupancyAndIsActiveTrueOrderByOrderNumberAsc(occupancy);
            for (SubOccupancy subOccupancy : list) {
                List<Usage> usages = usagesRepository
                        .findBySubOccupancyAndIsActiveTrueOrderByOrderNumberAsc(subOccupancy);
                usagesList.addAll(usages);
            }
        } else {
            SubOccupancy subOccupancy;
            if (list == null) {
                subOccupancy = subOccupancyRepository.findByName(occupancyName);
                List<Usage> usages = usagesRepository
                        .findBySubOccupancyAndIsActiveTrueOrderByOrderNumberAsc(subOccupancy);
                usagesList.addAll(usages);
            }
        }

        return usagesList;
    }
}
