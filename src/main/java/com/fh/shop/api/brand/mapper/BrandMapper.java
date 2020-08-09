package com.fh.shop.api.brand.mapper;

import com.fh.shop.api.brand.po.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface BrandMapper {

    @Insert("insert into t_brand(brandName) values(#{brandName})")
    void addbrand(Brand brand);

    @Select("select b.*,b.brandName brandName from t_brand b")
    List<Brand> queryBrand();

    @Delete("delete from t_brand where id=#{v}")
    void delete(Long id);

    @Update("update t_brand set brandName=#{brandName} where id=#{id}")
    void update(Brand brand);


    void deleteBatch(List<Long> idList);

}
