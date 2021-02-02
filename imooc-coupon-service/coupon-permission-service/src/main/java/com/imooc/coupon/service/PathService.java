package com.imooc.coupon.service;

import com.imooc.coupon.dao.PathRepository;
import com.imooc.coupon.entity.Path;
import com.imooc.coupon.vo.CreatePathRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>路径相关的服务功能实现</h1>
 * @Author: crowsjian
 * @Date: 2020/6/26 20:42
 */
@Slf4j
@Service
public class PathService {
    /*Path Repository*/
    private final PathRepository pathRepository;

    public PathService(PathRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    /**
     * <h2>添加新的path 到数据表中</h2>
     * @param request{@link CreatePathRequest}
     * @return Path 数据记录的主键
     */
    public List<Integer> createPath(CreatePathRequest request){
        List<CreatePathRequest.PathInfo> pathInfos = request.getPathInfos();
        List<CreatePathRequest.PathInfo> validRequests =
                new ArrayList<>(request.getPathInfos().size());
        List<Path> currentPaths = pathRepository.findAllByServiceName(
                pathInfos.get(0).getServiceName()
        );
        if(!CollectionUtils.isEmpty(currentPaths)){
            for (CreatePathRequest.PathInfo pathInfo : pathInfos) {
                boolean isValid = true;
                for (Path currentPath : currentPaths) {
                    if(currentPath.getPathParttern().equals(pathInfo.getPathPattern())
                    &&currentPath.getHttpMethod().equals(pathInfo.getHttpMethod())){
                        isValid = false;
                        break;
                    }
                }
                if(isValid){
                    validRequests.add(pathInfo);
                }
            }
        }else{
            validRequests = pathInfos;
        }
        List<Path> paths = new ArrayList<>(validRequests.size());
        validRequests.forEach(p->paths.add(new Path(
                p.getPathPattern(),
                p.getHttpMethod(),
                p.getPathName(),
                p.getServiceName(),
                p.getOpMode()
        )));
        return pathRepository.saveAll(paths).stream().map(Path::getId)
                .collect(Collectors.toList());
    }
}
