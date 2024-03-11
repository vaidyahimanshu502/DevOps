/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
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
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
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
 *
 */

package org.egov.infra.web.filter;

import static org.egov.infra.security.utils.SecurityUtils.getCurrentAuthentication;
import static org.egov.infra.utils.ApplicationConstant.APP_RELEASE_ATTRIB_NAME;
import static org.egov.infra.utils.ApplicationConstant.CDN_ATTRIB_NAME;
import static org.egov.infra.utils.ApplicationConstant.CITY_CODE_KEY;
import static org.egov.infra.utils.ApplicationConstant.CITY_CORP_NAME_KEY;
import static org.egov.infra.utils.ApplicationConstant.CITY_LOCAL_NAME_KEY;
import static org.egov.infra.utils.ApplicationConstant.CITY_NAME_KEY;
import static org.egov.infra.utils.ApplicationConstant.TENANTID_KEY;
import static org.egov.infra.utils.ApplicationConstant.USERID_KEY;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.egov.infra.admin.master.entity.City;
import org.egov.infra.admin.master.entity.CityPreferences;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.config.security.authentication.userdetail.CurrentUser;
import org.egov.infra.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

public class ApplicationCoreFilter implements Filter {

    @Autowired
    private CityService cityService;

    @Autowired
    private SecurityUtils securityUtils;

    @Value("${cdn.domain.url}")
    private String cdnURL;

    @Resource(name = "cities")
    private transient List<String> cities;

    @Value("${client.id}")
    private String clientId;

    @Value("${app.version}_${app.build.no}")
    private String applicationRelease;

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationCoreFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession();
        try {
            prepareUserSession(session);
            prepareApplicationThreadLocal(session);
            prepareRestService(request, session);
            chain.doFilter(request, resp);
        } finally {
            ApplicationThreadLocals.clearValues();
        }
    }

    private void prepareRestService(HttpServletRequest req, HttpSession session) {
        String requestURL = new StringBuilder().append(ApplicationThreadLocals.getDomainURL())
                .append(req.getRequestURI()).toString();
        if (requestURL.contains(ApplicationTenantResolverFilter.tenants.get("state"))
                && (requestURL.contains("/rest/") || requestURL.contains("/oauth/"))) {
            prepareThreadLocal(ApplicationThreadLocals.getTenantID());

        }
    }

    private void prepareUserSession(HttpSession session) {
        // if (session.getAttribute(CITY_CODE_KEY) == null)
        cityService.cityDataAsMap().forEach(session::setAttribute);
        if (session.getAttribute(APP_RELEASE_ATTRIB_NAME) == null)
            session.setAttribute(APP_RELEASE_ATTRIB_NAME, applicationRelease);
        if (session.getAttribute(TENANTID_KEY) == null)
            session.setAttribute(TENANTID_KEY, ApplicationThreadLocals.getTenantID());
        if (session.getServletContext().getAttribute(CDN_ATTRIB_NAME) == null)
            session.getServletContext().setAttribute(CDN_ATTRIB_NAME, cdnURL);
        if (session.getAttribute(USERID_KEY) == null) {
            Optional<Authentication> authentication = getCurrentAuthentication();
            if (authentication.isPresent() && authentication.get().getPrincipal() instanceof CurrentUser) {
                session.setAttribute(USERID_KEY, ((CurrentUser) authentication.get().getPrincipal()).getUserId());
            } else if (!authentication.isPresent() || !(authentication.get().getPrincipal() instanceof User)) {
                session.setAttribute(USERID_KEY, securityUtils.getCurrentUser().getId());
            }
        }
    }

    private void prepareApplicationThreadLocal(HttpSession session) {

        ApplicationThreadLocals.setCityCode((String) session.getAttribute(CITY_CODE_KEY));
        ApplicationThreadLocals.setCityName((String) session.getAttribute(CITY_NAME_KEY));
        ApplicationThreadLocals.setCityNameLocal((String) session.getAttribute(CITY_LOCAL_NAME_KEY));
        ApplicationThreadLocals.setMunicipalityName((String) session.getAttribute(CITY_CORP_NAME_KEY));
        ApplicationThreadLocals.setUserId((Long) session.getAttribute(USERID_KEY));
        if (session.getAttribute(CITY_CODE_KEY) != null) {
            City city = cityService.getCityByCode((String) session.getAttribute(CITY_CODE_KEY));
            if (city != null) {
                ApplicationThreadLocals.setDistrictCode(city.getDistrictCode());
                ApplicationThreadLocals.setDistrictName(city.getDistrictName());
                ApplicationThreadLocals.setStateName(clientId);
                ApplicationThreadLocals.setGrade(city.getGrade());
            }
        }

    }

    private void prepareThreadLocal(String tenant) {
        ApplicationThreadLocals.setTenantID(tenant);
        // ApplicationThreadLocals.setUserId(this.userService.getUserByUsername(this.userName).getId());

        // TODO: get the city by tenant
        City city = this.cityService.findAll().get(0);
        if (city != null) {
            ApplicationThreadLocals.setCityCode(city.getCode());
            ApplicationThreadLocals.setCityName(city.getName());
            ApplicationThreadLocals.setDistrictCode(city.getDistrictCode());
            ApplicationThreadLocals.setDistrictName(city.getDistrictName());
            ApplicationThreadLocals.setStateName(clientId);
            ApplicationThreadLocals.setGrade(city.getGrade());
            ApplicationThreadLocals.setDomainName(city.getDomainURL());
            // ApplicationThreadLocals.setDomainURL("https://"+city.getDomainURL());
        } else {
            LOG.warn("Unable to find the city");
        }
        CityPreferences cityPreferences = city.getPreferences();
        if (cityPreferences != null)
            ApplicationThreadLocals.setMunicipalityName(cityPreferences.getMunicipalityName());
        else
            LOG.warn("City preferences not set for {}", city.getName());
    }

    @Override
    public void destroy() {
        // Nothing to be destroyed
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to be initialized
    }
}
