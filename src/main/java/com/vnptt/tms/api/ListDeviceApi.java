package com.vnptt.tms.api;


import com.vnptt.tms.api.output.DeviceOutput;
import com.vnptt.tms.api.output.ListDeviceOutput;
import com.vnptt.tms.dto.ListDeviceDTO;
import com.vnptt.tms.service.IListDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("TMS/api")
public class ListDeviceApi {

    @Autowired
    private IListDeviceService listDeviceService;

    /**
     * Get List device for web
     *
     * @param page  desired page to display
     * @param limit number of elements 1 page
     * @return List app DTO
     */
    @GetMapping(value = "/listDevice")
    public ListDeviceOutput showListDevice(@RequestParam(value = "page", required = false) Integer page,
                                           @RequestParam(value = "limit", required = false) Integer limit) {
        ListDeviceOutput result = new ListDeviceOutput();
        if (page != null && limit != null) {
            result.setPage(page);
            Pageable pageable = PageRequest.of(page - 1, limit);
            result.setListResult((listDeviceService.findAll(pageable)));
            result.setTotalPage((int) Math.ceil((double) listDeviceService.totalItem() / limit));
        } else {
            result.setListResult(listDeviceService.findAll());
        }

        if (result.getListResult().size() >= 1) {
            result.setMessage("Get List ListDevice success");
            result.setTotalElement(result.getListResult().size());
        } else {
            result.setMessage("no matching element found");
        }

        return result;
    }

    /**
     * show application with id
     *
     * @param id id of app want to show
     * @return app DTO
     */
    @GetMapping(value = "/listDevice/{id}")
    public ListDeviceDTO showListDevice(@PathVariable("id") Long id) {
        return listDeviceService.findOne(id);
    }

    /**
     * get list device in roles management
     *
     * @return
     */
    @GetMapping(value = "/roleManagement/{roleManagementId}/listDevice")
    public ListDeviceOutput showListDeviceInRoles(@PathVariable(name = "roleManagementId") Long roleManagementId) {
        ListDeviceOutput result = new ListDeviceOutput();
        result.setListResult(listDeviceService.findListDeviceInRoleManagement(roleManagementId));

        if (result.getListResult().size() >= 1) {
            result.setMessage("Request Success");
            result.setTotalElement(result.getListResult().size());
        } else {
            result.setMessage("no matching element found");
        }
        return result;
    }

    /**
     * add new app to database
     *
     * @return http status ok 200
     */
    @PostMapping(value = "listDevice")
    public ResponseEntity<ListDeviceDTO> createListDevice(@RequestBody ListDeviceDTO listDeviceDTO) {

        return new ResponseEntity<>(listDeviceService.save(listDeviceDTO), HttpStatus.OK);
    }


    /**
     * Map List Device to roles Management
     *
     * @param roleManagementId id of role
     * @param listDeviceId     id of list Device
     * @return
     */
    @PostMapping(value = "/roleManagement/{roleManagementId}/listDevice/{listDeviceId}")
    public ListDeviceDTO addListDeviceToRoleManagement(@PathVariable(value = "roleManagementId") Long roleManagementId,
                                                       @PathVariable(value = "listDeviceId") Long listDeviceId) {
        return listDeviceService.addListDeviceToRolesManagement(roleManagementId, listDeviceId);
    }


    /**
     * Edit list device info
     *
     * @param listDeviceId
     * @param listDeviceDTO
     * @return
     */
    @PutMapping(value = "/listDevice/{listDeviceId}")
    public ResponseEntity<ListDeviceDTO> updateListDevice(@PathVariable(name = "listDeviceId") Long listDeviceId,
                                                          @RequestBody ListDeviceDTO listDeviceDTO) {
        listDeviceDTO.setId(listDeviceId);
        return new ResponseEntity<>(listDeviceService.save(listDeviceDTO), HttpStatus.OK);
    }

    /**
     * remove map list device of roles management
     *
     * @param roleManagementId
     * @param listDeviceId
     * @return
     */
    @DeleteMapping(value = "/roleManagement/{roleManagementId}/listDevice/{listDeviceId}")
    public ResponseEntity<HttpStatus> removeListDeviceInManagement(@PathVariable(value = "roleManagementId") Long roleManagementId,
                                                                   @PathVariable(value = "listDeviceId") Long listDeviceId) {
        listDeviceService.removeListDeviceInManagement(roleManagementId, listDeviceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * unnecessary (only use to test)
     *
     * @param ids list id of app want to delete ex: [1,2,3]
     */
    @DeleteMapping(value = "/ListDevice")
    @PreAuthorize("hasRole('MODERATOR')")
    public void deleteListDevice(@RequestBody Long[] ids) {
        listDeviceService.delete(ids);
    }
}