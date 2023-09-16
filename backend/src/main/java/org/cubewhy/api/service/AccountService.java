package org.cubewhy.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cubewhy.api.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface AccountService extends UserDetailsService, IService<Account> {
    Account findAccountByNameOrEmail(String text);
}
