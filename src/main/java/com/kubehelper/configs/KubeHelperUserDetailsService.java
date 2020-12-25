/*
Kube Helper
Copyright (C) 2021 JDev

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.kubehelper.configs;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author JDev
 */
@Service
public class KubeHelperUserDetailsService implements UserDetailsService {

    @Value("#{environment.KUBE_HELPER_UI_USERNAME}")
    private String username;

    @Value("#{environment.KUBE_HELPER_UI_PASSWORD}")
    private String password;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {

        if (StringUtils.isAnyBlank(username, password)) {
            throw new UsernameNotFoundException("User not found.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        return new KubeHelperUser(username, encodedPassword);
    }
}
