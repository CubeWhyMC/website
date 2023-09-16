package org.cubewhy.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.cubewhy.api.entity.Account;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
