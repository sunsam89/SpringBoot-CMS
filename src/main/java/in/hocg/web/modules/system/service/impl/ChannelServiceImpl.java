package in.hocg.web.modules.system.service.impl;

import in.hocg.web.lang.CheckError;
import in.hocg.web.modules.base.Base2Service;
import in.hocg.web.modules.system.domain.Channel;
import in.hocg.web.modules.system.domain.repository.ChannelRepository;
import in.hocg.web.modules.system.filter.ChannelFilter;
import in.hocg.web.modules.system.service.ArticlesService;
import in.hocg.web.modules.system.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by hocgin on 2017/12/4.
 * email: hocgin@gmail.com
 */
@Service
public class ChannelServiceImpl
        extends Base2Service<Channel, String, ChannelRepository> implements ChannelService {
    
    
    private ArticlesService articlesService;
    
    @Autowired
    public ChannelServiceImpl(@Lazy ArticlesService articlesService) {
        this.articlesService = articlesService;
    }
    
    @Override
    public List<Channel> queryRoot() {
        return repository.findAllByParentInOrderByLocationAscAndPathAsc(null, "");
    }
    
    
    @Override
    public List<Channel> queryChildren(String parentId) {
        return repository.findAllByParentInOrderByLocationAscAndPathAsc(parentId);
    }
    
    @Override
    public void insert(ChannelFilter filter, CheckError checkError) {
        Channel channel = filter.get();
        String path = "";
        // 检测上级栏目是否存在
        if (!StringUtils.isEmpty(channel.getParent())) {
            Channel parent = repository.findOne(channel.getParent());
            if (ObjectUtils.isEmpty(parent)) {
                checkError.putError("上级栏目不存在");
                return;
            }
            path = StringUtils.isEmpty(parent.getPath()) ? "" : parent.getPath();
            parent.setHasChildren(true);
            repository.save(parent);
        }
        channel.setPath(getSubPath(path));
        repository.save(channel);
    }
    
    @Override
    public void update(ChannelFilter filter, CheckError checkError) {
        Channel channel = repository.findOne(filter.getId());
        // 检测是否存在
        if (ObjectUtils.isEmpty(channel)) {
            checkError.putError("更新的栏目异常");
            return;
        }
        repository.save(filter.update(channel));
    }
    
    @Override
    public void available(String id, boolean available) {
        Channel channel = repository.findOne(id);
        if (!ObjectUtils.isEmpty(channel)) {
            channel.setAvailable(available);
            channel.updatedAt();
            repository.save(channel);
        }
    }
    
    @Override
    public void delete(String id, CheckError checkError) {
        Channel channel = repository.findOne(id);
        if (ObjectUtils.isEmpty(channel)) {
            return;
        }
        // 查处此栏目 及 子栏目
        List<Channel> all = repository.findAllByPathRegex(String.format("%s.*", (StringUtils.isEmpty(channel.getPath()) ? "" : channel.getPath())));
        String[] ids = all
                .stream()
                .map(Channel::getId)
                .toArray(String[]::new);
        // 删除此栏目 及 子栏目
        repository.deleteAllByIdIn(ids);
        // 判断是否把父节点设置为根结点
        if (!StringUtils.isEmpty(channel.getParent())
                && repository.existsByParent(channel.getParent())) {
            repository.updateHasChildren(channel.getParent(), false);
        }
        
        // 删除 此栏目 及 子栏目 关联文章
        articlesService.deletesByChannel(ids);
    }
    
    @Override
    public List<Channel> queryAllOrderByLocationAscAndPathAsc() {
        return repository.findAllOrderByLocationAscAndPathAsc();
    }
    
    
    @Override
    public void sort(String... ids) {
        repository.updateLocation(ids);
    }
    
    @Override
    public Channel findOneByAlias(String alias) {
        return repository.findTopByAliasIsAndAvailableIs(alias, true);
    }
    
    
    /**
     * 获取子路径结构算法
     *
     * @param parentPath
     * @return
     */
    public String getSubPath(String parentPath) {
        List<Channel> all = repository.findAllByPathRegexOrderByPathDesc(String.format("^%s.{4}$", parentPath));
        String rsvalue = parentPath + "0001";
        if (all.size() > 0) {
            rsvalue = all.get(0).getPath();
            int newvalue = Integer.parseInt(rsvalue.substring(rsvalue.length() - 4)) + 1;
            rsvalue = rsvalue.substring(0, rsvalue.length() - 4)
                    + new java.text.DecimalFormat("0000")
                    .format(newvalue);
        }
        return rsvalue;
    }
}
