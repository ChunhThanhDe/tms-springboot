package com.vnptt.tms.service;

import com.vnptt.tms.api.output.chart.AreaChartDeviceOnl;
import com.vnptt.tms.api.output.chart.AreaChartHisPerf;
import com.vnptt.tms.api.output.chart.BarChart;
import com.vnptt.tms.api.output.chart.PieChart;
import com.vnptt.tms.api.output.studio.TerminalStudioOutput;
import com.vnptt.tms.dto.DeviceDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IDeviceService {

    DeviceDTO save(DeviceDTO deviceDTO);

    DeviceDTO findOne(Long id);

    int totalItem();

    void delete(Long[] ids);

    List<DeviceDTO> findAll(Pageable pageable);

    List<DeviceDTO> findAll();

    DeviceDTO findOneBySn(String ip, String serialnumber);

    List<DeviceDTO> findByLocation(String location, Pageable pageable);

    List<DeviceDTO> findAllDeviceRunNow(Pageable pageable);

    ResponseEntity<?> authenticateDevice(String serialnumber, String mac);

    List<DeviceDTO> findDeviceActive(int day, long hour, int minutes, Pageable pageable);

    List<DeviceDTO> findAllWithApplication(Long applicationId, Pageable pageable);

    List<DeviceDTO> findAllDeviceRunApp(Long applicationId, Pageable pageable);

    List<DeviceDTO> mapDeviceToListDevice(Long listDeviceId, Long[] deviceIds);

    void removeDeviceinListDevice(Long listDeviceId, Long deviceId);

    List<DeviceDTO> findDeviceInListDevice(Long listDeviceId, Pageable pageable);

    TerminalStudioOutput updateTerminalStudioInfo();

    List<PieChart> getTotalPieChart(String type);

    List<BarChart> getTotalBarChart();

    DeviceDTO boxUpdate(String sn, DeviceDTO model);

    Long countDeviceRunNow();

    Long countDeviceActive(int day, long hour, int minutes);

    List<DeviceDTO> findByDescriptionAndSn(String search, Pageable pageable);

    Long countByDescriptionAndSn(String search);

    List<DeviceDTO> findByDescriptionAndLocation(String location, String description, Pageable pageable);

    Long countByDescriptionAndLocation(String location, String description);

    Long countByLocation(String location);

    List<AreaChartHisPerf> getAreaChartStatus(Integer dayAgo, Long id );

    List<DeviceDTO> findDeviceWithPolicyId(Long policyId, Pageable pageable);

    Long countDeviceWithPolicyId(Long policyId);

    List<DeviceDTO> findAllDeviceRunNowWithSN(String serialmunber, Pageable pageable);

    Long countDeviceRunNowWithSN(String serialmunber);

    Long countDeviceWithPolicyIdAndSn(Long policyId, String sn);

    List<DeviceDTO> findDeviceWithPolicyIdAndSN(Long policyId, String sn, Pageable pageable);

    List<DeviceDTO> findAllWithApplicationIdAndSn(Long applicationId, String sn, Pageable pageable);

    Long countByApplicationIdAndSn(Long applicationId, String sn);

    Long countByApplicationId(Long applicationId);

    List<DeviceDTO> findOnBarSearch(String search);

    List<DeviceDTO> findDeviceInListDeviceWithSn(Long listDeviceId, String serialmunber, Pageable pageable);

    Long countDeviceinListDeviceWithSn(Long listDeviceId, String serialmunber);

    Long countDeviceinListDevice(Long listDeviceId);

    List<AreaChartDeviceOnl> getAreaChartDeviceOnline();

    List<DeviceDTO> findAllDeviceRunAppWithSn(Long applicationId, String sn, Pageable pageable);

    Long countAppActiveByApplicationIdAndSn(Long applicationId, String sn);

    Long countAppactiveByApplicationId(Long applicationId);

    List<AreaChartDeviceOnl> getAreaChartDeviceTime(Long deviceId);
}
