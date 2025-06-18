package com.example.rearend.service.impl;

import com.example.rearend.mapper.MitochondrialDetailMapper;
import com.example.rearend.mapper.RecordsMapper;
import com.example.rearend.model.MitochondrialDetail;
import com.example.rearend.model.Records;
import com.example.rearend.model.SiteInfo;
import com.example.rearend.service.RecordsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * RecordsService 接口的实现类，负责处理与 Records 相关的业务逻辑。
 */
@Service
public class RecordsServiceImpl implements RecordsService {
    private final RecordsMapper mapper;
    private final MitochondrialDetailMapper detailMapper;

    /**
     * 构造函数，注入 RecordsMapper 和 MitochondrialDetailMapper。
     *
     * @param mapper       用于操作 Records 数据的 Mapper
     * @param detailMapper 用于操作 MitochondrialDetail 数据的 Mapper
     */
    public RecordsServiceImpl(RecordsMapper mapper, MitochondrialDetailMapper detailMapper) {
        this.mapper = mapper;
        this.detailMapper = detailMapper;
    }

    /**
     * 根据目标名称查找 Records 列表。
     *
     * @param goalName 目标名称
     * @return 符合条件的 Records 列表
     */
    @Override
    public List<Records> findByGoalName(String goalName) {
        return mapper.findByGoalName(goalName);
    }

    /**
     * 插入一条 Records 记录。
     *
     * @param records 要插入的 Records 对象
     * @return 插入操作影响的行数
     */
    @Override
    public int insert(Records records) {
        return mapper.insert(records);
    }

    /**
     * 进行记录比较并创建新的 Records 记录。
     *
     * @param list      站点信息列表
     * @param mit       线粒体详细信息对象
     * @param detail    另一个线粒体详细信息对象
     * @param list2     另一个站点信息列表，如果为空则从数据库获取
     * @return 创建的 Records 对象
     */
    @Override
    public Records Comparison(List<SiteInfo> list, MitochondrialDetail mit, MitochondrialDetail detail, List<SiteInfo> list2) {
        Records records = new Records();
        List<SiteInfo> siteInfoList;
        if (list2.size() == 0) {
            siteInfoList = detailMapper.getMitochondrialDetailDetails(detail.getOriginal_data_name());
        } else {
            siteInfoList = list2;
        }
        int num = calculateMatchingCount(siteInfoList, list);
        int result = calculateAllowance(siteInfoList, list, num);

        setupRecord(records, mit, detail, result);
        insert(records);
        return records;
    }

    /**
     * 进行记录比较，根据条件创建新的 Records 记录。
     *
     * @param list   站点信息列表
     * @param mit    线粒体详细信息对象
     * @param detail 另一个线粒体详细信息对象
     * @return 创建的 Records 对象，如果不满足条件则返回空属性的 Records 对象
     */
    @Override
    public Records Compare(List<SiteInfo> list, MitochondrialDetail mit, MitochondrialDetail detail,int num1) {
        Records records = new Records();
        List<SiteInfo> siteInfoList = detailMapper.getMitochondrialDetailDetails(detail.getOriginal_data_name());

        int num = calculateMatchingCount(siteInfoList, list);
        int result = calculateAllowance(siteInfoList, list, num);

        if (shouldCreateRecord(result, siteInfoList, list, num1)) {
            setupRecord(records, mit, detail, result);
            insert(records);
        }
        return records;
    }

    /**
     * 计算两个站点信息列表的匹配数量。
     *
     * @param siteInfoList 第一个站点信息列表
     * @param list         第二个站点信息列表
     * @return 匹配的数量
     */
    private int calculateMatchingCount(List<SiteInfo> siteInfoList, List<SiteInfo> list) {
        int num = 0;
        boolean flag = false;
        for (SiteInfo siteInfo : siteInfoList) {
            for (SiteInfo info : list) {
                if (!siteInfo.getOriginal_data_name().equals(info.getOriginal_data_name())) {
                    flag = true;
                    if (Objects.equals(siteInfo.getBase_position(), info.getBase_position())) {
                        if (siteInfo.getMutant_base().equals(info.getMutant_base())) {
                            num++;
                            continue;
                        }
                        num++;
                    }
                } else {
                    flag = false;
                }
            }
        }
        return num;
    }

    /**
     * 计算允许的差异数量。
     *
     * @param siteInfoList 第一个站点信息列表
     * @param list         第二个站点信息列表
     * @param num          匹配的数量
     * @return 允许的差异数量
     */
    private int calculateAllowance(List<SiteInfo> siteInfoList, List<SiteInfo> list, int num) {
        return siteInfoList.size() + list.size() - (num*2);
    }

    /**
     * 判断是否应该创建新的 Records 记录。
     *
     * @param result       允许的差异数量
     * @param siteInfoList 第一个站点信息列表
     * @param list         第二个站点信息列表
     * @param num          允许的最大差异数量
     * @return 如果满足条件则返回 true，否则返回 false
     */
    private boolean shouldCreateRecord(int result, List<SiteInfo> siteInfoList, List<SiteInfo> list, int num) {
        boolean flag = false;
        for (SiteInfo siteInfo : siteInfoList) {
            for (SiteInfo info : list) {
                if (!siteInfo.getOriginal_data_name().equals(info.getOriginal_data_name())) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }
        return result >= num && flag;
    }

    /**
     * 设置 Records 对象的属性。
     *
     * @param records 要设置属性的 Records 对象
     * @param mit     线粒体详细信息对象
     * @param detail  另一个线粒体详细信息对象
     * @param result  允许的差异数量
     */
    private void setupRecord(Records records, MitochondrialDetail mit, MitochondrialDetail detail, int result) {
        records.setAllowance(result);
        records.setTime(LocalDateTime.now());
        records.setGoal_name(mit.getSample_name());
        records.setCompare_name(detail.getSample_name());
        records.setOriginal_goal(mit.getOriginal_data_name());
        records.setOriginal_compare(detail.getOriginal_data_name());
    }

    /**
     * 获取所有的 Records 记录。
     *
     * @return 所有的 Records 记录列表
     */
    @Override
    public List<Records> findAll() {
        return mapper.findAll();
    }
}