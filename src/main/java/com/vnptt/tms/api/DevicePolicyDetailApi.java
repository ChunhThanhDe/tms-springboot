package com.vnptt.tms.api;

import com.vnptt.tms.api.output.chart.PieChart;
import com.vnptt.tms.api.output.table.DevicePolicyDetailOutput;
import com.vnptt.tms.api.output.table.PolicyOutput;
import com.vnptt.tms.dto.DevicePolicyDetailDTO;
import com.vnptt.tms.repository.DevicePolicyDetailRepository;
import com.vnptt.tms.service.IDevicePolicyDetailnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("TMS/api")
public class DevicePolicyDetailApi {

    @Autowired
    private IDevicePolicyDetailnService devicePolicyDetailService;
    @Autowired
    private DevicePolicyDetailRepository devicePolicyDetailRepository;


    /**
     * unnecessary (Only use to test)
     *
     * @return
     */
    @GetMapping(value = "/devicePolicyDetail")
    public DevicePolicyDetailOutput showDevicePolicyDetail() {
        DevicePolicyDetailOutput result = new DevicePolicyDetailOutput();
        result.setListResult(devicePolicyDetailService.findAll());

        if (result.getListResult().size() >= 1) {
            result.setMessage("Request Success");
            result.setTotalElement(result.getListResult().size());
        } else {
            result.setMessage("no matching element found");
        }

        return result;
    }

    /**
     * api find devicePolicyDetail with id
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/devicePolicyDetail/{id}")
    public DevicePolicyDetailDTO showDevicePolicyDetail(@PathVariable("id") Long id) {
        return devicePolicyDetailService.findOne(id);
    }

    /**
     * api get list PolicyDetail math with device to check policies unfinished for box
     * <p>
     * status of PolicyDetail
     * status 0 = not run
     * status 1 = run
     * status 2 = success
     * status 3 = error
     *
     * @param deviceId
     * @return
     */
    @GetMapping(value = "/box/{deviceId}/devicePolicyDetail")
    public PolicyOutput showAllPolicyDetailOfDeviceRunOrError(@PathVariable(value = "deviceId") Long deviceId) {
        PolicyOutput result = new PolicyOutput();
        result.setListResult(devicePolicyDetailService.findAllWithDeviceAndStatusRun(deviceId));

        if (result.getListResult().size() >= 1) {
            result.setMessage("Request Success");
            result.setTotalElement(result.getListResult().size());
        } else {
            result.setMessage("no matching element found");
        }
        return result;
    }

    /**
     * api get list PolicyDetail math for Web
     * <p>
     * status of PolicyDetail
     * status 0 = not run
     * status 1 = run
     * status 2 = running
     * status 3 = success
     * status 4 = error
     *
     * @param deviceId
     * @return
     */
    @GetMapping(value = "/device/{deviceId}/devicePolicyDetail")
    public DevicePolicyDetailOutput showAllPolicyDetailOfDevice(@PathVariable(value = "deviceId") Long deviceId,
                                                                @RequestParam(value = "status", required = false) Integer status,
                                                                @RequestParam(value = "page") Integer page,
                                                                @RequestParam(value = "limit") Integer limit) {
        DevicePolicyDetailOutput result = new DevicePolicyDetailOutput();

        if (status == null) {
            result.setPage(page);
            Pageable pageable = PageRequest.of(page - 1, limit);
            result.setListResult(devicePolicyDetailService.findAllWithDevice(deviceId, pageable));
            result.setTotalPage((int) Math.ceil((double) devicePolicyDetailService.countAllWithDevice(deviceId) / limit));
        } else if (status == 1 || status == 2 || status == 3 || status == 0) {
            result.setPage(page);
            Pageable pageable = PageRequest.of(page - 1, limit);
            result.setListResult(devicePolicyDetailService.findAllWithDeviceAndStatus(deviceId, status, pageable));
            result.setTotalPage((int) Math.ceil((double) devicePolicyDetailService.countAllWithDeviceAndStatus(deviceId, status) / limit));
        } else {
            throw new RuntimeException("status must be 1, 2, 3, 0");
        }

        if (result.getListResult().size() >= 1) {
            result.setMessage("Request Success");
            result.setTotalElement(result.getListResult().size());
        } else {
            result.setMessage("no matching element found");
        }
        return result;
    }

    /**
     * api get list PolicyDetail math with device to check policies detail unfinished for policy
     * <p>
     * status of PolicyDetail
     * status 0 = not run
     * status 1 = run
     * status 2 = success
     * status 3 = error
     * <p>
     *
     * @param
     * @return
     */
    @GetMapping(value = "/policy/{policyId}/devicePolicyDetail")
    public DevicePolicyDetailOutput showAllPolicyDetailOfPolicy(@PathVariable(value = "policyId") Long policyId,
                                                                @RequestParam(value = "status", required = false) Integer status,
                                                                @RequestParam(value = "page") Integer page,
                                                                @RequestParam(value = "limit") Integer limit) {
        DevicePolicyDetailOutput result = new DevicePolicyDetailOutput();

        if (status == null) {
            result.setPage(page);
            Pageable pageable = PageRequest.of(page - 1, limit);
            result.setListResult(devicePolicyDetailService.findAllWithPolicy(policyId, pageable));
            result.setTotalPage((int) Math.ceil((double) devicePolicyDetailService.countAllWithPolicy(policyId) / limit));
        } else if (status == 1 || status == 2 || status == 3 || status == 0) {
            result.setPage(page);
            Pageable pageable = PageRequest.of(page - 1, limit);
            result.setListResult(devicePolicyDetailService.findAllWithPolicy(policyId, status, pageable));
            result.setTotalPage((int) Math.ceil((double) devicePolicyDetailService.countAllWithPolicyStatus(policyId, status) / limit));
        } else {
            throw new RuntimeException("status must be 1, 2, 3, 0");
        }

        if (result.getListResult().size() >= 1) {
            result.setMessage("Request Success");
            result.setTotalElement(result.getListResult().size());
        } else {
            result.setMessage("no matching element found");
        }
        return result;
    }

    /**
     * api Show device Pass of Policy
     * todo add ui for this
     *
     * @return
     */
    @GetMapping(value = "/chart/pie/policy/{policyId}")
    public List<PieChart> showTotalPolicyDetailStatus(@PathVariable(name = "policyId") Long policyId) {
        List<PieChart> result = devicePolicyDetailService.getTotalPieChart(policyId);
        return result;
    }

    /**
     * api create policy detail for devices web
     * <p>
     * status of policy detail
     * status = 0 not run
     *
     * @param policyId  id of policy
     * @param deviceIds list id device
     * @return
     */
    @PostMapping(value = "/policy/{policyId}/devicePolicyDetail")
    public DevicePolicyDetailOutput createDevicePolicyDetail(HttpServletRequest request,
                                                             @PathVariable(value = "policyId") Long policyId,
                                                             @RequestBody Long[] deviceIds) {
        DevicePolicyDetailOutput output = new DevicePolicyDetailOutput();
        output.setListResult(devicePolicyDetailService.save(request, deviceIds, policyId));

        if (output.getListResult().size() >= 1) {
            output.setMessage("Request Success");
            output.setTotalElement(output.getListResult().size());
        } else {
            output.setMessage("no device had choose");
        }
        return output;
    }

    /**
     * api create policy detail for device web
     * <p>
     * status of policy detail
     * status = 0 not run
     *
     * @param policyId     id of policy
     * @param listDeviceId id device of List Device
     * @return
     */
    @PostMapping(value = "/policy/{policyId}/listDevice/{listDeviceId}/devicePolicyDetail")
    public DevicePolicyDetailOutput createDevicePolicyDetailForList(HttpServletRequest request,
                                                                    @PathVariable(value = "policyId") Long policyId,
                                                                    @PathVariable(value = "listDeviceId") Long listDeviceId) {

        DevicePolicyDetailOutput output = new DevicePolicyDetailOutput();
        output.setListResult(devicePolicyDetailService.save(request, listDeviceId, policyId));

        if (output.getListResult().size() >= 1) {
            output.setMessage("Request Success");
            output.setTotalElement(output.getListResult().size());
        } else {
            output.setMessage("no device had choose");
        }
        return output;
    }

    /**
     * api update status of policy detail for box
     * <p>
     * status of PolicyDetail
     * status 0 = not run
     * status 1 = run
     * status 2 = running
     * status 3 = success
     * status 4 = error
     *
     * @param status
     * @param id
     * @return
     */
    @PutMapping(value = "/devicePolicyDetail/{id}")
    public DevicePolicyDetailDTO updateDevicePolicyDetail(@RequestParam(value = "status") int status,
                                                          @PathVariable("id") Long id) {
        return devicePolicyDetailService.update(id, status);
    }

    /**
     * api Remove device in policy
     *
     * @return https 200
     */
    @DeleteMapping(value = "/device/{deviceId}/policy/{policyId}/devicePolicyDetail")
    public ResponseEntity<HttpStatus> removeDevicePolicyDetailWithDeviceAndPolicy(@PathVariable(value = "policyId") Long policyId,
                                                                                  @PathVariable(value = "deviceId") Long deviceId) {
        devicePolicyDetailService.removeDevicePolicyDetailWithDeviceAndPolicy(policyId, deviceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * dangerous (only use to test)
     *
     * @param ids
     */
    @DeleteMapping(value = "/devicePolicyDetail")
    @PreAuthorize("hasRole('MODERATOR')")
    public void removeDevicePolicyDetail(@RequestBody Long[] ids) {
        devicePolicyDetailService.delete(ids);
    }
}
