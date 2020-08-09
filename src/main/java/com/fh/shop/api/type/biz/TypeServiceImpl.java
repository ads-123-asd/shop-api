package com.fh.shop.api.type.biz;

import com.fh.shop.api.common.ServerResponse;
import com.fh.shop.api.type.mapper.TypeMapper;
import com.fh.shop.api.type.po.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.Action;
import java.util.List;

@Service
public class TypeServiceImpl implements TypeService{
    @Autowired
    private TypeMapper typeMapper;

    @Override
    public List<Type> queryTypeList() {
        List<Type> typeList = typeMapper.selectList(null);
        return typeList;
    }
}
