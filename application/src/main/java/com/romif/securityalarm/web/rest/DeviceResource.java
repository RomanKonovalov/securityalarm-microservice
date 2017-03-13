package com.romif.securityalarm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.domain.ConfigStatus;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.domain.sms.DeliveryStatus;
import com.romif.securityalarm.domain.sms.Receipt;
import com.romif.securityalarm.repository.DeviceRepository;
import com.romif.securityalarm.security.AuthoritiesConstants;
import com.romif.securityalarm.security.SecurityUtils;
import com.romif.securityalarm.service.DeviceService;
import com.romif.securityalarm.service.SmsTxtlocalService;
import com.romif.securityalarm.service.StatusService;
import com.romif.securityalarm.service.dto.DeviceDTO;
import com.romif.securityalarm.service.dto.DeviceManagementDTO;
import com.romif.securityalarm.web.rest.util.HeaderUtil;
import com.romif.securityalarm.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Roman_Konovalov on 1/26/2017.
 */
@RestController
@RequestMapping("/api")
public class DeviceResource {

    private final Logger log = LoggerFactory.getLogger(DeviceResource.class);

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SmsTxtlocalService smsService;

    @Autowired
    private StatusService statusService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(DeliveryStatus.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(DeliveryStatus.getByCode(text));
            }
        });
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @PostMapping(Constants.HANDLE_RECEIPTS_PATH)
    @Timed
    public ResponseEntity<?> handleReceipt(Receipt receipt) {
        log.debug("REST request to handle Receipt");
        smsService.handleReceipt(receipt);
        return ResponseEntity.ok().build();
        // Receipt(number=375445486323, status=D, customID=, datetime=2017-02-23 21:49:56)
    }

    @GetMapping("/devices/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<DeviceManagementDTO> getDevice(@PathVariable String login) {
        log.debug("REST request to get Device : {}", login);

        return ResponseUtil.wrapOrNotFound(deviceService.getDevice(login));

    }

    @PostMapping("/devices/{login:" + Constants.LOGIN_REGEX + "}/login")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> loginDevice(@PathVariable String login) {
        log.debug("REST request to login Device: {}", login);
        boolean result = deviceService.activateDevice(login);
        return result ? ResponseEntity.ok().headers(HeaderUtil.createAlert("A Device is logged with login " + login, login)).build() :
            ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("device", "deviceError", "Error. Device is not logged"))
                .body(null);
    }

    @PostMapping("/devices/{login:" + Constants.LOGIN_REGEX + "}/logout")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> logoutDevice(@PathVariable String login) {
        log.debug("REST request to logout Device: {}", login);
        boolean result = deviceService.deactivateDevice(login);
        return result ? ResponseEntity.ok().headers(HeaderUtil.createAlert("A Device is logged out with login " + login, login)).build() :
            ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("device", "deviceError", "Error. Device is still logged"))
                .body(null);
    }

    @PostMapping("/devices/{login:" + Constants.LOGIN_REGEX + "}/config")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public CompletableFuture<ConfigStatus> configDevice(@PathVariable String login) {
        log.debug("REST request to config Device: {}", login);
        CompletableFuture<ConfigStatus> result = deviceService.configDevice(login);
        return result;
        /*return result ? ResponseEntity.ok().headers(HeaderUtil.createAlert( "Config has been sent to device: " + login, login)).build() :
            ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("device", "deviceError", "Error while sending config"))
                .body(null);*/
    }

    @GetMapping("/devices/{login:" + Constants.LOGIN_REGEX + "}/statuses")
    @Secured("ROLE_USER")
    @Timed
    @Transactional
    public ResponseEntity<List<Status>> getAllStatuses(@ApiParam Pageable pageable,
                                                       @RequestParam(required = false) ZonedDateTime startDate,
                                                       @RequestParam(required = false) ZonedDateTime endDate,
                                                       @PathVariable String login)
        throws URISyntaxException {
        log.debug("REST request to get a page of Statuses for Device {}", login);

        Optional<Device> device = deviceRepository.findOneByLogin(login);
        if (!device.isPresent() || device.get().getUser() == null || !SecurityUtils.getCurrentUserLogin().equals(device.get().getUser().getLogin())) {
            HttpHeaders headers = HeaderUtil.createAlert("A device is not found with login " + login, login);
            return new ResponseEntity<>(Collections.emptyList(), headers, HttpStatus.NOT_FOUND);
        }
        Page<Status> page = statusService.findAll(pageable, startDate, endDate, login);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/statuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/devices/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteDevice(@PathVariable String login) {
        log.debug("REST request to delete Device: {}", login);
        deviceService.deleteDevice(login);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert("A Device is deleted with identifier " + login, login)).build();
    }

    @GetMapping("/devices")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<DeviceDTO>> getAllDevices(Pageable pageable, @RequestParam(required = false) boolean free) throws URISyntaxException {

        Page<DeviceDTO> page = free ? deviceService.getAllFreeDevices(pageable) : deviceService.getAllDevices(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/devices");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/devices")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> createDevice(@RequestBody Device device) throws URISyntaxException {
        log.debug("REST request to save Device : {}", device);

        Device result = deviceService.createDevice(device);

        return ResponseEntity.created(new URI("/api/devices/" + result.getLogin()))
            .headers(HeaderUtil.createAlert("A Device is created with identifier " + result.getLogin(), result.getLogin()))
            .body(result);
    }

    @PutMapping("/devices")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<?> updateDevice(@RequestBody DeviceDTO device) {
        log.debug("REST request to update Device : {}", device);

        String login = SecurityUtils.getCurrentUserLogin();

        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN) ? deviceService.updateDevice(device) : deviceService.updateDevice(device, login)) {
            return ResponseEntity.ok()
                .headers(HeaderUtil.createAlert("A device is updated with name " + device.getDescription(), device.getDescription()))
                .build();
        } else {
            return ResponseEntity.notFound()
                .headers(HeaderUtil.createAlert("A device is not found with name " + device.getDescription(), device.getDescription()))
                .build();
        }

    }

    @GetMapping("/config")
    @Timed
    @Secured(AuthoritiesConstants.DEVICE)
    public ResponseEntity<?> getDeviceConfig() {
        String login = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        return deviceService.getConfig(login)
            .map(deviceConfigDTO -> new ResponseEntity<>(deviceConfigDTO, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

    }
}
