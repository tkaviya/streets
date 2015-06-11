package net.blaklizt.streets.core.authentication;

import net.blaklizt.streets.persistence.User;
import net.blaklizt.streets.persistence.UserGroupRole;
import net.blaklizt.streets.persistence.dao.UserDao;
import net.blaklizt.streets.persistence.dao.UserGroupRoleDao;
import net.blaklizt.symbiosis.sym_authentication.security.Security;
import net.blaklizt.symbiosis.sym_common.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* SymbiosisUser: tkaviya
* Date: 8/6/13
* Time: 7:06 PM
*/
@Service
@Transactional(readOnly=true)
public class StreetAuthenticator implements UserDetailsService, PasswordEncoder {
	protected HashMap<String, List<SimpleGrantedAuthority>> grantedAuthoritiesCache = new HashMap<>();

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserGroupRoleDao userGroupRoleDao;

	private static final Logger log4j = Configuration.getNewLogger(StreetAuthenticator.class.getSimpleName());

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		log4j.info("Logging in user: " + username);
		User dbUser = userDao.findByUsername(username);

		if (dbUser == null) throw new UsernameNotFoundException("Could not find username " + username);

		boolean active;

		if (dbUser.getStatus() == User.UserStatus.ACTIVE.getValue()) active = true;
		else
		{
			active = false;
			log4j.warn("Cannot login " + dbUser.getUsername() + " : Account is not active.");
		}

		return new org.springframework.security.core.userdetails.User(username, dbUser.getPassword(),
				active, active, active, active, getAuthorities(dbUser.getUserGroupID()));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(String userGroup)
	{
		List<SimpleGrantedAuthority> authList = new ArrayList<>();

		if (!grantedAuthoritiesCache.containsKey(userGroup))
		{
			log4j.debug("Getting authorities for access group " + userGroup);

			List<UserGroupRole> userGroupRoles = userGroupRoleDao.findByUserGroup(userGroup);

			for (UserGroupRole userGroupRole : userGroupRoles)
			{
				log4j.debug("Caching role " + userGroupRole.getRoleID());
				authList.add(new SimpleGrantedAuthority(userGroupRole.getRoleID()));
			}

			//cache the authorities to avoid future db hits.
			grantedAuthoritiesCache.put(userGroup, authList);
		}
		return grantedAuthoritiesCache.get(userGroup);
	}

	@Override
	public String encodePassword(String rawPass, Object salt) {
		//implement hectic encryption here
		log4j.info("Encrypting [ " + rawPass + " with salt " + salt + " ]");
		return new String(Security.encrypt(rawPass.getBytes()));
	}

	@Override
	public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
		//implement hectic encryption here
		log4j.info("Comparing [ " + new String(Security.encrypt(rawPass.getBytes())) + " | " + rawPass + " ]");
		return encPass.matches(new String(Security.encrypt(rawPass.getBytes())));
	}
}
