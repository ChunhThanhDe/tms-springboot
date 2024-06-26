package com.vnptt.tms.service;

import com.vnptt.tms.api.output.chart.PieChart;
import com.vnptt.tms.dto.PolicyDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPolicyService {
    PolicyDTO save(PolicyDTO policyDTO);

    PolicyDTO findOne(Long id);

    int totalItem();

    void delete(Long[] ids);

    List<PolicyDTO> findAll(Pageable pageable);

    List<PolicyDTO> findAll();

    List<PolicyDTO> findAllWithCommand(Long commandId);

    List<PolicyDTO> findAllWithApk(Long apkId);

    List<PolicyDTO> findAllWithDeviceId(Long deviceId, Pageable pageable);

    PolicyDTO updateStatus(Long id, int status);

    List<PolicyDTO> findwithPolicyname(String policyname, Pageable pageable);

    Long totalCountByPolicynameContain(String packagename);

    Long totalCountByDeviceId(Long deviceId);

    List<PolicyDTO> findAllWithDeviceIdAndPolicyName(Long deviceId, String policyname, Pageable pageable);

    Long countAllByDeviceIdAndPolicyName(Long deviceId, String policyname);

    List<PieChart> getTotalPieChart(String type);
}
